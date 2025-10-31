package app.internos.servicea.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class CsrfDoubleSubmitFilter extends OncePerRequestFilter {
    
    private static final String CSRF_HEADER_NAME = "X-CSRF-Token";
    private static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        if (isStateChanging(request)) {
            String headerToken = request.getHeader(CSRF_HEADER_NAME);
            String cookieToken = extractCsrfTokenFromCookie(request);
            
            if (headerToken == null || cookieToken == null || !Objects.equals(headerToken, cookieToken)) {
                log.warn("CSRF token validation failed. Header: {}, Cookie: {}", headerToken, cookieToken);
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":{\"code\":\"CSRF_ERROR\",\"message\":\"Invalid CSRF token\"}}");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isStateChanging(HttpServletRequest request) {
        String method = request.getMethod();
        return "POST".equals(method) || "PUT".equals(method) || 
               "PATCH".equals(method) || "DELETE".equals(method);
    }
    
    private String extractCsrfTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        
        return Arrays.stream(cookies)
                .filter(cookie -> CSRF_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}

