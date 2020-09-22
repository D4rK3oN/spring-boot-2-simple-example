package com.example.simple.service;

import com.example.simple.domain.Simple;
import com.example.simple.repository.SimpleRepository;
import com.github.d4rk3on.spring.mvc.util.ErrorEnum;
import com.github.d4rk3on.spring.mvc.util.exception.FunctionalException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SimpleServiceImpl implements SimpleService {

    private static final String FIND_ALL = "ALL";
    private static final String FIND_BY_ALL_FILTERS = "ALL_FILTERS";
    private static final String FIND_BY_NAME = "NAME";
    private static final String FIND_BY_AGE = "AGE";

    private final SimpleRepository simpleRepository;

    @Override
    public List<Simple> findAllSimple(Optional<String> name, Optional<Integer> initialAge, Optional<Integer> finalAge) {
        final List<Simple> simpleList = executeFindRequest(
                evaluateFindRequest(name, initialAge, finalAge),
                name.orElse(""),
                initialAge.orElse(0),
                finalAge.orElse(100)
        );

        return !CollectionUtils.isEmpty(simpleList) ? simpleList : List.of();
    }

    private String evaluateFindRequest(Optional<String> name, Optional<Integer> initialAge, Optional<Integer> finalAge) {
        if (name.isPresent() && (initialAge.isPresent() || finalAge.isPresent()))
            return FIND_BY_ALL_FILTERS;

        if (name.isPresent())
            return FIND_BY_NAME;

        if (initialAge.isPresent() || finalAge.isPresent())
            return FIND_BY_AGE;

        return FIND_ALL;
    }

    private List<Simple> executeFindRequest(String requestType, String name, Integer initialAge, Integer finalAge) {
        switch (requestType) {
            case FIND_ALL:
                return simpleRepository.findAll();
            case FIND_BY_ALL_FILTERS:
                return simpleRepository.findAllByCustomFilters(name, initialAge, finalAge);
            case FIND_BY_NAME:
                return simpleRepository.findAllByNameIgnoreCaseLike(name);
            case FIND_BY_AGE:
                return simpleRepository.findAllByAgeBetween(initialAge, finalAge);
            default:
                throw new FunctionalException("Error evaluating find request", ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Simple findSimpleById(String simpleId) {
        final var simple = simpleRepository.findBySimpleId(simpleId).orElse(null);

        if (simple == null || simple.isEmpty())
            throw new FunctionalException(
                    "Not valid findBySimpleId response", ErrorEnum.NO_DATA_FOUND, "ID [".concat(simpleId).concat("] not exist")
            );

        return simple;
    }

    @Override
    public void saveSimple(String simpleId, Simple simple) {
        try {
            simpleRepository.save(simple.toBuilder().simpleId(simpleId).build());
        } catch (DuplicateKeyException ex) {
            throw new FunctionalException(
                    ex.getMessage(), ErrorEnum.CONFLICT, "Index <simpleId> : duplicate key [".concat(simpleId).concat("]")
            );
        }
    }

    @Override
    public void deleteSimple(String simpleId) {
        simpleRepository.delete(findSimpleToDeleteBySimpleId(simpleId));
    }

    private Simple findSimpleToDeleteBySimpleId(String simpleId) {
        final var simple = simpleRepository.findBySimpleId(simpleId).orElse(null);

        if (simple == null || simple.isEmpty())
            throw new FunctionalException(
                    "Resource to delete not found", ErrorEnum.NO_DATA_FOUND, "ID [".concat(simpleId).concat("] not exist")
            );

        return simple;
    }
}
