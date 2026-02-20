package com.infodif.car_data_analysis.dto;

import com.infodif.car_data_analysis.entity.Role;

public record UserRoleUpdateDTO(
        Long userId,
        Role newRole
) {}