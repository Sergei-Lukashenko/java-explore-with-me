package ru.practicum.controller.privatereq;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.*;
import ru.practicum.service.EventService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/{userId}/events")
    public ResponseEntity<EventDto> addNewEvent(@PathVariable Long userId,
                                                @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Поступил запрос на создание события от пользователя ID {} с телом {}", userId, newEventDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(userId, newEventDto));
    }

    @GetMapping("/{userId}/events")
    public ResponseEntity<Collection<EventShortDto>> findEvents(@PathVariable Long userId,
                                                                @RequestParam(defaultValue = "0") Integer from,
                                                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("Поступил запрос событий от пользователя ID {} c индекса {} количеством {}", userId, from, size);
        return ResponseEntity.ok().body(eventService.findEventsByUserId(userId, from, size));
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventDto> findEvent(@PathVariable Long userId,
                                              @PathVariable Long eventId) {
        log.info("Поступил запрос события ID {} от пользователя ID {}", eventId, userId);
        return ResponseEntity.ok().body(eventService.findEventByIdPlusUserId(eventId, userId));
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long userId,
                                                @PathVariable Long eventId,
                                                @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.info("Поступил запрос на обновление события ID {} от пользователя ID {} с телом {}",
                eventId, userId, updateEventUserRequest);
        return ResponseEntity.ok().body(eventService.updateEventByUser(eventId, userId, updateEventUserRequest));
    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<ParticipationRequestDto> newRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Поступил запрос на участие в событии ID {} от пользователя ID {}", eventId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.newParticipationRequest(userId, eventId));
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Поступил запрос на отмену запроса на участие ID {} от пользователя ID {}", requestId, userId);
        return ResponseEntity.ok().body(eventService.cancelParticipationRequest(userId, requestId));
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<Collection<ParticipationRequestDto>> findAllRequestsByUserId(@PathVariable Long userId) {
        log.info("Поступил запрос информации о заявках пользователя ID {} на участие в чужих событиях", userId);
        return ResponseEntity.ok().body(eventService.findAllRequestsByUserId(userId));
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<Collection<ParticipationRequestDto>> findAllRequestsByUserIdAndEventId(
            @PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Поступил запрос информации о запросах на участие в событии ID {} пользователя ID {}", eventId, userId);
        return ResponseEntity.ok().body(eventService.findAllRequestsByEventId(userId, eventId));
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("Поступил запрос на изменение статуса заявки на участие в событии {} от пользователя ID {} с телом {}",
                eventId, userId, updateRequest);
        return eventService.updateRequestsStatus(userId, eventId, updateRequest);
    }
}
