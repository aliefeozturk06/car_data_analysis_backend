package com.infodif.car_data_analysis.dto;

import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record CarUpdateDTO(

        String color,

        @Min(value = 0, message = "Mileage cannot be negative")
        Integer mileage,

        @Min(value = 0, message = "Price cannot be negative")
        Double price

) {}