package com.arenainteligente.core.application.reservations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.arenainteligente.core.application.exception.ConflictException;
import com.arenainteligente.core.domain.courts.Court;
import com.arenainteligente.core.domain.courts.CourtAvailabilityWindow;
import com.arenainteligente.core.domain.courts.CourtStatus;
import com.arenainteligente.core.domain.courts.SportType;
import com.arenainteligente.core.domain.reservations.Reservation;
import com.arenainteligente.core.domain.reservations.ReservationStatus;
import com.arenainteligente.core.infrastructure.repository.CourtAvailabilityRepository;
import com.arenainteligente.core.infrastructure.repository.CourtRepository;
import com.arenainteligente.core.infrastructure.repository.CourtUnavailabilityBlockRepository;
import com.arenainteligente.core.infrastructure.repository.ReservationAuditEventRepository;
import com.arenainteligente.core.infrastructure.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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

    @Mock
    private CourtAvailabilityRepository courtAvailabilityRepository;

    @Mock
    private CourtUnavailabilityBlockRepository courtUnavailabilityBlockRepository;

    @Mock
    private ReservationAuditEventRepository reservationAuditEventRepository;

    private ReservationService reservationService;

    @BeforeEach
    void setup() {
        reservationService = new ReservationService(
            courtRepository,
            courtAvailabilityRepository,
            courtUnavailabilityBlockRepository,
            reservationRepository,
            reservationAuditEventRepository
        );
    }

    @Test
    void shouldRejectWhenThereIsTimeConflict() {
        String tenantId = "tenant-a";
        Long courtId = 10L;
        LocalDateTime startAt = LocalDateTime.now().plusDays(1).withHour(19).withMinute(0);
        LocalDateTime endAt = startAt.plusHours(1);

        Court court = new Court(tenantId, "Quadra 1", SportType.FUTSAL, 12);
        court.update("Quadra 1", SportType.FUTSAL, 12, CourtStatus.ACTIVE);
        CourtAvailabilityWindow window = new CourtAvailabilityWindow(
            tenantId,
            courtId,
            startAt.getDayOfWeek().getValue(),
            LocalTime.of(18, 0),
            LocalTime.of(22, 0)
        );

        when(courtRepository.findByIdAndTenantId(courtId, tenantId)).thenReturn(Optional.of(court));
        when(courtAvailabilityRepository.findByTenantIdAndCourtIdAndDayOfWeek(
            eq(tenantId), eq(courtId), eq(startAt.getDayOfWeek().getValue())
        )).thenReturn(List.of(window));
        when(courtUnavailabilityBlockRepository.hasOverlap(eq(tenantId), eq(courtId), any(), any())).thenReturn(false);
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

    @Test
    void shouldRejectWhenReservationIsOutsideAvailabilityWindow() {
        String tenantId = "tenant-a";
        Long courtId = 10L;
        LocalDateTime startAt = LocalDateTime.now().plusDays(1).withHour(17).withMinute(0);
        LocalDateTime endAt = startAt.plusHours(1);

        Court court = new Court(tenantId, "Quadra 1", SportType.FUTSAL, 12);
        court.update("Quadra 1", SportType.FUTSAL, 12, CourtStatus.ACTIVE);
        CourtAvailabilityWindow window = new CourtAvailabilityWindow(
            tenantId,
            courtId,
            startAt.getDayOfWeek().getValue(),
            LocalTime.of(18, 0),
            LocalTime.of(22, 0)
        );

        when(courtRepository.findByIdAndTenantId(courtId, tenantId)).thenReturn(Optional.of(court));
        when(courtAvailabilityRepository.findByTenantIdAndCourtIdAndDayOfWeek(
            eq(tenantId), eq(courtId), eq(startAt.getDayOfWeek().getValue())
        )).thenReturn(List.of(window));

        assertThrows(
            ConflictException.class,
            () -> reservationService.create(tenantId, courtId, "customer-1", startAt, endAt)
        );
    }

    @Test
    void shouldCancelReservation() {
        String tenantId = "tenant-a";
        Long courtId = 10L;
        Long reservationId = 99L;
        LocalDateTime startAt = LocalDateTime.now().plusDays(1).withHour(19).withMinute(0);
        LocalDateTime endAt = startAt.plusHours(1);
        Reservation reservation = new Reservation(tenantId, courtId, "customer-1", startAt, endAt);

        when(reservationRepository.findByIdAndTenantId(reservationId, tenantId)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reservation cancelled = reservationService.cancel(tenantId, reservationId, "cliente pediu cancelamento", "owner-1");

        org.junit.jupiter.api.Assertions.assertEquals(ReservationStatus.CANCELLED, cancelled.getStatus());
        org.junit.jupiter.api.Assertions.assertNotNull(cancelled.getCancelledAt());
        org.junit.jupiter.api.Assertions.assertEquals("cliente pediu cancelamento", cancelled.getCancelReason());
    }

    @Test
    void shouldGroupAgendaByDay() {
        String tenantId = "tenant-a";
        Long courtId = 10L;
        LocalDateTime from = LocalDateTime.now().plusDays(1).withHour(8).withMinute(0);
        LocalDateTime to = from.plusDays(2);

        Reservation r1 = new Reservation(tenantId, courtId, "customer-1", from.withHour(10), from.withHour(11));
        LocalDateTime nextDay = from.plusDays(1);
        Reservation r2 = new Reservation(tenantId, courtId, "customer-2", nextDay.withHour(12), nextDay.withHour(13));

        when(reservationRepository.findByTenantIdAndCourtIdAndStartAtLessThanAndEndAtGreaterThan(tenantId, courtId, to, from))
            .thenReturn(List.of(r1, r2));

        Map<LocalDate, List<Reservation>> grouped = reservationService.agendaByDay(tenantId, courtId, from, to);

        org.junit.jupiter.api.Assertions.assertEquals(2, grouped.size());
    }
}
