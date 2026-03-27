package com.arenainteligente.core.application.reservations;

import com.arenainteligente.core.application.exception.ConflictException;
import com.arenainteligente.core.application.exception.NotFoundException;
import com.arenainteligente.core.domain.courts.Court;
import com.arenainteligente.core.domain.courts.CourtStatus;
import com.arenainteligente.core.domain.reservations.Reservation;
import com.arenainteligente.core.domain.reservations.ReservationStatus;
import com.arenainteligente.core.infrastructure.repository.CourtRepository;
import com.arenainteligente.core.infrastructure.repository.ReservationRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private final CourtRepository courtRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(CourtRepository courtRepository, ReservationRepository reservationRepository) {
        this.courtRepository = courtRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public Reservation create(String tenantId, Long courtId, String customerUserId, LocalDateTime startAt, LocalDateTime endAt) {
        if (!startAt.isBefore(endAt)) {
            throw new ConflictException("Invalid reservation interval");
        }

        Court court = courtRepository.findByIdAndTenantId(courtId, tenantId)
            .orElseThrow(() -> new NotFoundException("Court not found"));

        if (court.getStatus() != CourtStatus.ACTIVE) {
            throw new ConflictException("Court is not active");
        }

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
}
