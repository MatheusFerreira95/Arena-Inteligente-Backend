package com.arenainteligente.core.application.courts;

import com.arenainteligente.core.application.exception.NotFoundException;
import com.arenainteligente.core.application.exception.ConflictException;
import com.arenainteligente.core.domain.courts.Court;
import com.arenainteligente.core.domain.courts.CourtAvailabilityWindow;
import com.arenainteligente.core.domain.courts.CourtStatus;
import com.arenainteligente.core.domain.courts.CourtUnavailabilityBlock;
import com.arenainteligente.core.domain.courts.SportType;
import com.arenainteligente.core.infrastructure.repository.CourtAvailabilityRepository;
import com.arenainteligente.core.infrastructure.repository.CourtRepository;
import com.arenainteligente.core.infrastructure.repository.CourtUnavailabilityBlockRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourtService {

    private final CourtRepository courtRepository;
    private final CourtAvailabilityRepository courtAvailabilityRepository;
    private final CourtUnavailabilityBlockRepository courtUnavailabilityBlockRepository;

    public CourtService(
        CourtRepository courtRepository,
        CourtAvailabilityRepository courtAvailabilityRepository,
        CourtUnavailabilityBlockRepository courtUnavailabilityBlockRepository
    ) {
        this.courtRepository = courtRepository;
        this.courtAvailabilityRepository = courtAvailabilityRepository;
        this.courtUnavailabilityBlockRepository = courtUnavailabilityBlockRepository;
    }

    public List<Court> listByTenant(String tenantId) {
        return courtRepository.findByTenantId(tenantId);
    }

    @Transactional
    public Court create(String tenantId, String name, SportType sportType, Integer capacity) {
        return courtRepository.save(new Court(tenantId, name, sportType, capacity));
    }

    @Transactional
    public Court update(String tenantId, Long courtId, String name, SportType sportType, Integer capacity, CourtStatus status) {
        Court court = courtRepository.findByIdAndTenantId(courtId, tenantId)
            .orElseThrow(() -> new NotFoundException("Court not found"));
        court.update(name, sportType, capacity, status);
        return courtRepository.save(court);
    }

    @Transactional
    public CourtAvailabilityWindow addAvailability(
        String tenantId,
        Long courtId,
        Integer dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
    ) {
        courtRepository.findByIdAndTenantId(courtId, tenantId)
            .orElseThrow(() -> new NotFoundException("Court not found"));
        return courtAvailabilityRepository.save(new CourtAvailabilityWindow(tenantId, courtId, dayOfWeek, startTime, endTime));
    }

    @Transactional
    public CourtUnavailabilityBlock addUnavailabilityBlock(
        String tenantId,
        Long courtId,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String reason
    ) {
        if (!startAt.isBefore(endAt)) {
            throw new ConflictException("Invalid block interval");
        }
        courtRepository.findByIdAndTenantId(courtId, tenantId)
            .orElseThrow(() -> new NotFoundException("Court not found"));

        return courtUnavailabilityBlockRepository.save(
            new CourtUnavailabilityBlock(tenantId, courtId, startAt, endAt, reason)
        );
    }
}
