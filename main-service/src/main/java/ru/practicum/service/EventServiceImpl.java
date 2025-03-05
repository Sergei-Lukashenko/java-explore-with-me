package ru.practicum.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.StatsHitDto;
import ru.practicum.StatsViewDto;
import ru.practicum.dto.event.*;
import ru.practicum.enums.EventState;
import ru.practicum.enums.EventActionStateAdmin;
import ru.practicum.enums.RequestStatus;
import ru.practicum.enums.SortingOptions;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.storage.EventRepository;
import ru.practicum.storage.LocationRepository;
import ru.practicum.storage.RequestRepository;
import ru.practicum.storage.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    private final StatsClient statsClient;

    @Override
    public EventDto findEventByIdPlusUserId(Long eventId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID %d".formatted(userId)));

        return EventMapper.INSTANCE.toEventDto(
                eventRepository.findByIdAndUserId(eventId, userId)
                        .orElseThrow(() -> new NotFoundException("Не найдено событие с ID %d по пользователю %d".formatted(eventId, userId)))
        );
    }

    @Override
    public Collection<EventShortDto> findEventsByUserId(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID %d".formatted(userId)));

        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        return eventRepository.findByUserId(userId, pageable).stream()
                .map(EventMapper.INSTANCE::toEventShortDto)
                .toList();
    }

    @Override
    public EventDto findEventPublic(Long eventId, HttpServletRequest request) {
        Event storedEvent = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Не найдено опубликованное событие с ID %d".formatted(eventId)));
        sendStatsRequest(request);

        List<StatsViewDto> viewStats = statsClient.getStats(
                storedEvent.getPublishedOn().format(DTF),
                LocalDateTime.now().format(DTF),
                List.of(request.getRequestURI()),
                true);
        log.debug("Получен список StatsViewDto: {}", viewStats);

        final long viewCount = viewStats != null && !viewStats.isEmpty()
                ? viewStats.getFirst().getHits()
                : /*storedEvent.getViews() +*/ 1;
        storedEvent.setViews(viewCount);
        eventRepository.save(storedEvent);
        return EventMapper.INSTANCE.toEventDto(storedEvent);
    }

    @Override
    public Collection<EventDto> findEventsByFilterAdmin(List<Long> users, List<String> states, List<Long> categories,
                                                        String rangeStart, String rangeEnd, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);

        LocalDateTime start = rangeStart != null ? LocalDateTime.parse(rangeStart, DTF) : null;
        LocalDateTime end = rangeEnd != null ? LocalDateTime.parse(rangeEnd, DTF) : null;

        return eventRepository.findAllByFilter((users == null || users.isEmpty() ? null : users),
                        (states == null || states.isEmpty() ? null : states),
                        (categories == null || categories.isEmpty() ? null : categories),
                        start, end, pageable).stream()
                .map(EventMapper.INSTANCE::toEventDto)
                .toList();
    }

    @Override
    public Collection<EventShortDto> findEventsByFilterPublic(String text, List<Long> categories, Boolean paid,
                                                              String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                              SortingOptions sortingOptions, Integer from, Integer size,
                                                              HttpServletRequest request) {
        Pageable pageable;
        if (sortingOptions != null) {
            final String sort = sortingOptions == SortingOptions.EVENT_DATE ? "eventDate" : "views";
            pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(sort).descending());
        } else {
            pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        }

        LocalDateTime start = rangeStart != null ? LocalDateTime.parse(rangeStart, DTF) : LocalDateTime.now();
        LocalDateTime end = null;
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, DTF);
            if (end.isBefore(start)) {
                throw new ValidationException("Дата окончания события оказалась до указанной даты начала или до текущего момента при пустой дате начала");
            }
        }

        sendStatsRequest(request);

        List<Event> events = eventRepository.findAllByFilterPublic(text,
                (categories == null || categories.isEmpty() ? null : categories),
                paid, start, end, onlyAvailable, EventState.PUBLISHED, pageable
        );

        if (events.isEmpty()) {
            return List.of();
        }

        List<String> uris = events.stream()
                .map(e -> "/event/" + e.getId())
                .toList();

        String startStatsDate = events.stream()
                .map(Event::getPublishedOn)
                .min(LocalDateTime::compareTo)
                .map(d -> d.format(DTF))
                .orElse("2000-01-01 00:00:00");

        String endStatsDate = LocalDateTime.now().format(DTF);

        List<StatsViewDto> statViews = statsClient.getStats(startStatsDate, endStatsDate, uris, false);
        Map<String, Long> eventViews = statViews.stream()
                .collect(Collectors.toMap(StatsViewDto::getUri, StatsViewDto::getHits));
        eventViews.forEach((uri, hits) -> {
            String[] uriSplit = "/".split(uri);
            long partUri = Long.parseLong(uriSplit[uriSplit.length - 1]);
            events.stream()
                    .filter(e -> e.getId() == partUri)
                    .findFirst()
                    .ifPresent(e -> e.setViews(hits));
        });

        eventRepository.saveAll(events);
        return events.stream()
                .map(EventMapper.INSTANCE::toEventShortDto)
                .toList();
    }

    @Override
    @Transactional
    public EventDto create(Long userId, NewEventDto newEventDto) {
        validateEventDate(newEventDto.getEventDate());

        final User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID %d".formatted(userId)));

        final Event newEvent = EventMapper.INSTANCE.toEvent(newEventDto);

        final Location location = locationRepository.save(newEvent.getLocation());

        newEvent.setInitiator(initiator);
        newEvent.setState(EventState.PENDING);
        newEvent.setCreatedOn(LocalDateTime.now());
        newEvent.setLocation(location);
        eventRepository.save(newEvent);

        return EventMapper.INSTANCE.toEventDto(eventRepository.save(newEvent));
    }

    @Override
    @Transactional
    public EventDto updateEventByUser(Long eventId, Long userId, UpdateEventUserRequest updateEventUserRequest) {
        validateEventDate(updateEventUserRequest.getEventDate());
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID %d".formatted(userId)));

        final Event storedEvent = eventRepository.findByIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с ID %d по пользователю %d".formatted(eventId, userId)));

        if (storedEvent.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Запрещено обновлять опубликованное событие ID %d".formatted(eventId));
        }

        EventUpdater.INSTANCE.update(storedEvent, updateEventUserRequest);
        eventRepository.save(storedEvent);

        return EventMapper.INSTANCE.toEventDto(storedEvent);
    }

    @Override
    @Transactional
    public EventDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        validateEventDate(updateEventAdminRequest.getEventDate());

        final Event storedEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с ID %d".formatted(eventId)));

        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction() == EventActionStateAdmin.REJECT_EVENT &&
                    storedEvent.getState() == EventState.PUBLISHED) {
                throw new ConflictException("Невозможно отменить опубликованное событие %d".formatted(eventId));
            }

            if (updateEventAdminRequest.getStateAction() == EventActionStateAdmin.PUBLISH_EVENT &&
                    storedEvent.getState() != EventState.PENDING) {
                throw new ConflictException("Событие %d не в статусе PENDING, его невозможно опубликовать".formatted(eventId));
            }

            if (updateEventAdminRequest.getStateAction() == EventActionStateAdmin.PUBLISH_EVENT &&
                    storedEvent.getEventDate().minusHours(1L).isBefore(LocalDateTime.now())) {
                throw new ConflictException("Невозможно опубликовать событие %d менее, чем за 1 час до его начала".formatted(eventId));
            }
        }

        EventUpdater.INSTANCE.update(storedEvent, updateEventAdminRequest);

        if (storedEvent.getState() == EventState.PUBLISHED) {
            storedEvent.setPublishedOn(LocalDateTime.now());
            storedEvent.setConfirmedRequests(0L);
            storedEvent.setViews(0L);
        }
        eventRepository.save(storedEvent);

        return EventMapper.INSTANCE.toEventDto(storedEvent);
    }

    @Override
    @Transactional
    public ParticipationRequestDto newParticipationRequest(Long reqUserId, Long eventId) {
        if (requestRepository.findByUserIdAndEventId(reqUserId, eventId).isPresent()) {
            throw new ConflictException("Запрос на участие от пользователя ID %d на событие ID %d уже существует".formatted(reqUserId, eventId));
        }

        User reqUser = userRepository.findById(reqUserId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID %d".formatted(reqUserId)));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с ID %d".formatted(eventId)));

        if (Objects.equals(event.getInitiator().getId(), reqUserId))
            throw new ConflictException("Инициатор события не может делать запрос на участие в нем");

        if (event.getState() != EventState.PUBLISHED)
            throw new ConflictException("Событие с ID %d еще не опубликовано, запрос на участие отклонен".formatted(eventId));

        if ((event.getParticipantLimit() != 0) && (event.getParticipantLimit() <= event.getConfirmedRequests()))
            throw new ConflictException("Уже достигнут лимит на посещение события с ID %d".formatted(eventId));

        Request request = Request.builder()
                .requester(reqUser)
                .event(event)
                .createdOn(LocalDateTime.now())
                .build();
        if (event.getParticipantLimit() != 0 && event.getRequestModeration())
            request.setStatus(RequestStatus.PENDING);
        else {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        return RequestMapper.INSTANCE.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на участие с ID %d не найден".formatted(requestId)));
        if (!Objects.equals(request.getRequester().getId(), userId))
            throw new ConflictException("Пользователь с ID %d не является инициатором запроса на участие %d".formatted(userId, requestId));
        requestRepository.delete(request);
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.INSTANCE.toParticipationRequestDto(request);
    }

    @Override
    public Collection<ParticipationRequestDto> findAllRequestsByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID %d".formatted(userId)));
        return requestRepository.findAllByUserId(userId).stream()
                .map(RequestMapper.INSTANCE::toParticipationRequestDto)
                .toList();
    }

    @Override
    public Collection<ParticipationRequestDto> findAllRequestsByEventId(Long userId, Long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID %d".formatted(userId)));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с ID %d".formatted(eventId)));

        if (!Objects.equals(event.getInitiator().getId(), userId))
            throw new ConflictException("Инициатор события отличается от пользователя, указанного в запросе");

        return requestRepository.findAllByEventId(eventId).stream()
                .map(RequestMapper.INSTANCE::toParticipationRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestsStatus(Long userId,
                                                               Long eventId,
                                                               EventRequestStatusUpdateRequest updateRequest) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID %d".formatted(userId)));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с ID %d".formatted(eventId)));

        if (!Objects.equals(event.getInitiator().getId(), userId))
            throw new ValidationException("Пользователь ID %d не является инициатором события ID %d".formatted(userId, eventId));

        Collection<Request> requests = requestRepository.findAllRequestsOnEventByIdsAndStatus(eventId,
                updateRequest.getRequestIds(), RequestStatus.PENDING);
        int limit = event.getParticipantLimit() - event.getConfirmedRequests().intValue();
        int confirmed = event.getConfirmedRequests().intValue();
        if (limit <= 0) {
            throw new ConflictException("Достигнут лимит на участие в событии %d".formatted(eventId));
        }

        for (Request req : requests) {
            if (updateRequest.getStatus().equals(RequestStatus.REJECTED)) {
                req.setStatus(RequestStatus.REJECTED);
            } else if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
                req.setStatus(RequestStatus.CONFIRMED);
                confirmed++;
            } else if (limit == 0) {
                req.setStatus(RequestStatus.REJECTED);
            } else {
                req.setStatus(RequestStatus.CONFIRMED);
                limit--;
            }
            requestRepository.save(req);
        }
        if (event.getParticipantLimit() != 0)
            event.setConfirmedRequests((long) event.getParticipantLimit() - limit);
        else
            event.setConfirmedRequests((long) confirmed);
        eventRepository.save(event);

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(requestRepository.findAllRequestsOnEventByIdsAndStatus(eventId,
                        updateRequest.getRequestIds(), RequestStatus.CONFIRMED
                        ).stream()
                .map(RequestMapper.INSTANCE::toParticipationRequestDto)
                .toList());
        result.setRejectedRequests(requestRepository.findAllRequestsOnEventByIdsAndStatus(eventId,
                        updateRequest.getRequestIds(), RequestStatus.REJECTED
                        ).stream()
                .map(RequestMapper.INSTANCE::toParticipationRequestDto)
                .toList());
        return result;
    }

    private void sendStatsRequest(HttpServletRequest request) {
        log.debug("Сохранение статистики запроса события, URI = {}, remoteAddr = {}",
                request.getRequestURI(), request.getRemoteAddr());
        statsClient.hit(StatsHitDto.builder()
                .app("main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
    }

    private void validateEventDate(LocalDateTime eventDate) {
        LocalDateTime validEventDate = LocalDateTime.now().plusHours(2L);
        if (eventDate != null && eventDate.isBefore(validEventDate)) {
            throw new ValidationException("Время начала события должно отстоять от текущего как минимум на 2 часа");
        }
    }
}
