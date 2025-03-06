package ru.practicum.dto.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.model.Request;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RequestMapper {
    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    @Mapping(target = "requester", expression = "java(request.getRequester().getId())")
    @Mapping(target = "event", expression = "java(request.getEvent().getId())")
    @Mapping(target = "created", source = "createdOn")
    ParticipationRequestDto toParticipationRequestDto(Request request);
}
