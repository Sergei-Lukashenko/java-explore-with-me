package ru.practicum.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.StatsHitDto;

@Mapper
public interface StatMapper {
    StatMapper INSTANCE = Mappers.getMapper(StatMapper.class);

    @Mapping(target = "created", source = "timestamp")
    StatItem toStatItem(StatsHitDto statsHitDto);

    @Mapping(target = "timestamp", source = "created")
    StatsHitDto toStatsHitDto(StatItem statItem);
}
