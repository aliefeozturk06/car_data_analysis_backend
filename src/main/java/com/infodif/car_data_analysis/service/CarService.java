package com.infodif.car_data_analysis.service;

import com.infodif.car_data_analysis.dto.*;
import com.infodif.car_data_analysis.entity.Car;
import com.infodif.car_data_analysis.entity.User;
import com.infodif.car_data_analysis.repository.CarRepository;
import com.infodif.car_data_analysis.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public CarListResponseDTO getAllCars(CarFilterDTO filter) {
        List<Sort.Order> orders = new ArrayList<>();
        if (filter.sort() != null && !filter.sort().isEmpty()) {
            String[] sortParts = filter.sort().split(",");
            for (int i = 0; i < sortParts.length; i += 2) {
                String property = sortParts[i];
                String direction = (i + 1 < sortParts.length) ? sortParts[i+1] : "asc";

                Sort.Order order = direction.equalsIgnoreCase("desc")
                        ? Sort.Order.desc(property)
                        : Sort.Order.asc(property);
                orders.add(order);
            }
        }

        if (orders.isEmpty()) {
            orders.add(Sort.Order.asc("id"));
        }

        Sort sort = Sort.by(orders);
        Pageable pageable = PageRequest.of(filter.page(), filter.size(), sort);

        Specification<Car> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 🚨 ON_SALE FİLTRESİ BURADA KORUNDU
            predicates.add(cb.or(
                    cb.isNull(root.get("owner")),
                    cb.equal(root.get("status"), "ON_SALE")
            ));

            if (filter.manufacturer() != null && !filter.manufacturer().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("manufacturer")), filter.manufacturer().toLowerCase()));
            }

            if (filter.model() != null && !filter.model().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("model")), "%" + filter.model().toLowerCase() + "%"));
            }

            if (filter.color() != null && !filter.color().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("color")), filter.color().toLowerCase()));
            }

            if (filter.minYear() != null) predicates.add(cb.greaterThanOrEqualTo(root.get("year"), filter.minYear()));
            if (filter.maxYear() != null) predicates.add(cb.lessThanOrEqualTo(root.get("year"), filter.maxYear()));
            if (filter.minPrice() != null) predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.minPrice()));
            if (filter.maxPrice() != null) predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.maxPrice()));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Car> carPage = carRepository.findAll(spec, pageable);

        List<CarResponseDTO> dtoList = carPage.getContent().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        String message = "Total " + carPage.getTotalElements() + " cars found.";

        return CarListResponseDTO.builder()
                .message(message)
                .totalCount(carPage.getTotalElements())
                .totalPages(carPage.getTotalPages())
                .cars(dtoList)
                .build();
    }

    @Cacheable(value = "cars", key = "#id")
    public CarResponseDTO getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found!"));
        return toResponseDTO(car);
    }

    @Transactional
    @CacheEvict(value = "cars", allEntries = true)
    public CarResponseDTO createCar(CarRequestDTO dto, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Car car = Car.builder()
                .manufacturer(dto.manufacturer())
                .model(dto.model())
                .year(dto.year())
                .price(dto.price())
                .color(dto.color())
                .mileage(dto.mileage())
                .owner(owner)
                .status("OWNED")
                .build();

        return toResponseDTO(carRepository.save(car));
    }

    // 🟢 GERİ GETİRİLEN METOD: updateCar
    @Transactional
    @CacheEvict(value = "cars", key = "#id")
    public CarResponseDTO updateCar(Long id, CarRequestDTO dto) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car for update not found!"));
        updateEntityFromDto(car, dto);
        return toResponseDTO(carRepository.save(car));
    }

    // 🟢 GERİ GETİRİLEN METOD: patchCar
    @Transactional
    @CacheEvict(value = "cars", key = "#id")
    public CarResponseDTO patchCar(Long id, CarUpdateDTO updateDto) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found!"));

        if (!"OWNED".equals(car.getStatus())) {
            throw new RuntimeException("Only owned cars can be updated!");
        }

        if (updateDto.color() != null) car.setColor(updateDto.color());
        if (updateDto.mileage() != null) car.setMileage(updateDto.mileage());
        if (updateDto.price() != null) car.setPrice(updateDto.price());

        return toResponseDTO(carRepository.save(car));
    }

    @Transactional
    @CacheEvict(value = "cars", key = "#id")
    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new RuntimeException("Car with this ID does not exist!");
        }
        carRepository.deleteById(id);
    }

    private CarResponseDTO toResponseDTO(Car entity) {
        return CarResponseDTO.builder()
                .id(entity.getId())
                .manufacturer(entity.getManufacturer())
                .model(entity.getModel())
                .year(entity.getYear())
                .color(entity.getColor())
                .price(entity.getPrice())
                .mileage(entity.getMileage())
                .status(entity.getStatus())
                .isUsed(entity.getMileage() > 0)
                .build();
    }

    // 🟢 GERİ GETİRİLEN YARDIMCI METOD
    private void updateEntityFromDto(Car car, CarRequestDTO dto) {
        car.setManufacturer(dto.manufacturer());
        car.setModel(dto.model());
        car.setYear(dto.year());
        car.setColor(dto.color());
        car.setPrice(dto.price());
        car.setMileage(dto.mileage());
    }
}