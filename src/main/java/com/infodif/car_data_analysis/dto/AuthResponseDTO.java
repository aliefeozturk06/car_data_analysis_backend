package com.infodif.car_data_analysis.dto;

import java.math.BigDecimal;

public record AuthResponseDTO(
        String token,
        String username,
        String role,
        BigDecimal balance
) {}