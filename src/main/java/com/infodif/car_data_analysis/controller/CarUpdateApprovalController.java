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
public class CarUpdateApprovalController {

    private final CarUpdateApprovalService approvalService;

    @GetMapping("/pending")
    public List<UpdateCarRequestDTO> getPendingUpdates() {
        return approvalService.getAllPendingRequests();
    }

    @GetMapping("/my-history/{username}")
    public List<UpdateCarRequestDTO> getMyHistory(@PathVariable String username) {
        return approvalService.getUserRequestHistory(username);
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