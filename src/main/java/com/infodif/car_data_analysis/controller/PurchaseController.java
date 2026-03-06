package com.infodif.car_data_analysis.controller;

import com.infodif.car_data_analysis.dto.CarFilterDTO;
import com.infodif.car_data_analysis.dto.CarResponseDTO;
import com.infodif.car_data_analysis.dto.TransactionDTO;
import com.infodif.car_data_analysis.dto.UpdateCarRequestDTO;
import com.infodif.car_data_analysis.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase")
@RequiredArgsConstructor
@Slf4j
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping("/buy")
    public String buyCar(@RequestParam String username, @RequestParam Long carId) {
        log.info("User {} is attempting to purchase car with ID: {}", username, carId);
        return purchaseService.buyCar(username, carId);
    }

    @PostMapping("/create-update-request")
    public String createUpdateRequest(@RequestBody UpdateCarRequestDTO request, Authentication auth) {
        String currentUsername = auth.getName();
        log.info("Update request received for car ID: {} from user: {}", request.carId(), currentUsername);

        UpdateCarRequestDTO securedDto = new UpdateCarRequestDTO(
                request.id(),
                request.carId(),
                currentUsername,
                request.newPrice(),
                request.newColor(),
                request.newMileage(),
                request.oldPrice(),
                request.oldColor(),
                request.oldMileage(),
                request.manufacturer(),
                request.model(),
                request.status(),
                request.requestDate()
        );

        return purchaseService.createUpdateRequest(securedDto);
    }

    @PostMapping("/cancel-update-request")
    public String cancelUpdateRequest(@RequestParam String username, @RequestParam Long carId) {
        log.info("Cancelling update request for car ID: {} by user: {}", carId, username);
        return purchaseService.cancelUpdateRequest(username, carId);
    }

    @GetMapping("/my-cars")
    public List<CarResponseDTO> getMyCars(
            @RequestParam String username,
            @RequestParam String status,
            @ModelAttribute CarFilterDTO filter) {
        log.info("User {} is viewing their own cars. Distance will be calculated based on this user.", username);
        return purchaseService.getMyCars(username, status, filter);
    }

    @GetMapping("/sold-history")
    public List<TransactionDTO> getSoldHistory(@RequestParam String username) {
        log.info("Fetching sales history for user: {}", username);
        return purchaseService.getSoldHistory(username);
    }

    @GetMapping("/my-pending-requests")
    public List<UpdateCarRequestDTO> getMyPendingRequests(@RequestParam String username) {
        log.info("Fetching pending update requests for user: {}", username);
        return purchaseService.getMyPendingRequests(username);
    }

    @GetMapping("/my-update-history")
    public List<UpdateCarRequestDTO> getMyUpdateHistory(@RequestParam String username) {
        log.info("Fetching full update history for user: {}", username);
        return purchaseService.getMyUpdateHistory(username);
    }

    @PutMapping("/list-for-sale")
    public String listForSale(@RequestParam String username, @RequestParam Long carId) {
        log.info("User {} is listing car ID: {} for sale", username, carId);
        return purchaseService.listForSale(username, carId);
    }

    @PutMapping("/cancel-sale")
    public String cancelSale(@RequestParam String username, @RequestParam Long carId) {
        log.info("User {} is cancelling sale for car ID: {}", username, carId);
        return purchaseService.cancelSale(username, carId);
    }

    @GetMapping("/moderator/all-pending-requests")
    public List<UpdateCarRequestDTO> getAllPendingRequests() {
        log.info("Moderator is fetching all pending approval requests.");
        return purchaseService.getAllPendingApprovals();
    }

    @PostMapping("/moderator/approve/{id}")
    public String approveRequest(@PathVariable Long id) {
        log.info("Moderator is approving request ID: {}", id);
        return purchaseService.approveCarUpdate(id);
    }

    @PostMapping("/moderator/reject/{id}")
    public String rejectRequest(@PathVariable Long id) {
        log.warn("Moderator is rejecting request ID: {}", id);
        return purchaseService.rejectCarUpdate(id);
    }
}