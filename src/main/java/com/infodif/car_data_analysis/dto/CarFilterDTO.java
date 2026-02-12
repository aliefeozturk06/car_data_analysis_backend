package com.infodif.car_data_analysis.dto;

public record CarFilterDTO(
        String manufacturer,
        String model,
        Double minPrice,
        Double maxPrice,
        Integer minYear,
        Integer maxYear,
        String color,
        String status,
        Integer page,
        Integer size,
        String sort
) {}