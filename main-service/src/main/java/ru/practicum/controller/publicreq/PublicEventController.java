package ru.practicum.controller.publicreq;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.enums.SortingOptions;
import ru.practicum.service.EventService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<Collection<EventShortDto>> findEvents(@RequestParam(required = false) String text,
                                                                @RequestParam(required = false) List<Long> categories,
                                                                @RequestParam(required = false) Boolean paid,
                                                                @RequestParam(required = false) String rangeStart,
                                                                @RequestParam(required = false) String rangeEnd,
                                                                @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                                @RequestParam(required = false) SortingOptions sortingOptions,
                                                                @RequestParam(defaultValue = "0") Integer from,
                                                                @RequestParam(defaultValue = "10") Integer size,
                                                                HttpServletRequest request) {
        log.info("Поступил Public-запрос событий от индекса {} количеством {}, text = {}, categories = {}, paid = {}, rangeStart = {}, rangeEnd = {}, onlyAvailable = {}, sortingOptions = {}",
                from, size, text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sortingOptions);
        return ResponseEntity.ok().body(eventService.findEventsByFilterPublic(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sortingOptions, from, size, request));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEventById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("Получен Public-запрос события с ID {}", eventId);
        return ResponseEntity.ok().body(eventService.findEventPublic(eventId, request));
    }
}
