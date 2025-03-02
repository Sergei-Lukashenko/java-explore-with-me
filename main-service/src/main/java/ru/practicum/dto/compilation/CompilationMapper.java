package ru.practicum.dto.compilation;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.model.Compilation;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface CompilationMapper {
    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);

    CompilationDto toCompilationDto(Compilation compilation);

    Compilation toCompilation(NewCompilationDto newCompilationDto);

    void update(@MappingTarget Compilation compilation, UpdateCompilationRequest updateCompilationRequest);
}
