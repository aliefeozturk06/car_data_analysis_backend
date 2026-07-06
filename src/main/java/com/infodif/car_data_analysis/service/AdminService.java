package com.infodif.car_data_analysis.service;

import com.infodif.car_data_analysis.dto.UserAdminResponseDTO;
import com.infodif.car_data_analysis.dto.UserCarStatsDTO;
import com.infodif.car_data_analysis.dto.UserRoleUpdateDTO;
import com.infodif.car_data_analysis.entity.User;
import com.infodif.car_data_analysis.exception.ResourceNotFoundException;
import com.infodif.car_data_analysis.repository.CarRepository;
import com.infodif.car_data_analysis.repository.CarUpdateApprovalRepository;
import com.infodif.car_data_analysis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Map<String, Long> ownedCounts = new HashMap<>();
        Map<String, Long> onSaleCounts = new HashMap<>();

        for (Object[] row : carRepository.countCarsGroupedByOwnerAndStatus()) {
            String username = (String) row[0];
            String status = (String) row[1];
            Long count = (Long) row[2];

            if ("OWNED".equals(status)) {
                ownedCounts.put(username, count);
            } else if ("ON_SALE".equals(status)) {
                onSaleCounts.put(username, count);
            }
        }

        Map<String, Long> waitingCounts = new HashMap<>();
        for (Object[] row : carUpdateApprovalRepository.countPendingGroupedByUsername()) {
            String username = (String) row[0];
            Long count = (Long) row[1];
            waitingCounts.put(username, count);
        }

        return users.stream()
                .map(user -> new UserCarStatsDTO(
                        user.getUsername(),
                        ownedCounts.getOrDefault(user.getUsername(), 0L),
                        onSaleCounts.getOrDefault(user.getUsername(), 0L),
                        waitingCounts.getOrDefault(user.getUsername(), 0L)
                ))
                .toList();
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