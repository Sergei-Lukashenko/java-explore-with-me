package ru.practicum.dto.event;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.enums.EventActionState;
import ru.practicum.enums.EventActionStateAdmin;
import ru.practicum.enums.EventState;
import ru.practicum.model.Event;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface EventUpdater {
    EventUpdater INSTANCE = Mappers.getMapper(EventUpdater.class);

    @Mapping(target = "state", source = "stateAction")
    void update(@MappingTarget Event dbEvent, UpdateEventUserRequest updateEventUserRequest);

    @Mapping(target = "state", source = "stateAction")
    void update(@MappingTarget Event dbEvent, UpdateEventAdminRequest updateEventAdminRequest);

    @ValueMapping(target = "PENDING", source = "SEND_TO_REVIEW")
    @ValueMapping(target = "CANCELED", source = "CANCEL_REVIEW")
    EventState toEventState(EventActionState eventActionState);

    @ValueMapping(target = "PUBLISHED", source = "PUBLISH_EVENT")
    @ValueMapping(target = "CANCELED", source = "REJECT_EVENT")
    EventState toEventState(EventActionStateAdmin eventActionStateAdmin);
}
