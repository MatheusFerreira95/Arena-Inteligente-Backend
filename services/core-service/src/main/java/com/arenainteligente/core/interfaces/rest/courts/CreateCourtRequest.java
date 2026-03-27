package com.arenainteligente.core.interfaces.rest.courts;

import com.arenainteligente.core.domain.courts.SportType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCourtRequest(
    @NotBlank String name,
    @NotNull SportType sportType,
    @NotNull @Min(1) Integer capacity
) {
}
