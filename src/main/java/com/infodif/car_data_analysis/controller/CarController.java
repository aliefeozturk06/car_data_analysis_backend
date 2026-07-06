package com.infodif.car_data_analysis.controller;

import com.infodif.car_data_analysis.dto.*;
import com.infodif.car_data_analysis.service.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
@Slf4j
public class CarController {

    private final CarService carService;

    @GetMapping
    public CarListResponseDTO getAllCars(
            @ModelAttribute CarFilterDTO filter,
            @RequestParam(required = false) String ownerUsername,
            Authentication auth) {
        String viewerUsername = (auth != null) ? auth.getName() : null;
        log.info("Fetching cars for viewer: {}", viewerUsername);
        return carService.getAllCars(filter, ownerUsername, viewerUsername);
    }

    @GetMapping("/my-cars")
    public CarListResponseDTO getMyCars(CarFilterDTO filterDto, Principal principal) {
        String currentUsername = principal.getName();
        log.info("🏠 My Cars requested by user: {}", currentUsername);
        return carService.getAllCars(filterDto, currentUsername, currentUsername);
    }

    @GetMapping("/{id}")
    public CarResponseDTO getById(@PathVariable Long id) {
        return carService.getCarById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarResponseDTO createCar(@RequestBody CarRequestDTO requestDto, Principal principal) {
        return carService.createCar(requestDto, principal.getName());
    }

    @PutMapping("/{id}")
    public CarResponseDTO updateCar(@PathVariable Long id, @RequestBody CarRequestDTO requestDto, Authentication auth) {
        return carService.updateCar(id, requestDto, auth);
    }

    @PatchMapping("/{id}")
    public CarResponseDTO patchCar(@PathVariable Long id, @RequestBody CarUpdateDTO updateDto, Authentication auth) {
        return carService.patchCar(id, updateDto, auth);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable Long id, Authentication auth) {
        carService.deleteCar(id, auth);
    }
}