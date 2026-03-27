package com.arenainteligente.core.infrastructure.repository;

import com.arenainteligente.core.domain.courts.CourtUnavailabilityBlock;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourtUnavailabilityBlockRepository extends JpaRepository<CourtUnavailabilityBlock, Long> {

    @Query("""
        SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
        FROM CourtUnavailabilityBlock b
        WHERE b.tenantId = :tenantId
          AND b.courtId = :courtId
          AND b.startAt < :endAt
          AND b.endAt > :startAt
        """)
    boolean hasOverlap(
        @Param("tenantId") String tenantId,
        @Param("courtId") Long courtId,
        @Param("startAt") LocalDateTime startAt,
        @Param("endAt") LocalDateTime endAt
    );
}
