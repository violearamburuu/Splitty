

package com.violearamburuu.splitty.controller;

import com.violearamburuu.splitty.DTO.AuthResponse;
import com.violearamburuu.splitty.DTO.LoginRequest;
import com.violearamburuu.splitty.DTO.RegisterRequest;
import com.violearamburuu.splitty.model.User;
import com.violearamburuu.splitty.security.JwtUtil;
import com.violearamburuu.splitty.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        User user = userService.findUserByEmail(request.email());
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        return new AuthResponse(jwtUtil.generateToken(user.getEmail()));
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        User user = userService.createUser(request.name(), request.email(), request.password());
        return new AuthResponse(jwtUtil.generateToken(user.getEmail()));
    }
}
