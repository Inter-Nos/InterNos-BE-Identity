package app.internos.servicea.controller;

import app.internos.servicea.dto.request.TrackUserVisitReq;
import app.internos.servicea.dto.response.QueuedResp;
import app.internos.servicea.service.TrackingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/track")
@RequiredArgsConstructor
public class TrackingController {
    
    private final TrackingService trackingService;
    
    /**
     * POST /track/visit/user - Track a profile visit
     * Returns 202 Accepted immediately, processes asynchronously
     */
    @PostMapping("/visit/user")
    public ResponseEntity<QueuedResp> trackUserVisit(
            @Valid @RequestBody TrackUserVisitReq request,
            HttpServletRequest httpRequest) {
        
        // Process asynchronously
        trackingService.trackUserVisit(request, httpRequest);
        
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(QueuedResp.builder()
                        .queued(true)
                        .build());
    }
}

