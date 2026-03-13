package com.infodif.car_data_analysis.service;

import com.infodif.car_data_analysis.client.NominatimClient;
import com.infodif.car_data_analysis.client.TurkiyeApiClient;
import com.infodif.car_data_analysis.dto.UserDTO;
import com.infodif.car_data_analysis.entity.User;
import com.infodif.car_data_analysis.mapper.UserMapper;
import com.infodif.car_data_analysis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile; // 🔥 Yeni
import tools.jackson.databind.JsonNode;

import java.io.IOException; // 🔥 Yeni
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

    @Transactional
    public void updateProfilePicture(String username, MultipartFile file) {
        log.info("Uploading profile picture for user: {}", username);

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty! Please select a valid image.");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/"))) {
            throw new RuntimeException("Only image files are allowed!");
        }

        try {
            User user = getUserByUsername(username);
            user.setProfilePicture(file.getBytes());
            userRepository.save(user);
            log.info("Profile picture saved successfully for user: {}", username);
        } catch (IOException e) {
            log.error("Failed to store profile picture for user {}: {}", username, e.getMessage());
            throw new RuntimeException("Error occurred while processing the image file.");
        }
    }

    public byte[] getProfilePicture(String username) {
        User user = getUserByUsername(username);
        return user.getProfilePicture();
    }

    @Transactional
    public void deleteProfilePicture(String username) {
        User user = getUserByUsername(username);
        user.setProfilePicture(null);
        userRepository.save(user);
        log.info("Profile picture deleted for user: {}", username);
    }
}