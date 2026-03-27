package com.arenainteligente.core.infrastructure.repository;

import com.arenainteligente.core.domain.reservations.ReservationAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationAuditEventRepository extends JpaRepository<ReservationAuditEvent, Long> {
}
