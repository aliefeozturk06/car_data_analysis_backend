package com.infodif.car_data_analysis.service;

import com.infodif.car_data_analysis.dto.AuthRequestDTO;
import com.infodif.car_data_analysis.dto.AuthResponseDTO;
import com.infodif.car_data_analysis.entity.Role;
import com.infodif.car_data_analysis.entity.User;
import com.infodif.car_data_analysis.repository.UserRepository;
import com.infodif.car_data_analysis.security.CustomUserDetailsService;
import com.infodif.car_data_analysis.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDTO login(AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        String token = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        return new AuthResponseDTO(
                token,
                user.getUsername(),
                user.getRole().name(),
                user.getBalance()
        );
    }

    @Transactional
    public String register(AuthRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("This username already taken!");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .balance(BigDecimal.valueOf(100000))
                .build();

        userRepository.save(user);

        return "Kayıt başarıyla tamamlandı, şimdi giriş yapabilirsin!";
    }
}