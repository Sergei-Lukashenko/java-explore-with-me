package ru.practicum.service;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

import java.util.Collection;

public interface CompilationService {
    CompilationDto findByIdPublic(Long id);

    Collection<CompilationDto> findByFilterPublic(Boolean pinned, Integer from, Integer size);

    CompilationDto create(NewCompilationDto newCompilationDto);

    void deleteById(Long id);

    CompilationDto updateById(Long id, UpdateCompilationRequest updateCompilationRequest);
}
