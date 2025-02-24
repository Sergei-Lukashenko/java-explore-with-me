package ru.practicum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatsViewDto {
    String app;
    String uri;
    Long hits;
}
