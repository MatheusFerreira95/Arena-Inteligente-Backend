package com.arenainteligente.core.interfaces.rest.reservations;

import com.arenainteligente.core.domain.reservations.Reservation;
import com.arenainteligente.core.domain.reservations.ReservationStatus;
import java.time.LocalDateTime;

public record ReservationResponse(
    Long id,
    String tenantId,
    Long courtId,
    String customerUserId,
    LocalDateTime startAt,
    LocalDateTime endAt,
    ReservationStatus status
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
            reservation.getId(),
            reservation.getTenantId(),
            reservation.getCourtId(),
            reservation.getCustomerUserId(),
            reservation.getStartAt(),
            reservation.getEndAt(),
            reservation.getStatus()
        );
    }
}
