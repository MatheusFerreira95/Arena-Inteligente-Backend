package com.arenainteligente.core.interfaces.rest.reservations;

import com.arenainteligente.core.application.reservations.ReservationService;
import com.arenainteligente.core.domain.reservations.Reservation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse create(
        @RequestHeader("X-Tenant-Id") String tenantId,
        @Valid @RequestBody CreateReservationRequest request
    ) {
        Reservation reservation = reservationService.create(
            tenantId,
            request.courtId(),
            request.customerUserId(),
            request.startAt(),
            request.endAt()
        );
        return ReservationResponse.from(reservation);
    }
}
