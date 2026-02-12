package com.infodif.car_data_analysis.dto;

import jakarta.validation.constraints.Min;
import lombok.Builder;


@Builder
public record CarFilterDTO(
        @Min(value = 0, message = "Page index cannot be negative!")
        int page,

        @Min(value = 1, message = "Size must be at least 1!")
        int size,

        String sort,
        String manufacturer,
        String model,
        String color,
        Integer minYear,
        Integer maxYear,
        Double minPrice,
        Double maxPrice
) {

    public CarFilterDTO {
        if (sort == null) sort = "id,asc";
        if (size == 0) size = 10;
    }
}