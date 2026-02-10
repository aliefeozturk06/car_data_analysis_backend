package com.infodif.car_data_analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarResponseDTO {
    private Long id;
    private String manufacturer;
    private String model;
    private Integer year;
    private String color;
    private Double price;
    private Integer mileage;
    private String status;
    private boolean isUsed;
    private boolean hasPendingUpdate;
}