package com.example.simple.service;

import com.example.simple.domain.Simple;
import com.example.simple.repository.SimpleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimpleServiceTest {

    private static final List<Simple> RESPONSE_OK = List.of(
            Simple.builder().documentId("id_001").id("01").name("Domino").build(),
            Simple.builder().documentId("id_002").id("02").name("Cable").build(),
            Simple.builder().documentId("id_003").id("03").name("Psylocke").build(),
            Simple.builder().documentId("id_004").id("04").name("Colossus").build()
    );

    @Mock
    private SimpleRepository simpleRepository;

    @InjectMocks
    private SimpleServiceImpl simpleService;

    @Test
    void findAllSimpleWhenOk() {
        when(simpleRepository.findAll()).thenReturn(RESPONSE_OK);

        final var response = simpleService.findAllSimple();

        assertAll(
                () -> assertTrue(response != null && !response.isEmpty()),
                () -> assertEquals(4, response.size()),
                () -> assertEquals(RESPONSE_OK, response)
        );
    }

    @Test
    void findAllSimpleWhenNoDataFound() {
        when(simpleRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        final var response = simpleService.findAllSimple();

        assertAll(
                () -> assertTrue(response != null && response.isEmpty()),
                () -> assertEquals(0, response.size())
        );
    }

    @Test
    void findAllSimpleWhenIsNull() {
        when(simpleRepository.findAll()).thenReturn(null);

        final var response = simpleService.findAllSimple();

        assertAll(
                () -> assertTrue(response != null && response.isEmpty()),
                () -> assertEquals(0, response.size())
        );
    }
}