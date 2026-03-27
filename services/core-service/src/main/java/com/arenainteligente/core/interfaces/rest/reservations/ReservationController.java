package com.arenainteligente.core.interfaces.rest.reservations;

import com.arenainteligente.core.application.reservations.ReservationService;
import com.arenainteligente.core.domain.reservations.Reservation;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/agenda")
    public AgendaQueryResponse agenda(
        @RequestHeader("X-Tenant-Id") String tenantId,
        @RequestParam Long courtId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        List<ReservationResponse> reservations = reservationService.agenda(tenantId, courtId, from, to)
            .stream()
            .map(ReservationResponse::from)
            .toList();
        return new AgendaQueryResponse(reservations);
    }

    @PostMapping("/{reservationId}/cancel")
    public ReservationResponse cancel(
        @RequestHeader("X-Tenant-Id") String tenantId,
        @PathVariable Long reservationId
    ) {
        Reservation reservation = reservationService.cancel(tenantId, reservationId);
        return ReservationResponse.from(reservation);
    }

    @GetMapping("/agenda/daily")
    public DailyAgendaResponse agendaDaily(
        @RequestHeader("X-Tenant-Id") String tenantId,
        @RequestParam Long courtId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        Map<java.time.LocalDate, List<ReservationResponse>> grouped = reservationService
            .agendaByDay(tenantId, courtId, from, to)
            .entrySet()
            .stream()
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream().map(ReservationResponse::from).toList()
            ));

        List<DailyAgendaItemResponse> days = grouped.entrySet().stream()
            .map(entry -> new DailyAgendaItemResponse(entry.getKey(), entry.getValue()))
            .sorted(java.util.Comparator.comparing(DailyAgendaItemResponse::day))
            .toList();
        return new DailyAgendaResponse(days);
    }
}
