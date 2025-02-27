package ru.practicum.service;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAll(Integer from, Integer size);

    CategoryDto getById(Long id);

    CategoryDto create(NewCategoryDto newCategoryDto);

    CategoryDto delete(Long id);

    CategoryDto update(Long id, NewCategoryDto newCategoryDto);
}
