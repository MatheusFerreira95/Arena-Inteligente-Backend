package com.arenainteligente.core.interfaces.rest.courts;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AddUnavailabilityBlockRequest(
    @NotNull @Future LocalDateTime startAt,
    @NotNull @Future LocalDateTime endAt,
    @NotBlank String reason
) {
}
