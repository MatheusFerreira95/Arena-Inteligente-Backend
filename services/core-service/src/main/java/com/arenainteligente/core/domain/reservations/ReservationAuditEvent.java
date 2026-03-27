package com.arenainteligente.core.domain.reservations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "reservation_audit_events")
public class ReservationAuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false, length = 64)
    private String tenantId;

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(nullable = false, length = 255)
    private String message;

    @Column(name = "actor_user_id", length = 64)
    private String actorUserId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ReservationAuditEvent() {
    }

    public ReservationAuditEvent(
        String tenantId,
        Long reservationId,
        String eventType,
        String message,
        String actorUserId
    ) {
        this.tenantId = tenantId;
        this.reservationId = reservationId;
        this.eventType = eventType;
        this.message = message;
        this.actorUserId = actorUserId;
        this.createdAt = Instant.now();
    }
}
