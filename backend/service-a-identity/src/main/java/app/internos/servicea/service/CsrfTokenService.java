package app.internos.servicea.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CsrfTokenService {
    
    private static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";
    private static final int TOKEN_LENGTH = 32;
    
    private final SecureRandom secureRandom;
    
    public CsrfTokenService() {
        this.secureRandom = new SecureRandom();
    }
    
    /**
     * Generate a new CSRF token
     * @return Base64 encoded CSRF token
     */
    public String generateToken() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
    
    /**
     * Set CSRF token in cookie and return token value
     * Cookie attributes: HttpOnly=false, Secure=true, SameSite=Lax
     */
    public String setCsrfTokenInCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(CSRF_COOKIE_NAME, token);
        cookie.setHttpOnly(false); // Must be readable by JavaScript
        cookie.setSecure(true); // HTTPS only
        cookie.setPath("/");
        cookie.setMaxAge(3600); // 1 hour
        // SameSite=Lax is set by Spring Session automatically
        
        response.addCookie(cookie);
        return token;
    }
    
    /**
     * Get CSRF token from cookie if exists
     */
    public String getCsrfTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        
        for (Cookie cookie : cookies) {
            if (CSRF_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}

