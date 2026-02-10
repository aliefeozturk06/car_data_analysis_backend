package com.infodif.car_data_analysis.service;

import com.infodif.car_data_analysis.dto.UpdateCarRequestDTO;
import com.infodif.car_data_analysis.entity.*;
import com.infodif.car_data_analysis.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarUpdateApprovalService {

    private final CarUpdateApprovalRepository approvalRepository;
    private final CarRepository carRepository;

    public List<UpdateCarRequestDTO> getAllPendingRequests() {
        // 🛡️ 1. Adımdaki repository metodunu kullanıyoruz
        return approvalRepository.findByStatus(ApprovalStatus.PENDING)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveUpdate(Long approvalId) {
        CarUpdateApproval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Talep bulunamadı!"));

        Car car = carRepository.findById(approval.getCarId())
                .orElseThrow(() -> new RuntimeException("Araç bulunamadı!"));

        // Güncellemeleri yansıt
        if (approval.getNewPrice() != null) car.setPrice(approval.getNewPrice());
        if (approval.getNewColor() != null) car.setColor(approval.getNewColor());
        if (approval.getNewMileage() != null) car.setMileage(approval.getNewMileage());

        carRepository.save(car);

        approval.setStatus(ApprovalStatus.APPROVED);
        approvalRepository.save(approval);
    }

    @Transactional
    public void rejectUpdate(Long approvalId) {
        CarUpdateApproval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Talep bulunamadı!"));

        approval.setStatus(ApprovalStatus.REJECTED);
        approvalRepository.save(approval);
    }

    private UpdateCarRequestDTO convertToDTO(CarUpdateApproval entity) {
        Car car = carRepository.findById(entity.getCarId()).orElse(null);
        return UpdateCarRequestDTO.builder()
                .id(entity.getId())
                .carId(entity.getCarId())
                .username(entity.getUsername())
                .newPrice(entity.getNewPrice())
                .newColor(entity.getNewColor())
                .newMileage(entity.getNewMileage())
                .manufacturer(car != null ? car.getManufacturer() : "Bilinmiyor")
                .model(car != null ? car.getModel() : "Bilinmiyor")
                .status(entity.getStatus().name())
                .requestDate(entity.getRequestDate())
                .build();
    }
}