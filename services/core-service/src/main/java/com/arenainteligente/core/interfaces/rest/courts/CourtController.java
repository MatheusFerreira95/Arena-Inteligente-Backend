package com.arenainteligente.core.interfaces.rest.courts;

import com.arenainteligente.core.application.courts.CourtService;
import com.arenainteligente.core.domain.courts.Court;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/courts")
public class CourtController {

    private final CourtService courtService;

    public CourtController(CourtService courtService) {
        this.courtService = courtService;
    }

    @GetMapping
    public List<CourtResponse> listByTenant(@RequestHeader("X-Tenant-Id") String tenantId) {
        return courtService.listByTenant(tenantId).stream().map(CourtResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourtResponse create(
        @RequestHeader("X-Tenant-Id") String tenantId,
        @Valid @RequestBody CreateCourtRequest request
    ) {
        Court created = courtService.create(tenantId, request.name(), request.sportType(), request.capacity());
        return CourtResponse.from(created);
    }

    @PutMapping("/{courtId}")
    public CourtResponse update(
        @RequestHeader("X-Tenant-Id") String tenantId,
        @PathVariable Long courtId,
        @Valid @RequestBody UpdateCourtRequest request
    ) {
        Court updated = courtService.update(
            tenantId,
            courtId,
            request.name(),
            request.sportType(),
            request.capacity(),
            request.status()
        );
        return CourtResponse.from(updated);
    }

    @PostMapping("/{courtId}/availability")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAvailability(
        @RequestHeader("X-Tenant-Id") String tenantId,
        @PathVariable Long courtId,
        @Valid @RequestBody AddAvailabilityRequest request
    ) {
        courtService.addAvailability(tenantId, courtId, request.dayOfWeek(), request.startTime(), request.endTime());
    }

    @PostMapping("/{courtId}/blocks")
    @ResponseStatus(HttpStatus.CREATED)
    public void addUnavailabilityBlock(
        @RequestHeader("X-Tenant-Id") String tenantId,
        @PathVariable Long courtId,
        @Valid @RequestBody AddUnavailabilityBlockRequest request
    ) {
        courtService.addUnavailabilityBlock(
            tenantId,
            courtId,
            request.startAt(),
            request.endAt(),
            request.reason()
        );
    }
}
