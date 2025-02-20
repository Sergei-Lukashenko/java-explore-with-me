package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsHitDto;
import ru.practicum.StatsViewDto;
import ru.practicum.service.StatsService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<StatsHitDto> saveHit(@Valid @RequestBody StatsHitDto hitDto) {
        log.info("Получен POST /hit с телом {}", hitDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(statsService.saveHit(hitDto));
    }

    @GetMapping("/stats")
    public ResponseEntity<Collection<StatsViewDto>> getStat(@RequestParam @NotNull String start,
                                            @RequestParam @NotNull String end,
                                            @RequestParam(required = false) List<String> uris,
                                            @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Получен GET /stats с параметрами start = {}, end = {}, uris = {}, unique = {}",
                start, end, uris, unique);
        Collection<StatsViewDto> stats = statsService.getStats(start, end,
                (uris == null || uris.isEmpty() ? null : uris), unique);
        log.info("Подготовлен ответ на GET /stats с телом: {}", stats);
        return ResponseEntity.ok().body(stats);
    }
}
