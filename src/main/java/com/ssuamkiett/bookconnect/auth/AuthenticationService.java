package com.ssuamkiett.bookconnect.auth;


import com.ssuamkiett.bookconnect.exception.OperationNotPermittedException;
import com.ssuamkiett.bookconnect.role.Role;
import com.ssuamkiett.bookconnect.role.RoleRepository;
import com.ssuamkiett.bookconnect.security.JwtService;
import com.ssuamkiett.bookconnect.token.RefreshToken;
import com.ssuamkiett.bookconnect.token.RefreshTokenRepository;
import com.ssuamkiett.bookconnect.user.User;
import com.ssuamkiett.bookconnect.user.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final int REFRESH_TOKEN_VALIDITY_DAYS = 10;

    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void register(RegistrationRequest request) {
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Role USER not initialized"));

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new OperationNotPermittedException("User already exists");
        }

        User user = createUserFromRequest(request, userRole);
        userRepository.save(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        User user = (User) authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        ).getPrincipal();

        String refreshToken = generateAndSaveRefreshToken(user);
        String accessToken = jwtService.generateAccessToken(createClaims(user), user);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new OperationNotPermittedException("Refresh token required");
        }

        String refreshToken = authHeader.substring(7);
        String userEmail;
        try {
            userEmail = jwtService.extractUsername(refreshToken);
            if (userEmail == null || !isValidToken(refreshToken, userEmail)) {
                throw new OperationNotPermittedException("Invalid refresh token");
            }
        }
        catch (JwtException e) {
            throw new OperationNotPermittedException("Error while validating token");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new OperationNotPermittedException("User not found"));

        String accessToken = jwtService.generateAccessToken(createClaims(user), user);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private User createUserFromRequest(RegistrationRequest request, Role userRole) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(true)
                .roles(List.of(userRole))
                .build();
    }

    private String generateAndSaveRefreshToken(User user) {
        String refreshToken = jwtService.generateRefreshToken(user);
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByEmail(user.getEmail())
                .orElseGet(() -> RefreshToken.builder()
                        .email(user.getEmail())
                        .build()
                );

        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setCreatedAt(LocalDateTime.now());
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_VALIDITY_DAYS));
        refreshTokenEntity.setUser(user);
        refreshTokenRepository.save(refreshTokenEntity);
        return refreshToken;
    }

    private HashMap<String, Object> createClaims(User user) {
        var claims = new HashMap<String, Object>();
        claims.put("username", user.getUsername());
        return claims;
    }

    private boolean isValidToken(String refreshToken, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new OperationNotPermittedException("User not found"));

        return jwtService.isValidToken(refreshToken, user);
    }
}
