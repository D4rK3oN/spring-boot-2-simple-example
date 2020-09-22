package com.example.simple.config;

import com.github.d4rk3on.spring.mvc.model.Error;
import com.github.d4rk3on.spring.mvc.model.response.GlobalExceptionResponse;
import com.github.d4rk3on.spring.mvc.util.ErrorEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GlobalExceptionHandlerTest {

    private static final String PATH = "/test";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void testExceptionHandler() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(PATH.concat("/exception"));

        final var response = testRestTemplate.getForEntity(builder.build().toUri(), GlobalExceptionResponse.class);

        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(
                        GlobalExceptionResponse.builder()
                                .errors(
                                        Collections.singletonList(
                                                Error.builder()
                                                        .httpStatus(ErrorEnum.INTERNAL_SERVER_ERROR.getHttpStatus())
                                                        .cause(ErrorEnum.INTERNAL_SERVER_ERROR.getMessage())
                                                        .message("Throw Exception")
                                                        .build()
                                        )
                                )
                                .build(),
                        response.getBody())
        );
    }

    @Test
    void testConstraintViolationExceptionHandler() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(PATH.concat("/constraintViolationException"));

        final var response = testRestTemplate.getForEntity(builder.build().toUri(), GlobalExceptionResponse.class);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(
                        GlobalExceptionResponse.builder()
                                .errors(
                                        Collections.singletonList(
                                                Error.builder()
                                                        .httpStatus(ErrorEnum.INVALID_INPUT_PARAMETERS.getHttpStatus())
                                                        .cause(ErrorEnum.INVALID_INPUT_PARAMETERS.getMessage())
                                                        .build()
                                        )
                                )
                                .build(),
                        response.getBody())
        );
    }

    @Test
    void testHttpMessageNotReadableExceptionHandler() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(PATH.concat("/httpMessageNotReadableException"));

        final var response = testRestTemplate.getForEntity(builder.build().toUri(), GlobalExceptionResponse.class);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(
                        GlobalExceptionResponse.builder()
                                .errors(
                                        Collections.singletonList(
                                                Error.builder()
                                                        .httpStatus(ErrorEnum.INVALID_REQUEST.getHttpStatus())
                                                        .cause(ErrorEnum.INVALID_REQUEST.getMessage())
                                                        .message("Required request body is missing")
                                                        .build()
                                        )
                                )
                                .build(),
                        response.getBody())
        );
    }

    @Test
    void testFunctionalExceptionHandler() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(PATH.concat("/functionalException"));

        final var response = testRestTemplate.getForEntity(builder.build().toUri(), GlobalExceptionResponse.class);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(
                        GlobalExceptionResponse.builder()
                                .errors(
                                        Collections.singletonList(
                                                Error.builder()
                                                        .httpStatus(ErrorEnum.INVALID_INPUT_PARAMETERS.getHttpStatus())
                                                        .cause(ErrorEnum.INVALID_INPUT_PARAMETERS.getMessage())
                                                        .message("Throw FunctionalException >>> Testing functional exception")
                                                        .build()
                                        )
                                )
                                .build(),
                        response.getBody())
        );
    }
}