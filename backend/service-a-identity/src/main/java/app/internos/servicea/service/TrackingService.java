package app.internos.servicea.service;

import app.internos.servicea.domain.user.AppUser;
import app.internos.servicea.domain.user.UserRepository;
import app.internos.servicea.domain.visit.VisitLogUser;
import app.internos.servicea.domain.visit.VisitLogUserRepository;
import app.internos.servicea.dto.request.TrackUserVisitReq;
import app.internos.servicea.util.IpHashUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingService {
    
    private final VisitLogUserRepository visitLogUserRepository;
    private final UserRepository userRepository;
    private final IpHashUtil ipHashUtil;
    
    /**
     * Track a user profile visit asynchronously
     * Returns immediately (202 Accepted), saves to DB asynchronously
     */
    @Async
    @Transactional
    public void trackUserVisit(TrackUserVisitReq request, HttpServletRequest httpRequest) {
        try {
            // Get owner
            AppUser owner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Owner not found: " + request.getOwnerId()));
            
            // Get client IP and hash it
            String clientIp = getClientIp(httpRequest);
            String ipHash = ipHashUtil.hashIp(clientIp);
            
            // Create visit log
            VisitLogUser visitLog = VisitLogUser.builder()
                    .owner(owner)
                    .visitorAnonId(request.getVisitorAnonId())
                    .ipHash(ipHash)
                    .build();
            
            visitLogUserRepository.save(visitLog);
            
            log.debug("User visit tracked: ownerId={}, visitorAnonId={}", 
                    request.getOwnerId(), request.getVisitorAnonId());
        } catch (Exception e) {
            log.error("Failed to track user visit", e);
            // Don't throw exception - async method shouldn't fail silently in production
            // but we don't want to break the request flow
        }
    }
    
    /**
     * Get client IP address from request
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}

