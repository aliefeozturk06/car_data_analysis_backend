package com.infodif.car_data_analysis.service;

import com.infodif.car_data_analysis.dto.*;
import com.infodif.car_data_analysis.entity.Car;
import com.infodif.car_data_analysis.entity.Role;
import com.infodif.car_data_analysis.entity.User;
import com.infodif.car_data_analysis.exception.ResourceNotFoundException;
import com.infodif.car_data_analysis.mapper.CarMapper;
import com.infodif.car_data_analysis.repository.CarRepository;
import com.infodif.car_data_analysis.repository.UserRepository;
import com.infodif.car_data_analysis.specification.CarSpecifications;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final CarMapper carMapper;

    public CarListResponseDTO getAllCars(CarFilterDTO filter, String currentUsername) {
        List<Sort.Order> orders = new ArrayList<>();
        if (filter.sort() != null && !filter.sort().isEmpty()) {
            String[] sortParts = filter.sort().split(",");
            for (int i = 0; i < sortParts.length; i += 2) {
                String property = sortParts[i];
                String direction = (i + 1 < sortParts.length) ? sortParts[i + 1] : "asc";
                orders.add(direction.equalsIgnoreCase("desc") ? Sort.Order.desc(property) : Sort.Order.asc(property));
            }
        }
        if (orders.isEmpty()) orders.add(Sort.Order.asc("id"));
        Pageable pageable = PageRequest.of(filter.page(), filter.size(), Sort.by(orders));

        Specification<Car> spec = (root, query, cb) -> cb.conjunction();

        if (currentUsername != null && !currentUsername.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("owner", JoinType.LEFT).get("username"), currentUsername)
            );
        } else {
            if (filter.status() != null && !filter.status().isBlank() && !"ALL".equalsIgnoreCase(filter.status())) {
                spec = spec.and(CarSpecifications.hasStatus(filter.status()));
            } else {
                spec = spec.and((root, query, cb) -> cb.or(
                        cb.isNull(root.get("owner")),
                        cb.equal(root.get("status").as(String.class), "ON_SALE")
                ));
            }
        }

        spec = spec.and(CarSpecifications.hasManufacturer(filter.manufacturer()))
                .and(CarSpecifications.hasModel(filter.model()))
                .and(CarSpecifications.hasColor(filter.color()))
                .and(CarSpecifications.hasYearBetween(filter.minYear(), filter.maxYear()))
                .and(CarSpecifications.hasPriceBetween(filter.minPrice(), filter.maxPrice()))
                .and(CarSpecifications.hasMileageBetween(filter.minMileage(), filter.maxMileage()));

        Page<Car> carPage = carRepository.findAll(spec, pageable);

        List<CarResponseDTO> dtoList = carPage.getContent().stream()
                .map(carMapper::toResponseDto)
                .toList();

        return new CarListResponseDTO(
                "Total " + carPage.getTotalElements() + " cars found.",
                carPage.getTotalElements(),
                carPage.getTotalPages(),
                dtoList
        );
    }

    @Cacheable(value = "cars", key = "#id")
    public CarResponseDTO getCarById(Long id) {
        return carRepository.findById(id)
                .map(carMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("There are no car has an ID you choose! ID: " + id));
    }

    @Transactional
    @CacheEvict(value = "cars", allEntries = true)
    public CarResponseDTO createCar(CarRequestDTO dto, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User cannot found!"));

        Car car = carMapper.toEntity(dto);
        car.setOwner(owner);

        if (owner.getRole() == Role.ROLE_ADMIN || owner.getRole() == Role.ROLE_MODERATOR) {
            car.setStatus("OWNED");
            log.info("Authorized User ({}) car added, approval skipped. Role: {}", username, owner.getRole());
        }
        else {
            car.setStatus("APPROVAL_WAITING");
            log.info("Standart User ({}) waiting for approval.", username);
        }

        return carMapper.toResponseDto(carRepository.save(car));
    }

    @Transactional
    @CacheEvict(value = "cars", key = "#id")
    public CarResponseDTO updateCar(Long id, CarRequestDTO dto) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car for update not found!"));

        carMapper.updateEntityFromDto(dto, car);

        // 👑 KURŞUN GEÇİRMEZ VIP KONTROLÜ
        boolean isVip = false;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().contains("ADMIN") || a.getAuthority().contains("MODERATOR"))) {
            isVip = true;
        } else if (car.getOwner() != null && car.getOwner().getRole() != null &&
                (car.getOwner().getRole().name().contains("ADMIN") || car.getOwner().getRole().name().contains("MODERATOR"))) {
            isVip = true;
        }

        if (isVip) {
            car.setStatus("OWNED");
            log.info("VIP Update: Araba ID {} status changed as OWNED.", id);
        } else {
            car.setStatus("APPROVAL_WAITING");
            log.info("Standart User Update: Car ID {} waiting for approval.", id);
        }

        return carMapper.toResponseDto(carRepository.save(car));
    }

    @Transactional
    @CacheEvict(value = "cars", key = "#id")
    public CarResponseDTO patchCar(Long id, CarUpdateDTO updateDto) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found!"));

        if (!"OWNED".equals(car.getStatus())) {
            throw new RuntimeException("Only owned cars can be updated!");
        }

        carMapper.patchEntityFromDto(updateDto, car);

        boolean isVip = false;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().contains("ADMIN") || a.getAuthority().contains("MODERATOR"))) {
            isVip = true;
        } else if (car.getOwner() != null && car.getOwner().getRole() != null &&
                (car.getOwner().getRole().name().contains("ADMIN") || car.getOwner().getRole().name().contains("MODERATOR"))) {
            isVip = true;
        }

        if (isVip) {
            car.setStatus("OWNED");
            log.info("VIP Patch: Car ID {} status changed as OWNED.", id);
        } else {
            car.setStatus("APPROVAL_WAITING");
            log.info("Standart User Patch: Car ID {} waiting for approval.", id);
        }

        return carMapper.toResponseDto(carRepository.save(car));
    }

    @Transactional
    @CacheEvict(value = "cars", key = "#id")
    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new RuntimeException("Car with this ID does not exist!");
        }
        carRepository.deleteById(id);
    }
}