package com.infodif.car_data_analysis.controller;

import com.infodif.car_data_analysis.dto.*;
import com.infodif.car_data_analysis.service.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
@Slf4j
public class CarController {

    private final CarService carService;

    @GetMapping
    public CarListResponseDTO getAll(CarFilterDTO filterDto) {
        log.info("📢 Market listing requested. Filters: {}", filterDto);
        return carService.getAllCars(filterDto, null);
    }

    @GetMapping("/my-cars")
    public CarListResponseDTO getMyCars(CarFilterDTO filterDto, Principal principal) {
        log.info("🏠 My Cars requested by user: {}", principal.getName());
        return carService.getAllCars(filterDto, principal.getName());
    }

    @GetMapping("/{id}")
    public CarResponseDTO getById(@PathVariable Long id) {
        log.info("🔍 Fetching car details. ID: {}", id);
        return carService.getCarById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarResponseDTO createCar(@RequestBody CarRequestDTO requestDto, Principal principal) {
        log.info("🚗 New car registration. Owner: {}, Car: {} {}",
                principal.getName(), requestDto.manufacturer(), requestDto.model());
        return carService.createCar(requestDto, principal.getName());
    }

    @PutMapping("/{id}")
    public CarResponseDTO updateCar(@PathVariable Long id, @RequestBody CarRequestDTO requestDto) {
        log.info("🔄 Updating car ID: {}", id);
        return carService.updateCar(id, requestDto);
    }

    @PatchMapping("/{id}")
    public CarResponseDTO patchCar(@PathVariable Long id, @RequestBody CarUpdateDTO updateDto) {
        log.info("🩹 Patching car ID: {}", id);
        return carService.patchCar(id, updateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable Long id) {
        log.warn("🗑️ Deleting car ID: {}", id);
        carService.deleteCar(id);
    }
}