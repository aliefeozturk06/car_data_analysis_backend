package com.infodif.car_data_analysis.mapper;

import com.infodif.car_data_analysis.dto.TransactionDTO;
import com.infodif.car_data_analysis.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionDTO toDto(Transaction transaction);
}