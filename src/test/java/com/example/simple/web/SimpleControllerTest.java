package com.example.simple.web;

import com.example.simple.domain.Simple;
import com.example.simple.service.SimpleService;
import com.example.simple.web.response.SimpleResponse;
import com.github.d4rk3on.spring.mvc.model.Error;
import com.github.d4rk3on.spring.mvc.model.response.GlobalExceptionResponse;
import com.github.d4rk3on.spring.mvc.util.ErrorEnum;
import com.github.d4rk3on.spring.mvc.util.exception.FunctionalException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimpleControllerTest {

    private static final String PATH = "/example";
    private static final List<Simple> SIMPLE_LIST_OK = List.of(
            Simple.builder().id("id_001").simpleId("01").name("Domino").build(),
            Simple.builder().id("id_002").simpleId("02").name("Cable").build(),
            Simple.builder().id("id_003").simpleId("03").name("Psylocke").build(),
            Simple.builder().id("id_004").simpleId("04").name("Colossus").build()
    );

    @MockBean
    private SimpleService simpleService;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void findAllSimpleWhenOk() {
        when(simpleService.findAllSimple(Optional.empty(), Optional.empty(), Optional.empty()))
                .thenReturn(SIMPLE_LIST_OK);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(PATH);

        var response = testRestTemplate.getForEntity(builder.build().toUri(), SimpleResponse.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertTrue(response.getBody() != null && !response.getBody().getSimpleList().isEmpty()),
                () -> assertEquals(4, response.getBody().getSimpleList().size()),
                () -> assertNotEquals(SimpleResponse.builder().simpleList(SIMPLE_LIST_OK).build(), response.getBody()),
                () -> assertEquals(SimpleResponse.builder().simpleList(List.of(
                        Simple.builder().id("01").name("Domino").build(),
                        Simple.builder().id("02").name("Cable").build(),
                        Simple.builder().id("03").name("Psylocke").build(),
                        Simple.builder().id("04").name("Colossus").build()
                )).build(), response.getBody())
        );
    }

    @Test
    void findAllSimpleWhenNoDataFound() {
        when(simpleService.findAllSimple(Optional.empty(), Optional.empty(), Optional.empty()))
                .thenReturn(Collections.EMPTY_LIST);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(PATH);

        final var response = testRestTemplate.getForEntity(builder.build().toUri(), SimpleResponse.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertTrue(response.getBody() != null && response.getBody().getSimpleList().isEmpty()),
                () -> assertEquals(0, response.getBody().getSimpleList().size()),
                () -> assertEquals(SimpleResponse.builder().simpleList(Collections.EMPTY_LIST).build(), response.getBody())
        );
    }

    @Test
    void findSimpleByIdWhenOk() {
        when(simpleService.findSimpleById("01"))
                .thenReturn(Simple.builder()
                        .id("id_001")
                        .simpleId("01")
                        .name("Domino")
                        .build());

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(PATH.concat("/01"));

        var response = testRestTemplate.getForEntity(builder.build().toUri(), Simple.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(Simple.builder().id("01").name("Domino").build(), response.getBody())
        );
    }

    @Test
    void findSimpleByIdWhenNoDataFound() {
        when(simpleService.findSimpleById("00"))
                .thenThrow(new FunctionalException(
                        "Not valid findBySimpleId response",
                        ErrorEnum.NO_DATA_FOUND,
                        "ID [00] not exist"));

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(PATH.concat("/00"));

        final var response = testRestTemplate.getForEntity(builder.build().toUri(), GlobalExceptionResponse.class);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(
                        GlobalExceptionResponse.builder()
                                .errors(
                                        Collections.singletonList(
                                                Error.builder()
                                                        .httpStatus(ErrorEnum.NO_DATA_FOUND.getHttpStatus())
                                                        .cause(ErrorEnum.NO_DATA_FOUND.getMessage())
                                                        .message("Not valid findBySimpleId response >>> ID [00] not exist")
                                                        .build()
                                        )
                                )
                                .build(),
                        response.getBody())
        );
    }

    @Test
    void saveSimpleWhenOk() {
        doNothing().when(simpleService).saveSimple(eq("01"), any(Simple.class));

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(PATH.concat("/01"));

        final HttpEntity<Map<String, String>> request = new HttpEntity<>(
                new HashMap<>() {
                    {
                        put("name", "Testing Name");
                    }
                }
        );

        final var response = testRestTemplate
                .exchange(builder.build().toUri(), HttpMethod.PUT, request, ResponseEntity.class);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED, response.getStatusCode()),
                () -> assertNull(response.getBody()),
                () -> assertNotNull(response.getHeaders()),
                () -> assertFalse(response.getHeaders().isEmpty()),
                () -> assertNotNull(response.getHeaders().get("Location")),
                () -> assertFalse(Objects.requireNonNull(response.getHeaders().get("Location")).isEmpty()),
                () -> assertEquals("/example/01", Objects.requireNonNull(response.getHeaders().get("Location")).get(0))
        );
    }

    @Test
    void saveSimpleWhenIdAlreadyExist() {
        doThrow(
                new FunctionalException(
                        "Testing duplicate key exception in saveSimple method",
                        ErrorEnum.CONFLICT,
                        "Index <simpleId> : duplicate key [01]")
        ).when(simpleService).saveSimple(eq("01"), any(Simple.class));

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(PATH.concat("/01"));

        final HttpEntity<Map<String, String>> request = new HttpEntity<>(
                new HashMap<>() {
                    {
                        put("name", "Testing Name");
                    }
                }
        );

        final var response = testRestTemplate
                .exchange(builder.build().toUri(), HttpMethod.PUT, request, GlobalExceptionResponse.class);

        assertAll(
                () -> assertEquals(HttpStatus.CONFLICT, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(
                        GlobalExceptionResponse.builder()
                                .errors(
                                        Collections.singletonList(
                                                Error.builder()
                                                        .httpStatus(ErrorEnum.CONFLICT.getHttpStatus())
                                                        .cause(ErrorEnum.CONFLICT.getMessage())
                                                        .message("Testing duplicate key exception in saveSimple method >>> Index <simpleId> : duplicate key [01]")
                                                        .build()
                                        )
                                )
                                .build(),
                        response.getBody())
        );
    }

    @Test
    void deleteSimpleWhenOk() {
        doNothing().when(simpleService).deleteSimple(eq("01"));

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(PATH.concat("/01"));

        final var response = testRestTemplate
                .exchange(builder.build().toUri(), HttpMethod.DELETE, HttpEntity.EMPTY, ResponseEntity.class);

        assertAll(
                () -> assertEquals(HttpStatus.ACCEPTED, response.getStatusCode()),
                () -> assertNull(response.getBody())
        );
    }

    @Test
    void deleteSimpleWhenIdNotExist() {
        doThrow(
                new FunctionalException(
                        "Resource to delete not found",
                        ErrorEnum.NO_DATA_FOUND,
                        "ID [01] not exist")
        ).when(simpleService).deleteSimple(eq("01"));

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(PATH.concat("/01"));

        final var response = testRestTemplate
                .exchange(builder.build().toUri(), HttpMethod.DELETE, HttpEntity.EMPTY, GlobalExceptionResponse.class);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(
                        GlobalExceptionResponse.builder()
                                .errors(
                                        Collections.singletonList(
                                                Error.builder()
                                                        .httpStatus(ErrorEnum.NO_DATA_FOUND.getHttpStatus())
                                                        .cause(ErrorEnum.NO_DATA_FOUND.getMessage())
                                                        .message("Resource to delete not found >>> ID [01] not exist")
                                                        .build()
                                        )
                                )
                                .build(),
                        response.getBody())
        );
    }
}