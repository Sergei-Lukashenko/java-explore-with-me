package ru.practicum.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.event.*;
import ru.practicum.enums.SortingOptions;

import java.util.Collection;
import java.util.List;

public interface EventService {
    EventDto findEventByIdPlusUserId(Long eventId, Long userId);

    Collection<EventShortDto> findEventsByUserId(Long userId, Integer from, Integer size);

    EventDto findEventPublic(Long eventId, HttpServletRequest request);

    Collection<EventDto> findEventsByFilterAdmin(List<Long> users,
                                                 List<String> states,
                                                 List<Long> categories,
                                                 String rangeStart,
                                                 String rangeEnd,
                                                 Integer from,
                                                 Integer size);

    Collection<EventShortDto> findEventsByFilterPublic(String text,
                                                       List<Long> categories,
                                                       Boolean paid,
                                                       String rangeStart,
                                                       String rangeEnd,
                                                       Boolean onlyAvailable,
                                                       SortingOptions sortingOptions,
                                                       Integer from,
                                                       Integer size,
                                                       HttpServletRequest request);

    EventDto create(Long userId, NewEventDto newEventDto);

    EventDto updateEventByUser(Long eventId, Long userId, UpdateEventUserRequest updateEventUserRequest);

    EventDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    ParticipationRequestDto newParticipationRequest(Long reqUserId, Long eventId);

    ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId);

    Collection<ParticipationRequestDto> findAllRequestsByUserId(Long userId);

    Collection<ParticipationRequestDto> findAllRequestsByEventId(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsStatus(Long userId,
                                                        Long eventId,
                                                        EventRequestStatusUpdateRequest updateRequest);

    long countEventByCategory(Long categoryId);
}

