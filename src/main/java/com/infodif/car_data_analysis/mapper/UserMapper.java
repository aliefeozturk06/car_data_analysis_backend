package com.infodif.car_data_analysis.mapper;

import com.infodif.car_data_analysis.dto.AuthResponseDTO;
import com.infodif.car_data_analysis.dto.UserDTO;
import com.infodif.car_data_analysis.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDto(User user);

    @Mapping(target = "token", source = "token")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    @Mapping(target = "balance", source = "user.balance")
    AuthResponseDTO toAuthResponseDto(User user, String token);
}