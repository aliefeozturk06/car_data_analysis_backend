package com.infodif.car_data_analysis.service;

import com.infodif.car_data_analysis.dto.AuthRequestDTO;
import com.infodif.car_data_analysis.dto.AuthResponseDTO;
import com.infodif.car_data_analysis.entity.Role;
import com.infodif.car_data_analysis.entity.User;
import com.infodif.car_data_analysis.mapper.UserMapper;
import com.infodif.car_data_analysis.repository.UserRepository;
import com.infodif.car_data_analysis.security.CustomUserDetailsService;
import com.infodif.car_data_analysis.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AuthResponseDTO login(AuthRequestDTO request) {
        log.info("Attempting login for user: {}", request.username());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        String token = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        return userMapper.toAuthResponseDto(user, token);
    }

    @Transactional
    public String register(AuthRequestDTO request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("This username is already taken!");
        }

        log.info("Registering new user: {}", request.username());

        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                Role.ROLE_USER,
                BigDecimal.valueOf(100000)
        );

        userRepository.save(user);

        return "Sign up is completed, now you can log in!";
    }
}