package com.arenainteligente.core.infrastructure.repository;

import com.arenainteligente.core.domain.courts.Court;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourtRepository extends JpaRepository<Court, Long> {
    List<Court> findByTenantId(String tenantId);
    Optional<Court> findByIdAndTenantId(Long id, String tenantId);
}
