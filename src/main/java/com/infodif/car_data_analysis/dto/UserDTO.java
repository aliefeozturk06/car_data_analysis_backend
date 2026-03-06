package com.infodif.car_data_analysis.dto;

public record UserDTO(
        Long id,
        String username,
        String role,
        String location
) {}