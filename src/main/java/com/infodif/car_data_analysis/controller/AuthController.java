package com.infodif.car_data_analysis.controller;

import com.infodif.car_data_analysis.dto.AuthRequestDTO;
import com.infodif.car_data_analysis.dto.AuthResponseDTO;
import com.infodif.car_data_analysis.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody AuthRequestDTO request) {
        log.info("🔑 Log in attempt is done: {}", request.username());
        return authService.login(request);
    }

    @PostMapping("/register")
    public String register(@RequestBody AuthRequestDTO request) {
        log.info("📝 New user sign up request: {}", request.username());
        return authService.register(request);
    }
}