package ru.practicum.controller.adminreq;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.service.CompilationService;

@Slf4j
@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> newCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Получен Admin-запрос создания подборки событий с телом {}", newCompilationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(compilationService.create(newCompilationDto));
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long compId) {
        log.info("Получен Admin-запрос на удаление подборки событий с ID {}", compId);
        compilationService.deleteById(compId);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateById(@PathVariable Long compId,
                                     @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        log.info("Получен Admin-запрос на обновление подборки событий с ID {}", compId);
        return ResponseEntity.ok().body(compilationService.updateById(compId, updateCompilationRequest));
    }
}
