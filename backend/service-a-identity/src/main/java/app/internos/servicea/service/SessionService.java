package app.internos.servicea.service;

import app.internos.servicea.domain.session.UserSession;
import app.internos.servicea.domain.session.UserSessionRepository;
import app.internos.servicea.domain.user.AppUser;
import app.internos.servicea.util.IpHashUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {
    
    private final UserSessionRepository userSessionRepository;
    private final IpHashUtil ipHashUtil;
    
    private static final String SESSION_USER_ID_ATTR = "userId";
    private static final String SESSION_USERNAME_ATTR = "username";
    private static final String SESSION_FINGERPRINT_ATTR = "sessionFingerprint";
    private static final int SESSION_TIMEOUT_SECONDS = 86400; // 24 hours
    
    /**
     * Create a new session for the user
     * Store session in HttpSession (Spring Security default) and track in DB
     */
    @Transactional
    public void createSession(HttpServletRequest request, AppUser user) {
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
        
        // Generate session fingerprint
        String sessionFingerprint = UUID.randomUUID().toString();
        
        // Get client IP and hash it
        String clientIp = getClientIp(request);
        String ipHash = ipHashUtil.hashIp(clientIp);
        
        // Store user info in session
        session.setAttribute(SESSION_USER_ID_ATTR, user.getId());
        session.setAttribute(SESSION_USERNAME_ATTR, user.getUsername());
        session.setAttribute(SESSION_FINGERPRINT_ATTR, sessionFingerprint);
        
        // Track session in DB
        UserSession userSession = UserSession.builder()
                .user(user)
                .sessionFingerprint(sessionFingerprint)
                .ipHash(ipHash)
                .expiresAt(Instant.now().plusSeconds(SESSION_TIMEOUT_SECONDS))
                .build();
        
        userSessionRepository.save(userSession);
        
        log.info("Session created for user: {}, fingerprint: {}", user.getUsername(), sessionFingerprint);
    }
    
    /**
     * Get current session user ID from HttpSession
     */
    public Long getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        
        return (Long) session.getAttribute(SESSION_USER_ID_ATTR);
    }
    
    /**
     * Get current session username from HttpSession
     */
    public String getCurrentUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        
        return (String) session.getAttribute(SESSION_USERNAME_ATTR);
    }
    
    /**
     * Invalidate session
     * Remove session and mark as expired in DB
     */
    @Transactional
    public void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        
        String sessionFingerprint = (String) session.getAttribute(SESSION_FINGERPRINT_ATTR);
        
        // Invalidate session
        session.invalidate();
        
        // Remove from DB
        if (sessionFingerprint != null) {
            userSessionRepository.deleteBySessionFingerprint(sessionFingerprint);
            log.info("Session invalidated: {}", sessionFingerprint);
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

