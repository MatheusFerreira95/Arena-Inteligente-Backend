package com.arenainteligente.core.application.reservations;

import com.arenainteligente.core.application.exception.ConflictException;
import com.arenainteligente.core.application.exception.NotFoundException;
import com.arenainteligente.core.domain.courts.CourtAvailabilityWindow;
import com.arenainteligente.core.domain.courts.Court;
import com.arenainteligente.core.domain.courts.CourtStatus;
import com.arenainteligente.core.infrastructure.repository.CourtAvailabilityRepository;
import com.arenainteligente.core.domain.reservations.Reservation;
import com.arenainteligente.core.domain.reservations.ReservationStatus;
import com.arenainteligente.core.infrastructure.repository.CourtRepository;
import com.arenainteligente.core.infrastructure.repository.CourtUnavailabilityBlockRepository;
import com.arenainteligente.core.infrastructure.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private final CourtRepository courtRepository;
    private final CourtAvailabilityRepository courtAvailabilityRepository;
    private final CourtUnavailabilityBlockRepository courtUnavailabilityBlockRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(
        CourtRepository courtRepository,
        CourtAvailabilityRepository courtAvailabilityRepository,
        CourtUnavailabilityBlockRepository courtUnavailabilityBlockRepository,
        ReservationRepository reservationRepository
    ) {
        this.courtRepository = courtRepository;
        this.courtAvailabilityRepository = courtAvailabilityRepository;
        this.courtUnavailabilityBlockRepository = courtUnavailabilityBlockRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public Reservation create(String tenantId, Long courtId, String customerUserId, LocalDateTime startAt, LocalDateTime endAt) {
        if (!startAt.isBefore(endAt)) {
            throw new ConflictException("Invalid reservation interval");
        }
        if (!startAt.toLocalDate().isEqual(endAt.toLocalDate())) {
            throw new ConflictException("Cross-day reservations are not supported in this slice");
        }

        Court court = courtRepository.findByIdAndTenantId(courtId, tenantId)
            .orElseThrow(() -> new NotFoundException("Court not found"));

        if (court.getStatus() != CourtStatus.ACTIVE) {
            throw new ConflictException("Court is not active");
        }

        validateAvailabilityWindow(tenantId, courtId, startAt, endAt);
        validateUnavailabilityBlocks(tenantId, courtId, startAt, endAt);

        boolean hasConflict = reservationRepository.hasConflict(
            tenantId,
            courtId,
            ReservationStatus.CONFIRMED,
            startAt,
            endAt
        );
        if (hasConflict) {
            throw new ConflictException("Reservation time conflict detected");
        }

        Reservation reservation = new Reservation(tenantId, courtId, customerUserId, startAt, endAt);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> agenda(String tenantId, Long courtId, LocalDateTime from, LocalDateTime to) {
        if (!from.isBefore(to)) {
            throw new ConflictException("Invalid agenda interval");
        }
        return reservationRepository.findByTenantIdAndCourtIdAndStartAtLessThanAndEndAtGreaterThan(
            tenantId,
            courtId,
            to,
            from
        );
    }

    @Transactional
    public Reservation cancel(String tenantId, Long reservationId) {
        Reservation reservation = reservationRepository.findByIdAndTenantId(reservationId, tenantId)
            .orElseThrow(() -> new NotFoundException("Reservation not found"));
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            return reservation;
        }
        reservation.cancel();
        return reservationRepository.save(reservation);
    }

    public Map<LocalDate, List<Reservation>> agendaByDay(String tenantId, Long courtId, LocalDateTime from, LocalDateTime to) {
        List<Reservation> reservations = agenda(tenantId, courtId, from, to);
        return reservations.stream()
            .collect(Collectors.groupingBy(res -> res.getStartAt().toLocalDate()));
    }

    private void validateAvailabilityWindow(String tenantId, Long courtId, LocalDateTime startAt, LocalDateTime endAt) {
        int dayOfWeek = startAt.getDayOfWeek().getValue();
        List<CourtAvailabilityWindow> windows = courtAvailabilityRepository.findByTenantIdAndCourtIdAndDayOfWeek(
            tenantId, courtId, dayOfWeek
        );
        if (windows.isEmpty()) {
            throw new ConflictException("Court has no availability for selected day");
        }

        LocalTime startTime = startAt.toLocalTime();
        LocalTime endTime = endAt.toLocalTime();
        boolean withinAnyWindow = windows.stream().anyMatch(window ->
            !startTime.isBefore(window.getStartTime()) && !endTime.isAfter(window.getEndTime())
        );
        if (!withinAnyWindow) {
            throw new ConflictException("Reservation is outside court availability window");
        }
    }

    private void validateUnavailabilityBlocks(String tenantId, Long courtId, LocalDateTime startAt, LocalDateTime endAt) {
        boolean blocked = courtUnavailabilityBlockRepository.hasOverlap(tenantId, courtId, startAt, endAt);
        if (blocked) {
            throw new ConflictException("Court is blocked for this period");
        }
    }
}
