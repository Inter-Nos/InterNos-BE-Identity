package app.internos.servicea.controller;

import app.internos.servicea.dto.request.LoginReq;
import app.internos.servicea.dto.request.RegisterReq;
import app.internos.servicea.dto.response.SessionResp;
import app.internos.servicea.dto.response.UserResp;
import app.internos.servicea.domain.user.AppUser;
import app.internos.servicea.service.AuthService;
import app.internos.servicea.service.CsrfTokenService;
import app.internos.servicea.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final SessionService sessionService;
    private final CsrfTokenService csrfTokenService;
    
    /**
     * POST /auth/register - Register a new user
     * Creates user and automatically logs in
     */
    @PostMapping("/register")
    public ResponseEntity<UserResp> register(
            @Valid @RequestBody RegisterReq request,
            HttpServletRequest httpRequest) {
        
        AppUser user = authService.register(request);
        
        // Automatically create session (auto-login)
        sessionService.createSession(httpRequest, user);
        
        UserResp response = UserResp.builder()
                .user(UserResp.User.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .createdAt(user.getCreatedAt())
                        .build())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * POST /auth/login - Login with username and password
     * Creates session and returns user info
     */
    @PostMapping("/login")
    public ResponseEntity<UserResp> login(
            @Valid @RequestBody LoginReq request,
            HttpServletRequest httpRequest) {
        
        AppUser user = authService.login(request);
        
        // Create session
        sessionService.createSession(httpRequest, user);
        
        UserResp response = UserResp.builder()
                .user(UserResp.User.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .createdAt(user.getCreatedAt())
                        .build())
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /auth/logout - Logout and invalidate session
     * Requires CSRF token
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        sessionService.invalidateSession(request);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * GET /auth/session - Get current session info and CSRF token
     * Returns authentication status, user info, and CSRF token
     * Sets XSRF-TOKEN cookie and returns csrfToken in JSON
     */
    @GetMapping("/session")
    public ResponseEntity<SessionResp> getSession(
            HttpServletRequest request,
            HttpServletResponse response) {
        
        Long userId = sessionService.getCurrentUserId(request);
        
        if (userId == null) {
            // Not authenticated - return empty session
            String csrfToken = csrfTokenService.generateToken();
            csrfTokenService.setCsrfTokenInCookie(response, csrfToken);
            
            return ResponseEntity.ok(SessionResp.builder()
                    .authenticated(false)
                    .user(null)
                    .csrfToken(csrfToken)
                    .build());
        }
        
        // Get or generate CSRF token
        String existingToken = csrfTokenService.getCsrfTokenFromCookie(request);
        String csrfToken = existingToken != null 
                ? existingToken 
                : csrfTokenService.generateToken();
        
        // Set CSRF token in cookie
        csrfTokenService.setCsrfTokenInCookie(response, csrfToken);
        
        // TODO: Get user info from session or DB
        // For now, return basic info
        SessionResp.UserInfo userInfo = SessionResp.UserInfo.builder()
                .id(userId)
                .username("") // TODO: Get from session or DB
                .build();
        
        return ResponseEntity.ok(SessionResp.builder()
                .authenticated(true)
                .user(userInfo)
                .csrfToken(csrfToken)
                .build());
    }
}

