package com.arenainteligente.core.infrastructure.repository;

import com.arenainteligente.core.domain.courts.CourtAvailabilityWindow;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourtAvailabilityRepository extends JpaRepository<CourtAvailabilityWindow, Long> {
    List<CourtAvailabilityWindow> findByTenantIdAndCourtId(String tenantId, Long courtId);
}
