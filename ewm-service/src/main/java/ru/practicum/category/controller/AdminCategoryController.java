package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryRequestDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/admin/categories")
public class AdminCategoryController {
    private final CategoryService service;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        CategoryDto categoryDto = service.save(CategoryMapper.toCategory(categoryRequestDto));
        log.info("Category have been add with name={} and id={}", categoryDto.getName(), categoryDto.getId());
        return new ResponseEntity<>(categoryDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        CategoryDto categoryDto = service.update(id, CategoryMapper.toCategory(categoryRequestDto));
        log.info("Category with id={} have been update with name={}", categoryDto.getId(), categoryDto.getName());
        return new ResponseEntity<>(categoryDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        log.info("Category with id={} has been deleted", id);
        return new ResponseEntity<>("Category with id=" + id + " has been deleted", HttpStatus.NO_CONTENT);
    }
}
