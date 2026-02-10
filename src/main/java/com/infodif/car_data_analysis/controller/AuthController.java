package com.infodif.car_data_analysis.controller;

import com.infodif.car_data_analysis.dto.AuthRequestDTO;
import com.infodif.car_data_analysis.dto.AuthResponseDTO;
import com.infodif.car_data_analysis.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Frontend rahat erişsin diye
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody AuthRequestDTO request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public String register(@RequestBody AuthRequestDTO request) {
        return authService.register(request);
    }
}