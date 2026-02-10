package com.infodif.car_data_analysis.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarUpdateDTO {

    private String color;

    @Min(value = 0, message = "Mileage cannot be negative")
    private Integer mileage;

    @Min(value = 0, message = "Price cannot be negative")
    private Double price;
}