package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/compilations")
public class PublicCompilationController {
    private final CompilationService service;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getByParameters(@RequestParam(required = false) Boolean pinned,
                                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                                @RequestParam(defaultValue = "10") @Positive int size) {
        List<CompilationDto> listCompilationDto = service.getByParameters(pinned, from, size);
        log.info("The list of all categories has been received");
        return new ResponseEntity<>(listCompilationDto, HttpStatus.OK);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getById(@PathVariable Long compId) {
        CompilationDto compilationDto = service.getById(compId);
        log.info("Category with id={} have been received", compId);
        return new ResponseEntity<>(compilationDto, HttpStatus.OK);
    }
}