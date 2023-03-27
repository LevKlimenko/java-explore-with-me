package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto save(Category category);

    CategoryDto update(Long id, Category category);

    boolean deleteById(Long id);

    CategoryDto getById(Long id);

    List<CategoryDto> getAll(int from, int size);
}
