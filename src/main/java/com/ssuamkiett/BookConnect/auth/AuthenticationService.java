package com.ssuamkiett.BookConnect.auth;

import com.ssuamkiett.BookConnect.exception.OperationNotPermittedException;
import com.ssuamkiett.BookConnect.role.RoleRepository;
import com.ssuamkiett.BookConnect.security.JwtService;
import com.ssuamkiett.BookConnect.user.User;
import com.ssuamkiett.BookConnect.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void register(RegistrationRequest request) {
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Role USER not initialized"));
        boolean isUserExists = userRepository.existsByEmail(request.getEmail());
        if(isUserExists) {
            throw new OperationNotPermittedException("User already exists");
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(true)
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),
                        request.getPassword())
        );
        var claims = new HashMap<String, Object>();
        var user = (User) auth.getPrincipal();
        claims.put("fullName", user.fullName());
        var token = jwtService.generateAccessToken(claims, user);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }
}
