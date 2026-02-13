package com.infodif.car_data_analysis.dto;

import com.infodif.car_data_analysis.entity.ApprovalStatus;

public record CarFilterDTO(
        String manufacturer,
        String model,
        String color,
        Double minPrice,
        Double maxPrice,
        Integer minYear,
        Integer maxYear,
        Integer minMileage,
        Integer maxMileage,
        String status,

        Integer page,
        Integer size,
        String sort
) {
    public CarFilterDTO {
        if (page == null) page = 0;
        if (size == null) size = 10;
        if (sort == null) sort = "id";
    }
}