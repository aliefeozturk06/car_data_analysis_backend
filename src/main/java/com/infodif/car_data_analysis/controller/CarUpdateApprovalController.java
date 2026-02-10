package com.infodif.car_data_analysis.controller;

import com.infodif.car_data_analysis.dto.UpdateCarRequestDTO;
import com.infodif.car_data_analysis.service.CarUpdateApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CarUpdateApprovalController {

    private final CarUpdateApprovalService approvalService;

    @GetMapping("/pending")
    public ResponseEntity<List<UpdateCarRequestDTO>> getPendingUpdates() {
        return ResponseEntity.ok(approvalService.getAllPendingRequests());
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<String> approveUpdate(@PathVariable Long id) {
        approvalService.approveUpdate(id);
        return ResponseEntity.ok("Car update request approved without a problem!");
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<String> rejectUpdate(@PathVariable Long id) {
        approvalService.rejectUpdate(id);
        return ResponseEntity.ok("Car update rejected without a problem!");
    }
}