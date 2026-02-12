package com.infodif.car_data_analysis.dto;

import java.util.List;

public record CarListResponseDTO(
        String message,
        long totalCount,
        int totalPages,
        List<CarResponseDTO> cars
) {}