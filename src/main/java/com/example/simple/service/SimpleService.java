package com.example.simple.service;

import com.example.simple.domain.Simple;

import java.util.List;
import java.util.Optional;

public interface SimpleService {

    List<Simple> findAllSimple(Optional<String> name, Optional<Integer> initialAge, Optional<Integer> finalAge);

    Simple findSimpleById(String simpleId);

    void saveSimple(String simpleId, Simple simple);

    void deleteSimple(String simpleId);
}
