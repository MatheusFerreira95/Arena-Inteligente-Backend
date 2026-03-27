package com.arenainteligente.core.interfaces.rest.courts;

import com.arenainteligente.core.domain.courts.Court;
import com.arenainteligente.core.domain.courts.CourtStatus;
import com.arenainteligente.core.domain.courts.SportType;

public record CourtResponse(
    Long id,
    String tenantId,
    String name,
    SportType sportType,
    Integer capacity,
    CourtStatus status
) {
    public static CourtResponse from(Court court) {
        return new CourtResponse(
            court.getId(),
            court.getTenantId(),
            court.getName(),
            court.getSportType(),
            court.getCapacity(),
            court.getStatus()
        );
    }
}
