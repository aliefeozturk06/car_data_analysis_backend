package com.infodif.car_data_analysis.controller;

import com.infodif.car_data_analysis.dto.CarFilterDTO;
import com.infodif.car_data_analysis.dto.CarResponseDTO;
import com.infodif.car_data_analysis.dto.UpdateCarRequestDTO;
import com.infodif.car_data_analysis.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping("/buy")
    public String buyCar(@RequestParam String username, @RequestParam Long carId) {
        log.info("User {} is attempting to buy car ID {}", username, carId);
        return purchaseService.buyCar(username, carId);
    }

    @PostMapping("/create-update-request")
    public String createUpdateRequest(@RequestBody UpdateCarRequestDTO request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        request.username();

        log.info("📢 Update request received from: {}", currentUsername);
        log.info("📦 Data: {}", request);

        return purchaseService.createUpdateRequest(request);
    }

    @PostMapping("/cancel-update-request")
    public String cancelUpdateRequest(@RequestParam String username, @RequestParam Long carId) {
        log.info("Cancelling update request for car {} by user {}", carId, username);
        return purchaseService.cancelUpdateRequest(username, carId);
    }

    @GetMapping("/my-cars")
    public List<CarResponseDTO> getMyCars(
            @RequestParam String username,
            @RequestParam String status,
            @ModelAttribute CarFilterDTO filter) {
        log.info("Fetching cars for user {} with status {}", username, status);
        return purchaseService.getMyCars(username, status, filter);
    }

    @GetMapping("/sold-history")
    public List<CarResponseDTO> getSoldHistory(@RequestParam String username) {
        log.info("Fetching sold history for user {}", username);
        return purchaseService.getSoldHistory(username);
    }

    @GetMapping("/my-pending-requests")
    public List<UpdateCarRequestDTO> getMyPendingRequests(@RequestParam String username) {
        log.info("Fetching pending update requests for user {}", username);
        return purchaseService.getMyPendingRequests(username);
    }

    @PutMapping("/list-for-sale")
    public String listForSale(@RequestParam String username, @RequestParam Long carId) {
        log.info("User {} listing car {} for sale", username, carId);
        return purchaseService.listForSale(username, carId);
    }

    @PutMapping("/cancel-sale")
    public String cancelSale(@RequestParam String username, @RequestParam Long carId) {
        log.info("User {} cancelling sale for car {}", username, carId);
        return purchaseService.cancelSale(username, carId);
    }

    @GetMapping("/moderator/all-pending-requests")
    public List<UpdateCarRequestDTO> getAllPendingRequests() {
        log.info("Moderator fetching all pending approvals.");
        return purchaseService.getAllPendingApprovals();
    }

    @PostMapping("/moderator/approve/{id}")
    public String approveRequest(@PathVariable Long id) {
        log.info("Moderator approving request ID: {}", id);
        return purchaseService.approveCarUpdate(id);
    }

    @PostMapping("/moderator/reject/{id}")
    public String rejectRequest(@PathVariable Long id) {
        log.info("Moderator rejecting request ID: {}", id);
        return purchaseService.rejectCarUpdate(id);
    }
}