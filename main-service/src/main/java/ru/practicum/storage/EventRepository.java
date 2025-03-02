package ru.practicum.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.enums.EventState;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("""
            SELECT e
            FROM Event as e
            WHERE e.id = :eventId
            AND e.initiator.id = :userId
            """)
    Optional<Event> findByIdAndUserId(@Param("eventId") Long eventId,
                                      @Param("userId") Long userId);

    @Query("""
            SELECT e
            FROM Event as e
            WHERE e.initiator.id = :userId
            """)
    List<Event> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
            SELECT e
            FROM Event as e
            WHERE e.id = :eventId
            AND e.state = :state
            """)
    Optional<Event> findByIdAndState(@Param("eventId") Long eventId, EventState state);

    @Query("""
            SELECT e
            FROM Event as e
            WHERE (:userIds IS NULL OR e.initiator.id in :userIds)
            AND (:states IS NULL OR e.state in :states)
            AND (:categoryIds IS NULL OR e.category.id in :categoryIds)
            AND (:start IS NULL OR e.eventDate >= :start)
            AND (:end IS NULL OR e.eventDate <= :end)
            """)
    List<Event> findAllByFilter(@Param("userIds") List<Long> userIds,
                                @Param("states") List<String> states,
                                @Param("categoryIds") List<Long> categoryIds,
                                @Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end,
                                Pageable pageable);

    @Query("""
            SELECT e
            FROM Event as e
            WHERE (:text IS NULL OR (e.annotation ilike %:text% OR e.description ilike %:text%))
            AND (:categoryIds IS NULL OR e.category.id in :categoryIds)
            AND (:paid IS NULL OR e.paid = :paid)
            AND (e.eventDate >= :start)
            AND (e.state = :state)
            AND (:onlyAvailable = false OR e.participantLimit = 0 OR e.confirmedRequests <= e.participantLimit)
            AND (:end IS NULL OR e.eventDate <= :end)
            """)
    List<Event> findAllByFilterPublic(@Param("text") String text,
                                      @Param("categoryIds") List<Long> categoryIds,
                                      @Param("paid") Boolean paid,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end,
                                      @Param("onlyAvailable") Boolean onlyAvailable,
                                      @Param("state") EventState state,
                                      Pageable pageable);

}
