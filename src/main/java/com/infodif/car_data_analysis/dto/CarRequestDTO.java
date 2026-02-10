package com.infodif.car_data_analysis.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarRequestDTO {

    @NotBlank(message = "Manufacturer name is mandatory")
    private String manufacturer;

    @NotBlank(message = "Model name is mandatory")
    private String model;

    @NotNull(message = "Year is mandatory")
    @Min(value = 1950, message = "Production year cannot be earlier than 1950")
    @Max(value = 2026, message = "Production year cannot be later than 2026")
    private Integer year;

    @NotBlank(message = "Color is mandatory")
    private String color;

    @NotNull(message = "Price is mandatory")
    @Min(value = 0, message = "Price cannot be a negative value")
    private Double price;

    @NotNull(message = "Mileage is mandatory")
    @Min(value = 0, message = "Mileage cannot be a negative value")
    private Integer mileage;
}