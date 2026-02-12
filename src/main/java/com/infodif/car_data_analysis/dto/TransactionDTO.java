package com.infodif.car_data_analysis.dto;

import java.time.LocalDateTime;

public record TransactionDTO(
        Long id,
        Long carId,
        String sellerName,
        String buyerName,
        String manufacturer,
        String model,
        String color,
        Integer year,
        Double price,
        LocalDateTime saleDate
) {}