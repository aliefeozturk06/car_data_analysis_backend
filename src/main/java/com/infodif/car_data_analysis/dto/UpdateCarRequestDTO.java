package com.infodif.car_data_analysis.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCarRequestDTO {

    private Long id;
    private Long carId;
    private String username;

    private Double newPrice;
    private String newColor;
    private Integer newMileage;

    private Double oldPrice;
    private String oldColor;
    private Integer oldMileage;

    private String manufacturer;
    private String model;

    private String status;
    private LocalDateTime requestDate;
}