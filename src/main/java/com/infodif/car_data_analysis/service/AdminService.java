package com.infodif.car_data_analysis.service;

import com.infodif.car_data_analysis.dto.UserAdminResponseDTO;
import com.infodif.car_data_analysis.dto.UserCarStatsDTO;
import com.infodif.car_data_analysis.dto.UserRoleUpdateDTO;
import com.infodif.car_data_analysis.entity.ApprovalStatus;
import com.infodif.car_data_analysis.entity.CarUpdateApproval;
import com.infodif.car_data_analysis.entity.User;
import com.infodif.car_data_analysis.exception.ResourceNotFoundException;
import com.infodif.car_data_analysis.repository.CarRepository;
import com.infodif.car_data_analysis.repository.CarUpdateApprovalRepository;
import com.infodif.car_data_analysis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final CarUpdateApprovalRepository carUpdateApprovalRepository;

    @Transactional(readOnly = true)
    public List<UserAdminResponseDTO> getAllUsers() {
        log.info("Admin is fetching all users list.");
        return userRepository.findAll().stream()
                .map(user -> new UserAdminResponseDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getBalance(),
                        user.getRole()
                ))
                .toList();
    }

    public List<UserCarStatsDTO> getUserCarStats() {
        List<User> users = userRepository.findAll();
        List<UserCarStatsDTO> statsList = new ArrayList<>();

        for (User user : users) {
            long owned = carRepository.countByOwnerAndStatus(user, "OWNED");
            long onSale = carRepository.countByOwnerAndStatus(user, "ON_SALE");

            long waiting = carUpdateApprovalRepository.countByUsernameAndStatus(user.getUsername(), ApprovalStatus.PENDING);

            statsList.add(new UserCarStatsDTO(user.getUsername(), owned, onSale, waiting));
        }
        return statsList;
    }

    @Transactional
    public void updateUserRole(UserRoleUpdateDTO updateDto) {
        User user = userRepository.findById(updateDto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + updateDto.userId()));

        log.warn("Admin is changing role of user {} from {} to {}",
                user.getUsername(), user.getRole(), updateDto.newRole());

        user.setRole(updateDto.newRole());
        userRepository.save(user);
    }
}