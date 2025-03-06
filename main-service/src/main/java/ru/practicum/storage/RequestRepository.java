package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.enums.RequestStatus;
import ru.practicum.model.Request;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("""
            SELECT r
            FROM Request as r
            WHERE r.requester.id = :reqUserId
            AND r.event.id = :eventId
            """)
    Optional<Request> findByUserIdAndEventId(@Param("reqUserId") Long reqUserId,
                                             @Param("eventId") Long eventId);

    @Query("""
            SELECT r
            FROM Request as r
            WHERE r.requester.id = :reqUserId
            """)
    Collection<Request> findAllByUserId(@Param("reqUserId") Long reqUserId);

    @Query("""
            SELECT r
            FROM Request as r
            WHERE r.event.id = :eventId
            """)
    Collection<Request> findAllByEventId(@Param("eventId") Long eventId);

    @Query("""
            SELECT r
            FROM Request as r
            WHERE (r.event.id = :eventId)
            AND (r.status = :status)
            AND (r.id IN :requestIds)
            """)
    Collection<Request> findAllRequestsOnEventByIdsAndStatus(@Param("eventId") Long eventId,
                                                             @Param("requestIds") List<Long> requestIds,
                                                             @Param("status") RequestStatus status);
}
