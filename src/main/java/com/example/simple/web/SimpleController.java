package com.example.simple.web;

import com.example.simple.domain.Simple;
import com.example.simple.service.SimpleService;
import com.example.simple.web.response.SimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Optional;

@RestController
@RequestMapping("/example")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Validated
public class SimpleController {

    private final SimpleService simpleService;

    @GetMapping
    public SimpleResponse findAllSimple(
            @RequestParam(value = "name", required = false)
            @Size(min = 3, message = "The length of the name must be 3 or greater") final String name,
            @RequestParam(value = "initialAge", required = false) final Integer initialAge,
            @RequestParam(value = "finalAge", required = false) final Integer finalAge
    ) {
        return SimpleResponse.builder()
                .simpleList(simpleService.findAllSimple(
                        Optional.ofNullable(name),
                        Optional.ofNullable(initialAge),
                        Optional.ofNullable(finalAge)
                ))
                .build();
    }

    @GetMapping(path = "/{simpleId}")
    public Simple findSimpleById(@PathVariable final String simpleId) {
        return simpleService.findSimpleById(simpleId);
    }

    @PutMapping(path = "/{simpleId}")
    public ResponseEntity<?> saveSimple(@PathVariable @NotEmpty final String simpleId, @RequestBody Simple simple) {
        simpleService.saveSimple(simpleId, simple);

        return ResponseEntity.created(
                UriComponentsBuilder
                        .fromPath("/example/{simpleId}")
                        .buildAndExpand(simpleId)
                        .toUri()
        ).build();
    }

    @DeleteMapping(path = "/{simpleId}")
    public ResponseEntity<?> deleteSimple(@PathVariable @NotEmpty final String simpleId) {
        simpleService.deleteSimple(simpleId);

        return ResponseEntity.accepted().build();
    }
}
