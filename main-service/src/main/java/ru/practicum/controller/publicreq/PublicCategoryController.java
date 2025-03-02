package ru.practicum.controller.publicreq;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> findCategories(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Поступил запрос категорий от индекса {}, количеством {}", from, size);
        return ResponseEntity.ok().body(categoryService.findPortion(from, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> findCategoryById(@Positive @PathVariable Long id) {
        log.info("Поступил запрос категории с ID: {}", id);
        return ResponseEntity.ok().body(categoryService.findById(id));
    }
}
