package com.arenainteligente.core.interfaces.rest.reservations;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateReservationRequest(
    @NotNull Long courtId,
    @NotBlank String customerUserId,
    @NotNull @Future LocalDateTime startAt,
    @NotNull @Future LocalDateTime endAt
) {
}
