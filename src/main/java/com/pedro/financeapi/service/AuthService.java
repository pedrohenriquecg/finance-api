package com.pedro.financeapi.service;

import com.pedro.financeapi.dto.AuthLoginRequest;
import com.pedro.financeapi.dto.AuthRegisterRequest;
import com.pedro.financeapi.dto.AuthResponse;
import com.pedro.financeapi.dto.UserResponse;
import com.pedro.financeapi.exception.InvalidCredentialsException;
import com.pedro.financeapi.exception.ResourceConflictException;
import com.pedro.financeapi.model.User;
import com.pedro.financeapi.repository.UserRepository;
import com.pedro.financeapi.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(AuthRegisterRequest request) {
        String email = normalizeEmail(request.getEmail());

        if (userRepository.existsByEmail(email)) {
            throw new ResourceConflictException("Email is already in use");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(savedUser), new UserResponse(savedUser));
    }

    public AuthResponse login(AuthLoginRequest request) {
        String email = normalizeEmail(request.getEmail());

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return new AuthResponse(jwtService.generateToken(user), new UserResponse(user));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
