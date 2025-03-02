package ru.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.storage.CategoryRepository;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.dto.category.CategoryMapper;
import ru.practicum.model.Category;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> findPortion(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.ASC, "id"));
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper.INSTANCE::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto findById(Long id) {
        Category category = getCategoryIfExists(id);
        log.info("Выбрана категория ID {} с телом: {}", category.getId(), category);
        return CategoryMapper.INSTANCE.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        final Category category = categoryRepository.save(CategoryMapper.INSTANCE.toCategory(newCategoryDto));
        log.info("Сохранена новая категория с ID {}", category.getId());
        return CategoryMapper.INSTANCE.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto delete(Long id) {
        final Category category = getCategoryIfExists(id);
        categoryRepository.deleteById(id);
        log.info("Удалена категория с ID {}, ее содержимое {}", id, category);
        return CategoryMapper.INSTANCE.toCategoryDto(category);

    }

    @Override
    @Transactional
    public CategoryDto update(Long id, NewCategoryDto newCategoryDto) {
        final Category category = getCategoryIfExists(id);
        log.info("Обновление категории с {} на {}", category, newCategoryDto);
        category.setName(newCategoryDto.getName());
        categoryRepository.save(category);
        return CategoryMapper.INSTANCE.toCategoryDto(category);
    }

    private Category getCategoryIfExists(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Не найдена категория с ID %d".formatted(id));
        }
        return categoryRepository.findById(id).orElseThrow();
    }
}
