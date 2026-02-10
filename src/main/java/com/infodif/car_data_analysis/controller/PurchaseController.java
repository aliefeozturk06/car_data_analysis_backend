package com.infodif.car_data_analysis.controller;

import com.infodif.car_data_analysis.dto.CarFilterDTO;
import com.infodif.car_data_analysis.dto.CarResponseDTO;
import com.infodif.car_data_analysis.dto.UpdateCarRequestDTO;
import com.infodif.car_data_analysis.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PurchaseController {

    private final PurchaseService purchaseService;


    @PostMapping("/buy")
    public String buyCar(@RequestParam String username, @RequestParam Long carId) {
        return purchaseService.buyCar(username, carId);
    }

    @PostMapping("/create-update-request")
    public ResponseEntity<String> createUpdateRequest(@RequestBody UpdateCarRequestDTO request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String currentUsername = auth.getName();

        request.setUsername(currentUsername);

        System.out.println("📢 Update request has come: " + currentUsername);
        System.out.println("📦 Data: " + request.toString());

        return ResponseEntity.ok(purchaseService.createUpdateRequest(request));    }

    @PostMapping("/cancel-update-request")
    public ResponseEntity<String> cancelUpdateRequest(@RequestParam String username, @RequestParam Long carId) {
        return ResponseEntity.ok(purchaseService.cancelUpdateRequest(username, carId));
    }

    @GetMapping("/my-cars")
    public List<CarResponseDTO> getMyCars(
            @RequestParam String username,
            @RequestParam String status,
            @ModelAttribute CarFilterDTO filter) {
        return purchaseService.getMyCars(username, status, filter);
    }

    @GetMapping("/sold-history")
    public List<CarResponseDTO> getSoldHistory(@RequestParam String username) {
        return purchaseService.getSoldHistory(username);
    }

    @GetMapping("/my-pending-requests")
    public List<UpdateCarRequestDTO> getMyPendingRequests(@RequestParam String username) {
        return purchaseService.getMyPendingRequests(username);
    }

    @PutMapping("/list-for-sale")
    public String listForSale(@RequestParam String username, @RequestParam Long carId) {
        return purchaseService.listForSale(username, carId);
    }

    @PutMapping("/cancel-sale")
    public String cancelSale(@RequestParam String username, @RequestParam Long carId) {
        return purchaseService.cancelSale(username, carId);
    }

    @GetMapping("/moderator/all-pending-requests")
    public ResponseEntity<List<UpdateCarRequestDTO>> getAllPendingRequests() {
        return ResponseEntity.ok(purchaseService.getAllPendingApprovals());
    }

    @PostMapping("/moderator/approve/{id}")
    public ResponseEntity<String> approveRequest(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.approveCarUpdate(id));
    }

    @PostMapping("/moderator/reject/{id}")
    public ResponseEntity<String> rejectRequest(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.rejectCarUpdate(id));
    }
}