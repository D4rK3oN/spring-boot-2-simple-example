package com.example.simple.service;

import com.example.simple.domain.Simple;
import com.example.simple.repository.SimpleRepository;
import com.github.d4rk3on.spring.mvc.util.exception.FunctionalException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimpleServiceTest {

    private static final List<Simple> SIMPLE_LIST_OK = List.of(
            Simple.builder().id("id_001").simpleId("01").name("Domino").build(),
            Simple.builder().id("id_002").simpleId("02").name("Cable").build(),
            Simple.builder().id("id_003").simpleId("03").name("Psylocke").build(),
            Simple.builder().id("id_004").simpleId("04").name("Colossus").build(),
            Simple.builder().id("id_005").simpleId("05").name("Deadpool").age(28).build()
    );

    @Mock
    private SimpleRepository simpleRepository;

    @InjectMocks
    private SimpleServiceImpl simpleService;

    @Test
    void findAllSimpleWhenOk() {
        when(simpleRepository.findAll()).thenReturn(SIMPLE_LIST_OK);

        final var response = simpleService.findAllSimple(Optional.empty(), Optional.empty(), Optional.empty());

        assertAll(
                () -> assertTrue(response != null && !response.isEmpty()),
                () -> assertEquals(5, response.size()),
                () -> assertEquals(SIMPLE_LIST_OK, response)
        );
    }

    @Test
    void findAllSimpleWhenNoDataFound() {
        when(simpleRepository.findAll()).thenReturn(List.of());

        final var emptyResponse = simpleService.findAllSimple(Optional.empty(), Optional.empty(), Optional.empty());

        assertAll(
                () -> assertTrue(emptyResponse != null && emptyResponse.isEmpty()),
                () -> assertEquals(0, emptyResponse.size())
        );

        when(simpleRepository.findAll()).thenReturn(null);

        final var nullResponse = simpleService.findAllSimple(Optional.empty(), Optional.empty(), Optional.empty());

        assertAll(
                () -> assertTrue(nullResponse != null && nullResponse.isEmpty()),
                () -> assertEquals(0, nullResponse.size())
        );
    }

    @Test
    void findAllSimpleByNameWhenOk() {
        when(simpleRepository.findAllByNameIgnoreCaseLike(SIMPLE_LIST_OK.get(0).getName()))
                .thenReturn(List.of(SIMPLE_LIST_OK.get(0)));

        final var response = simpleService.findAllSimple(
                Optional.of(SIMPLE_LIST_OK.get(0).getName()),
                Optional.empty(),
                Optional.empty());

        assertAll(
                () -> assertTrue(response != null && !response.isEmpty()),
                () -> assertEquals(1, response.size()),
                () -> assertEquals(List.of(SIMPLE_LIST_OK.get(0)), response)
        );
    }

    @Test
    void findAllSimpleBetweenAgesWhenOk() {
        when(simpleRepository.findAllByAgeBetween(20, 30))
                .thenReturn(List.of(SIMPLE_LIST_OK.get(4)));

        final var response = simpleService.findAllSimple(
                Optional.empty(),
                Optional.of(20),
                Optional.of(30));

        assertAll(
                () -> assertTrue(response != null && !response.isEmpty()),
                () -> assertEquals(1, response.size()),
                () -> assertEquals(List.of(SIMPLE_LIST_OK.get(4)), response)
        );
    }

    @Test
    void findAllSimpleByNameAndBetweenAgesWhenOk() {
        when(simpleRepository.findAllByCustomFilters("pool", 20, 30))
                .thenReturn(List.of(SIMPLE_LIST_OK.get(4)));

        final var response = simpleService.findAllSimple(
                Optional.of("pool"),
                Optional.of(20),
                Optional.of(30));

        assertAll(
                () -> assertTrue(response != null && !response.isEmpty()),
                () -> assertEquals(1, response.size()),
                () -> assertEquals(List.of(SIMPLE_LIST_OK.get(4)), response)
        );
    }

    @Test
    void findSimpleByIdWhenExistData() {
        when(simpleRepository.findBySimpleId("01"))
                .thenReturn(Optional.of(Simple.builder()
                        .id("id_001")
                        .simpleId("01")
                        .name("Domino")
                        .build()));

        final var response = simpleService.findSimpleById("01");

        assertAll(
                () -> assertNotNull(response),
                () -> assertFalse(response.isEmpty()),
                () -> assertEquals(Simple.builder().id("id_001").simpleId("01").name("Domino").build(), response)
        );
    }

    @Test
    void findSimpleByIdWhenNoDataFound() {
        when(simpleRepository.findBySimpleId("00")).thenReturn(Optional.empty());

        assertThrows(FunctionalException.class, () -> simpleService.findSimpleById("00"));
    }

    @Test
    void saveSimpleWhenIdAlreadyExist() {
        when(simpleRepository.save(Simple.builder().simpleId("01").name("Testing").build()))
                .thenThrow(new DuplicateKeyException("Duplicate key error"));

        assertThrows(FunctionalException.class, () -> simpleService.saveSimple("01", Simple.builder().name("Testing").build()));
    }

    @Test
    void deleteSimpleWhenNoDataFoundById() {
        when(simpleRepository.findBySimpleId("01")).thenReturn(Optional.empty());

        assertThrows(FunctionalException.class, () -> simpleService.deleteSimple("01"));
    }
}