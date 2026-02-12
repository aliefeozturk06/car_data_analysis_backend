package com.infodif.car_data_analysis.mapper;

import com.infodif.car_data_analysis.dto.UpdateCarRequestDTO;
import com.infodif.car_data_analysis.entity.Car;
import com.infodif.car_data_analysis.entity.CarUpdateApproval;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarUpdateApprovalMapper {

    @Mapping(target = "id", source = "approval.id")
    @Mapping(target = "carId", source = "approval.carId")
    @Mapping(target = "username", source = "approval.username")
    @Mapping(target = "status", expression = "java(approval.getStatus().name())")
    @Mapping(target = "requestDate", source = "approval.requestDate")
    @Mapping(target = "newPrice", source = "approval.newPrice")
    @Mapping(target = "newColor", source = "approval.newColor")
    @Mapping(target = "newMileage", source = "approval.newMileage")
    @Mapping(target = "oldPrice", source = "car.price")
    @Mapping(target = "oldColor", source = "car.color")
    @Mapping(target = "oldMileage", source = "car.mileage")
    @Mapping(target = "manufacturer", source = "car.manufacturer")
    @Mapping(target = "model", source = "car.model")
    UpdateCarRequestDTO toDto(CarUpdateApproval approval, Car car);
}