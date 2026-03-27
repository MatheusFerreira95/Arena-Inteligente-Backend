package com.arenainteligente.core.interfaces.rest;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class HealthProbeController {

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("service", "core-service", "status", "ok");
    }
}
