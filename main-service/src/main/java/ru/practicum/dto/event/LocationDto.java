package ru.practicum.dto.event;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class LocationDto {
    @NotNull
    @DecimalMin(value = "-90", inclusive = false)
    @DecimalMax(value = "90", inclusive = false)
    private Double lat;

    @NotNull
    @DecimalMin(value = "-180", inclusive = false)
    @DecimalMax(value = "180", inclusive = false)
    private Double lon;
}
