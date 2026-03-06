package com.infodif.car_data_analysis.controller;

import com.infodif.car_data_analysis.dto.UserDTO;
import com.infodif.car_data_analysis.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public UserDTO getUserProfile(@PathVariable String username) {
        log.info("Fetching profile data for user: {}", username);
        return userService.getUserDtoByUsername(username);
    }

    @GetMapping("/{username}/balance")
    public BigDecimal getBalance(@PathVariable String username) {
        log.info("Fetching account balance for user: {}", username);
        return userService.getBalance(username);
    }

    @PutMapping("/add-balance")
    public BigDecimal addBalance(@RequestParam String username, @RequestParam BigDecimal amount) {
        log.info("Processing balance deposit of {} for user: {}", amount, username);
        return userService.addBalance(username, amount);
    }

    @PutMapping("/update-username")
    public String updateUsername(@RequestParam String currentUsername, @RequestParam String newUsername) {
        userService.updateUsername(currentUsername, newUsername);
        return "Username updated successfully.";
    }

    @PutMapping("/update-location")
    public String updateLocation(@RequestParam String username, @RequestParam String newLocation) {
        log.info("Updating location for user: {} to {}", username, newLocation);
        userService.updateLocation(username, newLocation);
        return "Location updated successfully.";
    }
}