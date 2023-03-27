package ru.practicum.category.controller;

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
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/categories")
public class PublicCategoryController {
    private final CategoryService service;

    @GetMapping
    public ResponseEntity<Object> getAll(@Valid @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @Valid @RequestParam(defaultValue = "10") @Positive int size) {
        List<CategoryDto> categories = service.getAll(from, size);
        log.info("The list of all categories has been received");
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        CategoryDto categoryDto = service.getById(id);
        log.info("Category with id={} have been received", id);
        return new ResponseEntity<>(categoryDto, HttpStatus.OK);
    }
}