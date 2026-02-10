package com.infodif.car_data_analysis.controller;

import com.infodif.car_data_analysis.dto.*;
import com.infodif.car_data_analysis.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CarController {

    private final CarService carService;

    @GetMapping
    public CarListResponseDTO getAll(CarFilterDTO filterDto) {
        return carService.getAllCars(filterDto);
    }

    @GetMapping("/{id}")
    public CarResponseDTO getById(@PathVariable Long id) {
        return carService.getCarById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarResponseDTO createCar(@RequestBody CarRequestDTO requestDto, @RequestParam String username) {
        return carService.createCar(requestDto, username);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}