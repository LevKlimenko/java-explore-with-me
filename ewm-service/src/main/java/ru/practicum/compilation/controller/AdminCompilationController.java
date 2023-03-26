package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationController {
    private final CompilationService service;

    @PostMapping
    public ResponseEntity<Object> addNew(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        CompilationDto newCompilation = service.save(newCompilationDto);
        log.info("Compilation have been add with name={} and id={}", newCompilation.getTitle(), newCompilation.getId());
        return new ResponseEntity<>(newCompilation, HttpStatus.CREATED);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<Object> patchCompilation(@PathVariable Long compId,
                                                   @RequestBody @Valid UpdateCompilationRequest request) {
        CompilationDto upCompilation = service.update(compId, request);
        log.info("Compilation with id={} have been updated", upCompilation.getId());
        return new ResponseEntity<>(upCompilation, HttpStatus.OK);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Object> deleteCompilation(@PathVariable Long compId) {
        service.delete(compId);
        log.info("Compilation with id={} have been updated", compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
