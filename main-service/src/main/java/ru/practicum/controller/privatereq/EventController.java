package ru.practicum.controller.privatereq;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
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
    public ResponseEntity<EventDto> getEvent(@PathVariable Long userId,
                                             @PathVariable Long eventId,
                                             @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.info("Поступил запрос на обновление события ID {} от пользователя ID {} с телом {}",
                eventId, userId, updateEventUserRequest);
        return ResponseEntity.ok().body(eventService.updateEventByUser(eventId, userId, updateEventUserRequest));
    }
/*
    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto newRequest(@PathVariable long userId,
                                              @RequestParam long eventId) {
        log.info("Creating request on event {} from user {}", eventId, userId);
        return eventService.newRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@PathVariable long userId,
                                                 @PathVariable long requestId) {
        log.info("Cancelling request {} from user {}", requestId, userId);
        return eventService.cancelRequest(userId, requestId);
    }
    @GetMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ParticipationRequestDto> findAllByUserId(@PathVariable long userId) {
        log.info("Get all requests from user {}", userId);
        return eventService.findAllRequestsByUserId(userId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ParticipationRequestDto> findAllByUserIdAndEventId(@PathVariable long userId,
                                                                         @PathVariable long eventId) {
        log.info("Get all requests on event {} created by user {}", eventId, userId);
        return eventService.findAllRequestsByEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateStatus(@PathVariable long userId,
                                                       @PathVariable long eventId,
                                                       @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Editing requests {} on event {} created by {}", request, eventId, userId);
        return eventService.updateRequestsStatus(userId, eventId, request);
    }
*/
}
