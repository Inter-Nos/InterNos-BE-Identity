package app.internos.servicea.service;

import app.internos.common.exception.InvalidCredentialsException;
import app.internos.common.exception.UsernameAlreadyExistsException;
import app.internos.servicea.domain.user.AppUser;
import app.internos.servicea.domain.user.UserRepository;
import app.internos.servicea.dto.request.LoginReq;
import app.internos.servicea.dto.request.RegisterReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Register a new user
     * Username is case-insensitive unique (CI-unique)
     * Password is hashed using BCrypt
     */
    @Transactional
    public AppUser register(RegisterReq request) {
        // Check if username already exists (case-insensitive)
        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new UsernameAlreadyExistsException(request.getUsername());
        }
        
        // Hash password
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        
        // Create and save user
        AppUser user = AppUser.builder()
                .username(request.getUsername())
                .passwordHash(hashedPassword)
                .build();
        
        AppUser savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());
        
        return savedUser;
    }
    
    /**
     * Login and verify credentials
     * Username is case-insensitive lookup
     */
    @Transactional(readOnly = true)
    public AppUser login(LoginReq request) {
        // Find user by username (case-insensitive)
        AppUser user = userRepository.findByUsernameIgnoreCase(request.getUsername())
                .orElseThrow(InvalidCredentialsException::new);
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        
        log.info("User logged in successfully: {}", user.getUsername());
        
        return user;
    }
}

