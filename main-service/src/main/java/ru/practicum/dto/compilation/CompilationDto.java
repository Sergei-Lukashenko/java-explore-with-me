package ru.practicum.dto.compilation;

import lombok.*;
import ru.practicum.dto.event.EventShortDto;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = { "id", "title" })
public class CompilationDto {
    private Long id;
    private Set<EventShortDto> events;
    private Boolean pinned;
    private String title;
}
