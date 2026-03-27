package com.arenainteligente.core.interfaces.rest.reservations;

import java.time.LocalDate;
import java.util.List;

public record DailyAgendaItemResponse(LocalDate day, List<ReservationResponse> reservations) {
}
