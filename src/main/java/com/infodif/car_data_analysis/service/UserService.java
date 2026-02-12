package com.infodif.car_data_analysis.service;

import com.infodif.car_data_analysis.dto.UserDTO;
import com.infodif.car_data_analysis.entity.User;
import com.infodif.car_data_analysis.mapper.UserMapper;
import com.infodif.car_data_analysis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public UserDTO getUserDtoByUsername(String username) {
        User user = getUserByUsername(username);
        return userMapper.toDto(user);
    }

    public BigDecimal getBalance(String username) {
        User user = getUserByUsername(username);
        return user.getBalance();
    }

    @Transactional
    public BigDecimal addBalance(String username, BigDecimal amount) {
        log.info("Adding balance: {} to user: {}", amount, username);
        User user = getUserByUsername(username);

        user.setBalance(user.getBalance().add(amount));

        userRepository.save(user);

        return user.getBalance();
    }
}