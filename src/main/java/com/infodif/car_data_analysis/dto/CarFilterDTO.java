package com.infodif.car_data_analysis.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarFilterDTO {

    @Min(value = 0, message = "Page index cannot be negative!")
    private int page = 0;

    @Min(value = 1, message = "Size must be at least 1!")
    private int size = 15;

    private String sort = "id,asc";
    private String manufacturer;
    private String model;
    private String color;
    private Integer minYear;
    private Integer maxYear;
    private Double minPrice;
    private Double maxPrice;
}