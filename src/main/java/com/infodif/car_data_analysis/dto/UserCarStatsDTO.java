package com.infodif.car_data_analysis.dto;

public record UserCarStatsDTO(
        String username,
        long ownedCount,
        long onSaleCount,
        long waitingCount
) {}