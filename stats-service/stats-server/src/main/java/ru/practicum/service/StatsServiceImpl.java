package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.StatsHitDto;
import ru.practicum.StatsViewDto;
import ru.practicum.storage.StatRepository;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.model.StatItem;
import ru.practicum.model.StatMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StatRepository statRepository;

    @Override
    public StatsHitDto saveHit(StatsHitDto hitDto) {
        StatItem statItem = statRepository.save(StatMapper.INSTANCE.toStatItem(hitDto));
        return StatMapper.INSTANCE.toStatsHitDto(statItem);
    }

    @Override
    public Collection<StatsViewDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        if (start == null || end == null) {
            throw new ValidationException("Параметры start и end не могут быть равны null");
        }
        try {
            LocalDateTime startDate = LocalDateTime.parse(start, DTF);
            LocalDateTime endDate = LocalDateTime.parse(end, DTF);
            if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
                throw new ValidationException("endDate должен быть после startDate");
            }
            if (unique) {
                return statRepository.getUniqueIpStats(startDate, endDate, uris);
            } else {
                return statRepository.getAllIpStats(startDate, endDate, uris);
            }
        } catch (Exception e) {
            throw new ValidationException("Неверный формат одной из дат. Ожидаемый формат " + DTF.toString());
        }
    }
}
