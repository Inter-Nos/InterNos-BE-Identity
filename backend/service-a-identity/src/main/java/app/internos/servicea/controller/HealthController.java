package app.internos.servicea.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {
    
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * Liveness probe - check if application is alive
     * Used by K8s Liveness Probe
     */
    @GetMapping("/liveness")
    public ResponseEntity<Map<String, String>> liveness() {
        return ResponseEntity.ok(Map.of("status", "OK"));
    }
    
    /**
     * Readiness probe - check if application is ready to serve traffic
     * Checks DB connection
     * Used by K8s Readiness Probe
     */
    @GetMapping("/readiness")
    public ResponseEntity<Map<String, String>> readiness() {
        boolean dbOk = checkDatabase();
        
        if (dbOk) {
            return ResponseEntity.ok(Map.of("status", "READY"));
        } else {
            log.warn("Readiness check failed: DB={}", dbOk);
            return ResponseEntity.status(503)
                    .body(Map.of("status", "NOT_READY", 
                            "db", "FAIL"));
        }
    }
    
    private boolean checkDatabase() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return true;
        } catch (Exception e) {
            log.error("Database health check failed", e);
            return false;
        }
    }
}

