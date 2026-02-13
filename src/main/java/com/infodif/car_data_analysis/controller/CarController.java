package com.infodif.car_data_analysis.controller;

import com.infodif.car_data_analysis.dto.*;
import com.infodif.car_data_analysis.service.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
@Slf4j
public class CarController {

    private final CarService carService;

    @GetMapping
    public CarListResponseDTO getAll(CarFilterDTO filterDto) {
        log.info("📢 All cars are listing. Filter criterias: {}", filterDto);
        return carService.getAllCars(filterDto);
    }

    @GetMapping("/{id}")
    public CarResponseDTO getById(@PathVariable Long id) {
        log.info("🔍 Car details are listing. ID: {}", id);
        return carService.getCarById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarResponseDTO createCar(@RequestBody CarRequestDTO requestDto, @RequestParam String username) {
        log.info("🚗 New car adding request! Owner: {}, Car: {} {}",
                username, requestDto.manufacturer(), requestDto.model());
        return carService.createCar(requestDto, username);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable Long id) {
        log.warn("🗑️ Car is deleting! Deleted Car's ID: {}", id);
        carService.deleteCar(id);
    }
}