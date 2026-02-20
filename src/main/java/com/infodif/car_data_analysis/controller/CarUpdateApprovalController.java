package com.infodif.car_data_analysis.controller;

import com.infodif.car_data_analysis.dto.UpdateCarRequestDTO;
import com.infodif.car_data_analysis.service.CarUpdateApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
@Slf4j
public class CarUpdateApprovalController {

    private final CarUpdateApprovalService approvalService;

    @GetMapping("/pending")
    public List<UpdateCarRequestDTO> getPendingUpdates() {
        log.info("🛡Moderator gets all the waiting update requests.");
        return approvalService.getAllPendingRequests();
    }

    @PostMapping("/approve/{id}")
    public String approveUpdate(@PathVariable Long id) {
        log.info("Approval request has been sent. Request ID: {}", id);
        approvalService.approveUpdate(id);
        return "Car update approval request has ben approved!";
    }

    @PostMapping("/reject/{id}")
    public String rejectUpdate(@PathVariable Long id) {
        log.warn("Rejection request has been sent. Request ID: {}", id);
        approvalService.rejectUpdate(id);
        return "Car update approval request has ben rejected.";
    }
}