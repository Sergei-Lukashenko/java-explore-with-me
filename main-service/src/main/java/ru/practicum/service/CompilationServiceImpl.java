package ru.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.storage.CompilationRepository;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.dto.compilation.CompilationMapper;
import ru.practicum.model.Compilation;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;

    @Override
    public CompilationDto findByIdPublic(Long id) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдена подборка событий с ID %d".formatted(id)));

        return CompilationMapper.INSTANCE.toCompilationDto(compilation);
    }

    @Override
    public Collection<CompilationDto> findByFilterPublic(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);

        return compilationRepository.findAllByFilterPublic(pinned, pageable).stream()
                .map(CompilationMapper.INSTANCE::toCompilationDto)
                .toList();
    }

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Compilation newCompilation = CompilationMapper.INSTANCE.toCompilation(newCompilationDto);
        newCompilation = compilationRepository.save(newCompilation);

        return CompilationMapper.INSTANCE.toCompilationDto(newCompilation);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException("Не найдена подборка событий с ID %d".formatted(id));
        } else {
            compilationRepository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public CompilationDto updateById(Long id, UpdateCompilationRequest updateCompilationRequest) {
        Compilation storedCompilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдена подборка событий с ID %d".formatted(id)));

        CompilationMapper.INSTANCE.update(storedCompilation, updateCompilationRequest);
        storedCompilation = compilationRepository.save(storedCompilation);

        return CompilationMapper.INSTANCE.toCompilationDto(storedCompilation);
    }
}
