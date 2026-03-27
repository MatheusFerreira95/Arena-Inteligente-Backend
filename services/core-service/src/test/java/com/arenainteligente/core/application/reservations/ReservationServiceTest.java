package com.arenainteligente.core.application.reservations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.arenainteligente.core.application.exception.ConflictException;
import com.arenainteligente.core.domain.courts.Court;
import com.arenainteligente.core.domain.courts.CourtStatus;
import com.arenainteligente.core.domain.courts.SportType;
import com.arenainteligente.core.domain.reservations.ReservationStatus;
import com.arenainteligente.core.infrastructure.repository.CourtRepository;
import com.arenainteligente.core.infrastructure.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private ReservationRepository reservationRepository;

    private ReservationService reservationService;

    @BeforeEach
    void setup() {
        reservationService = new ReservationService(courtRepository, reservationRepository);
    }

    @Test
    void shouldRejectWhenThereIsTimeConflict() {
        String tenantId = "tenant-a";
        Long courtId = 10L;
        LocalDateTime startAt = LocalDateTime.now().plusDays(1).withHour(19).withMinute(0);
        LocalDateTime endAt = startAt.plusHours(1);

        Court court = new Court(tenantId, "Quadra 1", SportType.FUTSAL, 12);
        court.update("Quadra 1", SportType.FUTSAL, 12, CourtStatus.ACTIVE);

        when(courtRepository.findByIdAndTenantId(courtId, tenantId)).thenReturn(Optional.of(court));
        when(reservationRepository.hasConflict(
            eq(tenantId),
            eq(courtId),
            eq(ReservationStatus.CONFIRMED),
            any(LocalDateTime.class),
            any(LocalDateTime.class)
        )).thenReturn(true);

        assertThrows(
            ConflictException.class,
            () -> reservationService.create(tenantId, courtId, "customer-1", startAt, endAt)
        );
    }
}
