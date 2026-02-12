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
@CrossOrigin(origins = "*")
@Slf4j
public class CarUpdateApprovalController {

    private final CarUpdateApprovalService approvalService;

    @GetMapping("/pending")
    public List<UpdateCarRequestDTO> getPendingUpdates() {
        log.info("Fetching all pending update requests.");
        return approvalService.getAllPendingRequests();
    }

    @PostMapping("/approve/{id}")
    public String approveUpdate(@PathVariable Long id) {
        approvalService.approveUpdate(id);
        log.info("Update request with ID {} approved successfully.", id);
        return "Car update request approved without a problem!";
    }

    @PostMapping("/reject/{id}")
    public String rejectUpdate(@PathVariable Long id) {
        approvalService.rejectUpdate(id);
        log.info("Update request with ID {} rejected successfully.", id);
        return "Car update rejected without a problem!";
    }
}