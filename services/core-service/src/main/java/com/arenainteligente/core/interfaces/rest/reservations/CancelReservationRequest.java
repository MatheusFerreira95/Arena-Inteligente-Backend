package com.arenainteligente.core.interfaces.rest.reservations;

import jakarta.validation.constraints.NotBlank;

public record CancelReservationRequest(@NotBlank String reason) {
}
