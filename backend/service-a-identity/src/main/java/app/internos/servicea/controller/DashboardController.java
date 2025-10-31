package app.internos.servicea.controller;

import app.internos.servicea.dto.response.DashboardResp;
import app.internos.servicea.dto.response.MyRoomsResp;
import app.internos.servicea.service.DashboardService;
import app.internos.servicea.service.MyRoomsService;
import app.internos.servicea.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    private final MyRoomsService myRoomsService;
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
    
    /**
     * GET /me/rooms - Get list of my rooms with per-room metrics
     * Returns rooms owned by current user (both PUBLIC and PRIVATE)
     * Includes metrics: visits, attempts, solved, solveRate
     */
    @GetMapping("/rooms")
    public ResponseEntity<MyRoomsResp> getMyRooms(
            @RequestParam(value = "limit", defaultValue = "50") @Min(1) @Max(100) Integer limit,
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
            HttpServletRequest request) {
        
        Long userId = sessionService.getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        MyRoomsResp response = myRoomsService.getMyRooms(userId, limit, offset);
        
        return ResponseEntity.ok(response);
    }
}

