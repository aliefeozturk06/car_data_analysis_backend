package com.infodif.car_data_analysis.dto;

import java.time.LocalDateTime;

public record UpdateCarRequestDTO(
        Long id,
        Long carId,
        String username,
        Double newPrice,
        String newColor,
        Integer newMileage,
        Double oldPrice,
        String oldColor,
        Integer oldMileage,
        String manufacturer,
        String model,
        String status,
        LocalDateTime requestDate
) {}