package ru.practicum.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class NewCompilationDto {
    private Set<Long> events;

    private  Boolean pinned = false;

    @Size(max = 50)
    @NotBlank
    private  String title;
}
