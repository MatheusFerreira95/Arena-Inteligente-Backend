package com.arenainteligente.core.infrastructure.repository;

import com.arenainteligente.core.domain.reservations.Reservation;
import com.arenainteligente.core.domain.reservations.ReservationStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByTenantIdAndCourtId(String tenantId, Long courtId);

    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
        FROM Reservation r
        WHERE r.tenantId = :tenantId
          AND r.courtId = :courtId
          AND r.status = :status
          AND r.startAt < :endAt
          AND r.endAt > :startAt
        """)
    boolean hasConflict(
        @Param("tenantId") String tenantId,
        @Param("courtId") Long courtId,
        @Param("status") ReservationStatus status,
        @Param("startAt") LocalDateTime startAt,
        @Param("endAt") LocalDateTime endAt
    );
}
