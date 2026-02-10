package com.infodif.car_data_analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponseDTO {
    private String token;
    private String username;
    private String role;
    private BigDecimal balance;
}