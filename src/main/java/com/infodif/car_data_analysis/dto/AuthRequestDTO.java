package com.infodif.car_data_analysis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequestDTO {

    @NotBlank(message = "Username cannot be blank!")
    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters!")
    private String username;

    @NotBlank(message = "Password cannot be blank!")
    @Size(min = 1, message = "Password must be at least 1 characters long!")
    private String password;
}