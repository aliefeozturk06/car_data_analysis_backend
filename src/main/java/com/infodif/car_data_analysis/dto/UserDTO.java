package com.infodif.car_data_analysis.dto;

import lombok.Builder;


@Builder
public record UserDTO(
        Long id,
        String username,
        String role
) {}