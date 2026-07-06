package com.infodif.car_data_analysis.controller;

import com.infodif.car_data_analysis.dto.UpdateCarRequestDTO;
import com.infodif.car_data_analysis.service.CarUpdateApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
public class CarUpdateApprovalController {

    private final CarUpdateApprovalService approvalService;

    @GetMapping("/pending")
    public List<UpdateCarRequestDTO> getPendingUpdates() {
        return approvalService.getAllPendingRequests();
    }

    @GetMapping("/my-history")
    public List<UpdateCarRequestDTO> getMyHistory(Authentication auth) {
        return approvalService.getUserRequestHistory(auth.getName());
    }

    @PostMapping("/approve/{id}")
    public String approveUpdate(@PathVariable Long id) {
        approvalService.approveUpdate(id);
        return "Update approved and applied to car!";
    }

    @PostMapping("/reject/{id}")
    public String rejectUpdate(@PathVariable Long id) {
        approvalService.rejectUpdate(id);
        return "Update request has been rejected.";
    }
}