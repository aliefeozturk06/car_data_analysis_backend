package com.infodif.car_data_analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarListResponseDTO {

    private String message;
    private long totalCount;
    private int totalPages;
    private List<CarResponseDTO> cars;
}