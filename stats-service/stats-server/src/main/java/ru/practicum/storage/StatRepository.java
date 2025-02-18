package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.StatsViewDto;
import ru.practicum.model.StatItem;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<StatItem, Long> {
    @Query("""
            SELECT new ru.practicum.StatsViewDto(si.app, si.uri, COUNT(si.ip))
            FROM StatItem AS si
            WHERE si.created BETWEEN :start AND :end
            AND (:uris IS NULL OR si.uri IN :uris)
            GROUP BY si.uri, si.app
            ORDER BY COUNT(si.ip) DESC
            """)
    List<StatsViewDto> getAllIpStats(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end,
                                     @Param("uris") List<String> uris);

    @Query("""
            SELECT new ru.practicum.StatsViewDto(si.app, si.uri, COUNT(DISTINCT si.ip))
            FROM StatItem AS si
            WHERE si.created BETWEEN :start AND :end
            AND (:uris IS NULL OR si.uri IN :uris)
            GROUP BY si.uri, si.app
            ORDER BY COUNT(DISTINCT si.ip) DESC
            """)
    List<StatsViewDto> getUniqueIpStats(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end,
                                        @Param("uris") List<String> uris);
}
