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

    ParticipationRequestDto newRequest(Long reqUserId, Long eventId);
/*

    ParticipationRequestDto cancelRequest(long userId, long requestId);

    Collection<ParticipationRequestDto> findAllRequestsByUserId(long userId);

    Collection<ParticipationRequestDto> findAllRequestsByEventId(long userId, long eventId);

    EventRequestStatusUpdateResult updateRequestsStatus(long userId,
                                                        long eventId,
                                                        EventRequestStatusUpdateRequest request);
*/
}

