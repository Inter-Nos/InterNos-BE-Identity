package app.internos.servicea.controller;

import app.internos.servicea.dto.response.DashboardResp;
import app.internos.servicea.service.DashboardService;
import app.internos.servicea.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;
    private final SessionService sessionService;
    
    /**
     * GET /me/dashboard - Get user dashboard data
     * Returns KPI and summary (profile visits + room metrics aggregate)
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResp> getDashboard(
            @RequestParam(value = "range", defaultValue = "24h") String range,
            HttpServletRequest request) {
        
        Long userId = sessionService.getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        DashboardResp response = dashboardService.getDashboard(userId, range);
        
        return ResponseEntity.ok(response);
    }
}

