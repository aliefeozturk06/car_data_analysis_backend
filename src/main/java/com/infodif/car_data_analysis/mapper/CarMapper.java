package com.infodif.car_data_analysis.mapper;

import com.infodif.car_data_analysis.dto.CarRequestDTO;
import com.infodif.car_data_analysis.dto.CarResponseDTO;
import com.infodif.car_data_analysis.dto.CarUpdateDTO;
import com.infodif.car_data_analysis.entity.Car;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CarMapper {

    @Mapping(target = "isUsed", expression = "java(car.getMileage() != null && car.getMileage() > 0)")
    @Mapping(source = "owner.location", target = "sellerLocation", defaultValue = "Unknown Location")
    CarResponseDTO toResponseDto(Car car);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "status", ignore = true)
    Car toEntity(CarRequestDTO dto);

    void updateEntityFromDto(CarRequestDTO dto, @MappingTarget Car car);

    void patchEntityFromDto(CarUpdateDTO dto, @MappingTarget Car car);
}