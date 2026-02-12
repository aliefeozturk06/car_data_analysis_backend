package com.infodif.car_data_analysis.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CarRequestDTO(
        @NotBlank(message = "Manufacturer name is mandatory.")
        String manufacturer,

        @NotBlank(message = "Model name is mandatory.")
        String model,

        @NotNull(message = "Year is mandatory")
        @Min(value = 1950, message = "Production year cannot be earlier than 1950.")
        @Max(value = 2026, message = "Production year cannot be later than 2026.")
        Integer year,

        @NotBlank(message = "Color is mandatory.")
        String color,

        @NotNull(message = "Price is mandatory.")
        @Min(value = 0, message = "Price cannot be a negative value.")
        Double price,

        @NotNull(message = "Mileage is mandatory.")
        @Min(value = 0, message = "Mileage cannot be a negative value.")
        Integer mileage
) {}