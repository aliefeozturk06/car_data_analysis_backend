package com.infodif.car_data_analysis.controller;

import com.infodif.car_data_analysis.dto.UserDTO;
import com.infodif.car_data_analysis.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserDtoByUsername(username));
    }

    @GetMapping("/{username}/balance")
    public BigDecimal getBalance(@PathVariable String username) {
        return userService.getBalance(username);
    }

    @PutMapping("/add-balance")
    public BigDecimal addBalance(@RequestParam String username, @RequestParam BigDecimal amount) {
        return userService.addBalance(username, amount);
    }
}