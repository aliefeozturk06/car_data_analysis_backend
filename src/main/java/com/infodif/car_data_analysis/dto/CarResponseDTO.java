package com.infodif.car_data_analysis.dto;

public record CarResponseDTO(
        Long id,
        String manufacturer,
        String model,
        Integer year,
        String color,
        Double price,
        Integer mileage,
        String status,
        boolean isUsed,
        boolean hasPendingUpdate
) {}