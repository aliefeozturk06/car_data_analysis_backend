package com.infodif.car_data_analysis.service;

import com.infodif.car_data_analysis.dto.*;
import com.infodif.car_data_analysis.entity.Car;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final CarMapper carMapper;

    public CarListResponseDTO getAllCars(CarFilterDTO filter, String ownerUsername, String viewerUsername) {
        User viewer = (viewerUsername != null) ? userRepository.findByUsername(viewerUsername).orElse(null) : null;
        Double vLat = (viewer != null) ? viewer.getLatitude() : null;
        Double vLng = (viewer != null) ? viewer.getLongitude() : null;

        List<Sort.Order> orders = new ArrayList<>();
        boolean sortByDistance = false;
        Sort.Direction distanceDirection = Sort.Direction.ASC;

        if (filter.sort() != null && !filter.sort().isEmpty()) {
            String[] sortParts = filter.sort().split(",");
            for (int i = 0; i < sortParts.length; i += 2) {
                String property = sortParts[i];
                String dirStr = (i + 1 < sortParts.length) ? sortParts[i + 1] : "asc";
                Sort.Direction direction = dirStr.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

                if ("distance".equalsIgnoreCase(property) || "location".equalsIgnoreCase(property)) {
                    sortByDistance = true;
                    distanceDirection = direction;
                } else {
                    orders.add(new Sort.Order(direction, property));
                }
            }
        }
        if (orders.isEmpty() && !sortByDistance) orders.add(Sort.Order.asc("id"));

        Pageable pageable = PageRequest.of(filter.page(), filter.size(), Sort.by(orders));

        Specification<Car> spec = (root, query, cb) -> cb.conjunction();

        if (ownerUsername != null && !ownerUsername.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("owner", JoinType.LEFT).get("username"), ownerUsername)
            );
        } else {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), "ON_SALE"));
        }

        spec = spec.and(CarSpecifications.hasManufacturer(filter.manufacturer()))
                .and(CarSpecifications.hasModel(filter.model()))
                .and(CarSpecifications.hasColor(filter.color()))
                .and(CarSpecifications.hasYearBetween(filter.minYear(), filter.maxYear()))
                .and(CarSpecifications.hasPriceBetween(filter.minPrice(), filter.maxPrice()))
                .and(CarSpecifications.hasMileageBetween(filter.minMileage(), filter.maxMileage()));

        Page<Car> carPage = carRepository.findAll(spec, pageable);

        List<CarResponseDTO> dtoList = carPage.getContent().stream()
                .map(car -> {
                    CarResponseDTO dto = carMapper.toResponseDto(car);
                    Double distance = null;

                    if (vLat != null && vLng != null && car.getOwner() != null &&
                            car.getOwner().getLatitude() != null && car.getOwner().getLongitude() != null) {
                        distance = calculateHaversine(vLat, vLng, car.getOwner().getLatitude(), car.getOwner().getLongitude());
                    }

                    return new CarResponseDTO(
                            dto.id(), dto.manufacturer(), dto.model(), dto.year(), dto.color(),
                            dto.price(), dto.mileage(), dto.status(), dto.sellerLocation(),
                            dto.isUsed(), dto.hasPendingUpdate(),
                            car.getOwner() != null ? car.getOwner().getLatitude() : null,
                            car.getOwner() != null ? car.getOwner().getLongitude() : null,
                            distance
                    );
                })
                .collect(Collectors.toList());

        if (sortByDistance) {
            Comparator<CarResponseDTO> distanceComparator = Comparator.comparing(
                    CarResponseDTO::distance,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            if (distanceDirection == Sort.Direction.DESC) distanceComparator = distanceComparator.reversed();
            dtoList.sort(distanceComparator);
        }

        return new CarListResponseDTO(
                "Analysis complete. Total " + carPage.getTotalElements() + " cars found.",
                carPage.getTotalElements(),
                carPage.getTotalPages(),
                dtoList
        );
    }

    @Cacheable(value = "cars", key = "#id")
    public CarResponseDTO getCarById(Long id) {
        return carRepository.findById(id)
                .map(carMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found! ID: " + id));
    }

    @Transactional
    @CacheEvict(value = "cars", allEntries = true)
    public CarResponseDTO createCar(CarRequestDTO dto, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        Car car = carMapper.toEntity(dto);
        car.setOwner(owner);
        car.setStatus("OWNED"); // Initial status after creation

        log.info("Car created for user: {}", username);
        return carMapper.toResponseDto(carRepository.save(car));
    }

    @Transactional
    @CacheEvict(value = "cars", key = "#id")
    public CarResponseDTO updateCar(Long id, CarRequestDTO dto) {
        Car car = carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found!"));
        carMapper.updateEntityFromDto(dto, car);
        handleStatusAfterUpdate(car);
        return carMapper.toResponseDto(carRepository.save(car));
    }

    @Transactional
    @CacheEvict(value = "cars", key = "#id")
    public CarResponseDTO patchCar(Long id, CarUpdateDTO updateDto) {
        Car car = carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found!"));
        if (!"OWNED".equals(car.getStatus()) && !"ON_SALE".equals(car.getStatus())) {
            throw new RuntimeException("Only owned or for-sale cars can be patched!");
        }
        carMapper.patchEntityFromDto(updateDto, car);
        handleStatusAfterUpdate(car);
        return carMapper.toResponseDto(carRepository.save(car));
    }

    @Transactional
    @CacheEvict(value = "cars", key = "#id")
    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) throw new RuntimeException("Car not found!");
        carRepository.deleteById(id);
    }

    private void handleStatusAfterUpdate(Car car) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isVip = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("ADMIN") || a.getAuthority().contains("MODERATOR"));

        if (isVip) {
            log.info("VIP user update: Auto-approving changes for Car ID {}", car.getId());
        } else {
            car.setStatus("APPROVAL_WAITING");
            log.info("Standard user update: Car ID {} sent to approval queue.", car.getId());
        }
    }

    private double calculateHaversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}