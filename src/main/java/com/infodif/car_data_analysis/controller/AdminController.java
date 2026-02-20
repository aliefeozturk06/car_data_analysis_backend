package com.infodif.car_data_analysis.controller;

import com.infodif.car_data_analysis.dto.UserAdminResponseDTO;
import com.infodif.car_data_analysis.dto.UserCarStatsDTO;
import com.infodif.car_data_analysis.dto.UserRoleUpdateDTO;
import com.infodif.car_data_analysis.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public List<UserAdminResponseDTO> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/car-stats")
    public ResponseEntity<List<UserCarStatsDTO>> getUserCarStats() {
        return ResponseEntity.ok(adminService.getUserCarStats());
    }

    @PatchMapping("/users/role")
    public ResponseEntity<String> updateUserRole(@RequestBody UserRoleUpdateDTO updateDto) {
        adminService.updateUserRole(updateDto);
        return ResponseEntity.ok("User role updated successfully to " + updateDto.newRole());
    }
}