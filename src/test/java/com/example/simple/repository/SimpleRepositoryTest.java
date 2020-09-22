package com.example.simple.repository;

import com.example.simple.config.MongoDbCollectionsConfig;
import com.example.simple.domain.Simple;
import com.mongodb.BasicDBList;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.apache.commons.io.FileUtils;
import org.bson.BsonArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoOperations;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimpleRepositoryTest {

    private static final List<Simple> SIMPLE_LIST_OK = List.of(
            Simple.builder().id("5cd9768a7a7aea34787394d4").simpleId("00").name("Domino").build(),
            Simple.builder().id("5cd976ab7a7aea34787394d5").simpleId("01").name("Cable").build(),
            Simple.builder().id("5a993d5d9ccd732bf541a19f").simpleId("02").name("Psylocke").build(),
            Simple.builder().id("5cd976ab7acd732bf541a19f").simpleId("03").name("Colossus").build(),
            Simple.builder().id("5d1b3dd7fd19fc27e027a65d").simpleId("04").name("Deadpool").age(28).build()
    );

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private MongoDbCollectionsConfig mongoDbCollectionsConfig;

    @Autowired
    private SimpleRepository simpleRepository;

    private void loadFileInMongodb(String path) throws IOException {
        mongoOperations.dropCollection(mongoDbCollectionsConfig.getSimpleObjects());

        mongoOperations.getCollection(mongoDbCollectionsConfig.getSimpleObjects())
                .createIndex(Indexes.ascending("simpleId"), new IndexOptions().unique(true));

        final var mongodbFile = FileUtils.readFileToString(
                new ClassPathResource(path).getFile(),
                Charset.defaultCharset()
        );

        BasicDBList dbList = new BasicDBList();

        dbList.addAll(BsonArray.parse(mongodbFile));

        for (Object dbObject : dbList)
            mongoOperations.save(dbObject, mongoDbCollectionsConfig.getSimpleObjects());
    }

    @Test
    void findAllWhenExistData() throws IOException {
        loadFileInMongodb("mongodb/examples.simpleObjects.data.json");

        final var response = simpleRepository.findAll();

        assertAll(
                () -> assertFalse(response.isEmpty()),
                () -> assertEquals(5, response.size()),
                () -> assertEquals(SIMPLE_LIST_OK, response)
        );
    }

    @Test
    void findAllWhenNoExistData() throws IOException {
        loadFileInMongodb("mongodb/examples.simpleObjects.empty.json");

        final var response = simpleRepository.findAll();

        assertAll(
                () -> assertTrue(response != null && response.isEmpty()),
                () -> assertEquals(List.of(), response)
        );
    }

    @Test
    void findAllWhenNoExistCollection() {
        mongoOperations.dropCollection(mongoDbCollectionsConfig.getSimpleObjects());

        final var response = simpleRepository.findAll();

        assertAll(
                () -> assertTrue(response != null && response.isEmpty()),
                () -> assertEquals(List.of(), response)
        );
    }

    @Test
    void findBySimpleIdWhenExistData() throws IOException {
        loadFileInMongodb("mongodb/examples.simpleObjects.data.json");

        final var response = simpleRepository.findBySimpleId("00");

        assertAll(
                () -> assertTrue(response.isPresent()),
                () -> assertEquals(Optional.of(Simple.builder()
                        .id("5cd9768a7a7aea34787394d4")
                        .simpleId("00")
                        .name("Domino")
                        .build()), response)
        );
    }

    @Test
    void findBySimpleIdWhenNoDataFound() throws IOException {
        loadFileInMongodb("mongodb/examples.simpleObjects.data.json");

        final var response = simpleRepository.findBySimpleId("unknown");

        assertAll(
                () -> assertFalse(response.isPresent()),
                () -> assertEquals(Optional.empty(), response)
        );
    }

    @Test
    void findByNameWhenExistData() throws IOException {
        loadFileInMongodb("mongodb/examples.simpleObjects.data.json");

        final var response = simpleRepository.findAllByNameIgnoreCaseLike("domi");

        assertAll(
                () -> assertFalse(response.isEmpty()),
                () -> assertEquals(List.of(Simple.builder()
                        .id("5cd9768a7a7aea34787394d4")
                        .simpleId("00")
                        .name("Domino")
                        .build()), response)
        );
    }

    @Test
    void findByNameWhenNoDataFound() throws IOException {
        loadFileInMongodb("mongodb/examples.simpleObjects.data.json");

        final var response = simpleRepository.findAllByNameIgnoreCaseLike("unknown");

        assertAll(
                () -> assertTrue(response.isEmpty()),
                () -> assertEquals(List.of(), response)
        );
    }

    @Test
    void findBetweenAgesWhenExistData() throws IOException {
        loadFileInMongodb("mongodb/examples.simpleObjects.data.json");

        final var response = simpleRepository.findAllByAgeBetween(20, 28);

        assertAll(
                () -> assertFalse(response.isEmpty()),
                () -> assertEquals(List.of(Simple.builder()
                        .id("5d1b3dd7fd19fc27e027a65d")
                        .simpleId("04")
                        .name("Deadpool")
                        .age(28)
                        .build()), response)
        );
    }

    @Test
    void findBetweenAgesWhenNoDataFound() throws IOException {
        loadFileInMongodb("mongodb/examples.simpleObjects.data.json");

        final var response = simpleRepository.findAllByAgeBetween(20, 25);

        assertAll(
                () -> assertTrue(response.isEmpty()),
                () -> assertEquals(List.of(), response)
        );
    }

    @Test
    void findByNameAndBetweenAgesWhenExistData() throws IOException {
        loadFileInMongodb("mongodb/examples.simpleObjects.data.json");

        final var response = simpleRepository.findAllByCustomFilters("dead", 28, 35);

        assertAll(
                () -> assertFalse(response.isEmpty()),
                () -> assertEquals(List.of(Simple.builder()
                        .id("5d1b3dd7fd19fc27e027a65d")
                        .simpleId("04")
                        .name("Deadpool")
                        .age(28)
                        .build()), response)
        );
    }

    @Test
    void findByNameAndBetweenAgesWhenNoDataFound() throws IOException {
        loadFileInMongodb("mongodb/examples.simpleObjects.data.json");

        final var response = simpleRepository.findAllByCustomFilters("Domi", 20, 30);

        assertAll(
                () -> assertTrue(response.isEmpty()),
                () -> assertEquals(List.of(), response)
        );
    }

    @Test
    void saveWhenOk() throws IOException {
        loadFileInMongodb("mongodb/examples.simpleObjects.empty.json");

        simpleRepository.save(Simple.builder().simpleId("01").name("Testing").build());

        final var response = simpleRepository.findAll();

        assertAll(
                () -> assertFalse(response.isEmpty()),
                () -> assertEquals(1, response.size()),
                () -> assertEquals("01", response.get(0).getSimpleId()),
                () -> assertEquals("Testing", response.get(0).getName())
        );
    }

    @Test
    void saveWhenIdAlreadyExist() throws IOException {
        loadFileInMongodb("mongodb/examples.simpleObjects.data.json");

        assertThrows(DuplicateKeyException.class, () -> simpleRepository.save(Simple.builder().simpleId("01").name("Testing").build()));
    }

    @Test
    void deleteWhenOk() throws IOException {
        loadFileInMongodb("mongodb/examples.simpleObjects.data.json");

        final var exampleToDelete = simpleRepository.findBySimpleId("00");

        assertAll(
                () -> assertTrue(exampleToDelete.isPresent()),
                () -> assertEquals(
                        Simple.builder()
                                .id("5cd9768a7a7aea34787394d4")
                                .simpleId("00")
                                .name("Domino")
                                .build(),
                        exampleToDelete.get())
        );

        simpleRepository.delete(exampleToDelete.get());

        final var findDeleted = simpleRepository.findBySimpleId("00");

        assertAll(
                () -> assertFalse(findDeleted.isPresent()),
                () -> assertTrue(findDeleted.isEmpty())
        );
    }

    @Test
    void deleteWhenElementNotExist() throws IOException {
        loadFileInMongodb("mongodb/examples.simpleObjects.data.json");

        simpleRepository.delete(Simple.builder().id("unknown").id("000").name("Testing").build());

        final var findDeleted = simpleRepository.findBySimpleId("000");

        assertAll(
                () -> assertFalse(findDeleted.isPresent()),
                () -> assertTrue(findDeleted.isEmpty())
        );
    }
}