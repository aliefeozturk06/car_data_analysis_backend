package com.infodif.car_data_analysis.dto;

import lombok.Builder;
import java.math.BigDecimal;


@Builder
public record AuthResponseDTO(
        String token,
        String username,
        String role,
        BigDecimal balance
) {}