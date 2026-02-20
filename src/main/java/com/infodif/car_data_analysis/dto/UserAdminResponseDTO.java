package com.infodif.car_data_analysis.dto;

import com.infodif.car_data_analysis.entity.Role;
import java.math.BigDecimal;

public record UserAdminResponseDTO(
        Long id,
        String username,
        BigDecimal balance,
        Role role
) {}