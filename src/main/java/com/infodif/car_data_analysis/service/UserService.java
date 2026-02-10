package com.infodif.car_data_analysis.service;

import com.infodif.car_data_analysis.dto.UserDTO;
import com.infodif.car_data_analysis.entity.User;
import com.infodif.car_data_analysis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Entity döner (Service içinden kullanım için)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    // DTO döner (Controller için)
    public UserDTO getUserDtoByUsername(String username) {
        User user = getUserByUsername(username);
        return mapToDto(user);
    }

    public BigDecimal getBalance(String username) {
        User user = getUserByUsername(username);
        return user.getBalance();
    }

    @Transactional
    public BigDecimal addBalance(String username, BigDecimal amount) {
        User user = getUserByUsername(username);

        // Mevcut bakiyenin üzerine ekle
        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);

        return user.getBalance();
    }

    private UserDTO mapToDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}