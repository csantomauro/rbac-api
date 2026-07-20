package com.cs.rbac_api.service;

import com.cs.rbac_api.dto.RegisterRequestDto;
import com.cs.rbac_api.exception.UserAlreadyExistsException;
import com.cs.rbac_api.model.Role;
import com.cs.rbac_api.model.User;
import com.cs.rbac_api.repository.UserRepository;
import com.cs.rbac_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public String register(RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already taken: " + request.getUsername());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.VIEWER);

        User saved = userRepository.save(user);
        return jwtService.generateToken(saved);
    }

    public String login(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User should exist after authentication"));

        return jwtService.generateToken(user);
    }
}