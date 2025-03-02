package ru.practicum.dto.compilation;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface CompilationMapper {
    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);

    CompilationDto toCompilationDto(Compilation compilation);

    Compilation toCompilation(NewCompilationDto newCompilationDto);

    void update(@MappingTarget Compilation compilation, UpdateCompilationRequest updateCompilationRequest);

/*  to avoid
    .../java-explore-with-me/main-service/src/main/java/ru/practicum/dto/compilation/CompilationMapper.java:17:17
    java: Can't map property "Set<Long> events" to "Set<Event> events". Consider to declare/implement a mapping method: "Set<Event> map(Set<Long> value)".
*/
    @Mapping(target = "id", source = "id")
    Event getEventFromLong(Long id);
}
