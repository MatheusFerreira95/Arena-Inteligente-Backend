package com.arenainteligente.core.interfaces.rest.reservations;

import java.util.List;

public record AgendaQueryResponse(List<ReservationResponse> reservations) {
}
