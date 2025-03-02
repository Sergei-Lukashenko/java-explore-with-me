package ru.practicum.controller.publicreq;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.service.CompilationService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public ResponseEntity<Collection<CompilationDto>> findCompilations(@RequestParam(required = false) Boolean pinned,
                                                                       @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                       @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Поступил запрос подборок событий от индекса {} количеством {}, pinned = {}", from, size, pinned);
        return ResponseEntity.ok().body(compilationService.findByFilterPublic(pinned, from, size));
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> findCompilationById(@PathVariable Long compId) {
        log.info("Поступил запрос подборки событий с ID {}", compId);
        return ResponseEntity.ok().body(compilationService.findByIdPublic(compId));
    }
}
