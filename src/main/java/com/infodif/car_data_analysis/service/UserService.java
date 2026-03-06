package com.infodif.car_data_analysis.service;

import com.infodif.car_data_analysis.client.NominatimClient;
import com.infodif.car_data_analysis.client.TurkiyeApiClient; // 🔥 Yeni eklendi
import com.infodif.car_data_analysis.dto.UserDTO;
import com.infodif.car_data_analysis.entity.User;
import com.infodif.car_data_analysis.mapper.UserMapper;
import com.infodif.car_data_analysis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final NominatimClient nominatimClient;
    private final TurkiyeApiClient turkiyeApiClient;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public UserDTO getUserDtoByUsername(String username) {
        User user = getUserByUsername(username);
        return userMapper.toDto(user);
    }

    public BigDecimal getBalance(String username) {
        User user = getUserByUsername(username);
        return user.getBalance();
    }

    public JsonNode getAllTurkishProvinces() {
        log.info("Fetching all Turkish provinces via HttpExchange");
        return turkiyeApiClient.getAllProvinces();
    }

    @Transactional
    public BigDecimal addBalance(String username, BigDecimal amount) {
        log.info("Adding balance: {} to user: {}", amount, username);
        User user = getUserByUsername(username);
        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);
        return user.getBalance();
    }

    @Transactional
    public void updateUsername(String currentUsername, String newUsername) {
        if (userRepository.findByUsername(newUsername).isPresent()) {
            throw new RuntimeException("This username is already taken!");
        }

        User user = getUserByUsername(currentUsername);
        user.setUsername(newUsername);
        userRepository.save(user);
    }

    @Transactional
    public void updateLocation(String username, String newLocation) {
        log.info("Updating location for user: {} to {}", username, newLocation);
        User user = getUserByUsername(username);
        user.setLocation(newLocation);

        try {
            JsonNode response = nominatimClient.search(newLocation, "json", 1);

            if (response != null && response.isArray() && !response.isEmpty()) {
                JsonNode firstResult = response.get(0);

                double lat = Double.parseDouble(firstResult.get("lat").asText());
                double lon = Double.parseDouble(firstResult.get("lon").asText());

                user.setLatitude(lat);
                user.setLongitude(lon);

                log.info("Coordinates found: Lat {}, Lng {}", lat, lon);
            } else {
                log.warn("No coordinates found for location: {}", newLocation);
            }
        } catch (Exception e) {
            log.error("Geocoding failed for {}: {}", newLocation, e.getMessage());
        }
        userRepository.save(user);
    }
}