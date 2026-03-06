    package com.infodif.car_data_analysis.service;

    import com.infodif.car_data_analysis.dto.UpdateCarRequestDTO;
    import com.infodif.car_data_analysis.entity.*;
    import com.infodif.car_data_analysis.mapper.CarUpdateApprovalMapper;
    import com.infodif.car_data_analysis.repository.*;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.List;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class CarUpdateApprovalService {

        private final CarUpdateApprovalRepository approvalRepository;
        private final CarRepository carRepository;
        private final CarUpdateApprovalMapper approvalMapper;

        public List<UpdateCarRequestDTO> getAllPendingRequests() {
            return approvalRepository.findByStatus(ApprovalStatus.PENDING)
                    .stream()
                    .map(approval -> {
                        Car car = carRepository.findById(approval.getCarId()).orElse(null);
                        return approvalMapper.toDto(approval, car);
                    })
                    .toList();
        }

        public List<UpdateCarRequestDTO> getUserRequestHistory(String username) {
            return approvalRepository.findByUsernameOrderByRequestDateDesc(username)
                    .stream()
                    .map(approval -> {
                        Car car = carRepository.findById(approval.getCarId()).orElse(null);
                        return approvalMapper.toDto(approval, car);
                    })
                    .toList();
        }

        @Transactional
        public void approveUpdate(Long approvalId) {
            CarUpdateApproval approval = approvalRepository.findById(approvalId)
                    .orElseThrow(() -> new RuntimeException("Request not found!"));

            Car car = carRepository.findById(approval.getCarId())
                    .orElseThrow(() -> new RuntimeException("Car not found!"));

            if (approval.getNewPrice() != null) car.setPrice(approval.getNewPrice());
            if (approval.getNewColor() != null) car.setColor(approval.getNewColor());
            if (approval.getNewMileage() != null) car.setMileage(approval.getNewMileage());

            carRepository.save(car);
            approval.setStatus(ApprovalStatus.APPROVED);
            approvalRepository.save(approval);
        }

        @Transactional
        public void rejectUpdate(Long approvalId) {
            CarUpdateApproval approval = approvalRepository.findById(approvalId).orElseThrow();
            approval.setStatus(ApprovalStatus.REJECTED);
            approvalRepository.save(approval);
        }
    }
