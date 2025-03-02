package ru.practicum.controller.adminreq;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.service.EventService;

import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public Collection<EventDto> findEvents(@RequestParam(required = false) List<Long> users,
                                           @RequestParam(required = false) List<String> states,
                                           @RequestParam(required = false) List<Long> categories,
                                           @RequestParam(required = false) String rangeStart,
                                           @RequestParam(required = false) String rangeEnd,
                                           @RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("Поступил Admin-запрос событий от индекса {} количеством {}, users = {}, states = {}, categories = {}, rangeStart = {}, rangeEnd = {}",
                from, size, users, states, categories, rangeStart, rangeEnd);
        return eventService.findEventsByFilterAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateEventAdmin(@PathVariable Long eventId,
                                     @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Получен Admin-запрос на обновление события с ID {} и телом {}", eventId, updateEventAdminRequest);
        return eventService.updateEventAdmin(eventId, updateEventAdminRequest);
    }
}
