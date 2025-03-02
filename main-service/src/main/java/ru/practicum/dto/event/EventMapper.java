package ru.practicum.dto.event;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.model.Event;

@Mapper
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    Event toEvent(NewEventDto newEventDto);

    EventDto toEventDto(Event event);

    EventShortDto toEventShortDto(Event event);
}
