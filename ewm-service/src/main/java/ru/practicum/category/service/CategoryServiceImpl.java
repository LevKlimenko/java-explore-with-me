package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private static final Sort SORT_BY_ASC = Sort.by(Sort.Direction.ASC, "id");

    @Transactional
    @Override
    public CategoryDto save(Category category) {
        try {
            return CategoryMapper.toCategoryDto(repository.save(category));
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Category with name: " + category.getName() +
                    " is already exist.");
        }
    }

    @Transactional
    @Override
    public CategoryDto update(Long id, Category category) {
        try {
            return CategoryMapper.toCategoryDto(checkUpdate(id, category));
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Category with name: " + category.getName() +
                    " is already exist.");
        }
    }

    @Transactional
    @Override
    public boolean deleteById(Long id) {
        Category category = findCategoryOrThrow(id);
        repository.deleteById(category.getId());
        return true;
    }

    @Override
    public CategoryDto getById(Long id) {
        return CategoryMapper.toCategoryDto(findCategoryOrThrow(id));
    }

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, SORT_BY_ASC);
        return CategoryMapper.toCategoryDtoList(repository.findAll(pageable).getContent());
    }

    private Category checkUpdate(Long id, Category category) {
        Category upCat = findCategoryOrThrow(id);
        if (category.getName() != null && !category.getName().isBlank()) {
            upCat.setName(category.getName());
        }
        return upCat;
    }

    private Category findCategoryOrThrow(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException("Category with ID=" + id + " not found"));
    }
}