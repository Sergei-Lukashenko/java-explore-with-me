package ru.practicum.controller.adminreq;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.service.CategoryService;

@Slf4j
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> addNewCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Добавление новой категории с телом: {}", newCategoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(newCategoryDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        log.info("Запрошено удаление категории с ID: {}", id);
        categoryService.delete(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id,
                                      @RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Обновление категории с ID: {}, тело: {}", id, newCategoryDto);
        return ResponseEntity.ok().body(categoryService.update(id, newCategoryDto));
    }
}
