package app.internos.servicea.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
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
    private final RedisConnectionFactory redisConnectionFactory;
    
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
     * Checks DB and Redis connections
     * Used by K8s Readiness Probe
     */
    @GetMapping("/readiness")
    public ResponseEntity<Map<String, String>> readiness() {
        boolean dbOk = checkDatabase();
        boolean redisOk = checkRedis();
        
        if (dbOk && redisOk) {
            return ResponseEntity.ok(Map.of("status", "READY"));
        } else {
            log.warn("Readiness check failed: DB={}, Redis={}", dbOk, redisOk);
            return ResponseEntity.status(503)
                    .body(Map.of("status", "NOT_READY", 
                            "db", dbOk ? "OK" : "FAIL", 
                            "redis", redisOk ? "OK" : "FAIL"));
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
    
    private boolean checkRedis() {
        try {
            redisConnectionFactory.getConnection().ping();
            return true;
        } catch (Exception e) {
            log.error("Redis health check failed", e);
            return false;
        }
    }
}

