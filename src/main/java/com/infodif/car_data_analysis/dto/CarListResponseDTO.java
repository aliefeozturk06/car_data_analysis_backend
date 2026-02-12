package com.infodif.car_data_analysis.dto;

import lombok.Builder;
import java.util.List;


@Builder
public record CarListResponseDTO(
        String message,
        long totalCount,
        int totalPages,
        List<CarResponseDTO> cars
) {}