package com.infodif.car_data_analysis.service;

import com.infodif.car_data_analysis.dto.*;
import com.infodif.car_data_analysis.entity.*;
import com.infodif.car_data_analysis.mapper.CarMapper;
import com.infodif.car_data_analysis.mapper.CarUpdateApprovalMapper;
import com.infodif.car_data_analysis.mapper.TransactionMapper;
import com.infodif.car_data_analysis.repository.*;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseService {

    private final CarRepository carRepository;
    private final CarUpdateApprovalRepository approvalRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    private final CarMapper carMapper;
    private final CarUpdateApprovalMapper approvalMapper;
    private final TransactionMapper transactionMapper;

    @Transactional
    public String buyCar(String username, Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found!"));

        User buyer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        boolean isBrandNew = car.getOwner() == null;
        boolean isOnSale = "ON_SALE".equals(car.getStatus());

        if (!isBrandNew && !isOnSale) {
            throw new RuntimeException("This car is not for sale!");
        }

        if (car.getOwner() != null && car.getOwner().getUsername().equals(username)) {
            throw new RuntimeException("You cannot purchase your own car!");
        }

        BigDecimal carPrice = BigDecimal.valueOf(car.getPrice());
        if (buyer.getBalance().compareTo(carPrice) < 0) {
            throw new RuntimeException("Your balance is insufficient!");
        }

        String sellerName = (car.getOwner() != null) ? car.getOwner().getUsername() : "GALLERY";
        buyer.setBalance(buyer.getBalance().subtract(carPrice));

        if (car.getOwner() != null) {
            User oldOwner = car.getOwner();
            oldOwner.setBalance(oldOwner.getBalance().add(carPrice));
            userRepository.save(oldOwner);
        }

        Transaction transaction = new Transaction(
                car.getId(), sellerName, buyer.getUsername(),
                car.getManufacturer(), car.getModel(), car.getColor(),
                car.getYear(), car.getMileage(), carPrice.doubleValue()
        );
        transactionRepository.save(transaction);

        car.setStatus("OWNED");
        car.setOwner(buyer);

        carRepository.save(car);
        userRepository.save(buyer);

        return "Car purchased!";
    }

    @Transactional
    public String createUpdateRequest(UpdateCarRequestDTO dto) {
        if (dto.newPrice() == null && dto.newColor() == null && dto.newMileage() == null) {
            throw new RuntimeException("Price, color or mileage should be filled!");
        }

        User user = userRepository.findByUsername(dto.username())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Car car = carRepository.findById(dto.carId())
                .orElseThrow(() -> new RuntimeException("Car not found!"));

        if (user.getRole() == Role.ROLE_MODERATOR || user.getRole() == Role.ROLE_ADMIN) {
            if (car.getOwner() != null && car.getOwner().getUsername().equals(dto.username())) {
                if (dto.newPrice() != null) car.setPrice(dto.newPrice());
                if (dto.newColor() != null) car.setColor(dto.newColor());
                if (dto.newMileage() != null) car.setMileage(dto.newMileage());
                carRepository.save(car);
                return "VIP Update: Your car is updated instantly!";
            }
        }

        boolean alreadyPending = approvalRepository.findByUsernameAndStatus(dto.username(), ApprovalStatus.PENDING)
                .stream().anyMatch(req -> req.getCarId().equals(dto.carId()));

        if (alreadyPending) {
            throw new RuntimeException("There is already a pending approval request for this car!");
        }

        CarUpdateApproval approval = new CarUpdateApproval(
                car.getId(),
                dto.username(),
                user,
                car.getPrice(),
                dto.newPrice(),
                car.getColor(),
                dto.newColor(),
                car.getMileage(),
                dto.newMileage()
        );

        approvalRepository.save(approval);
        return "Approval request has been sent.";
    }

    @Transactional
    public String approveCarUpdate(Long approvalId) {
        CarUpdateApproval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Request not found!"));

        Car car = carRepository.findById(approval.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found!"));

        boolean isOwnerChanged = (car.getOwner() == null) || !car.getOwner().getUsername().equals(approval.getUsername());
        if (isOwnerChanged) {
            approval.setStatus(ApprovalStatus.REJECTED);
            approvalRepository.save(approval);
            return "OWNER_CHANGED";
        }

        if (approval.getNewPrice() != null) car.setPrice(approval.getNewPrice());
        if (approval.getNewColor() != null) car.setColor(approval.getNewColor());
        if (approval.getNewMileage() != null) car.setMileage(approval.getNewMileage());

        carRepository.save(car);

        approval.setStatus(ApprovalStatus.APPROVED);
        approvalRepository.save(approval);

        return "SUCCESS";
    }

    @Transactional
    public String rejectCarUpdate(Long approvalId) {
        CarUpdateApproval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Request not found!"));

        approval.setStatus(ApprovalStatus.REJECTED);
        approvalRepository.save(approval);

        return "Rejected.";
    }

    private UpdateCarRequestDTO mapToRequestDto(CarUpdateApproval approval) {
        Car car = carRepository.findById(approval.getCarId()).orElse(null);
        return new UpdateCarRequestDTO(
                approval.getId(),
                approval.getCarId(),
                approval.getUsername(),
                approval.getNewPrice(),
                approval.getNewColor(),
                approval.getNewMileage(),
                approval.getOldPrice(),
                approval.getOldColor(),
                approval.getOldMileage(),
                car != null ? car.getManufacturer() : "N/A",
                car != null ? car.getModel() : "N/A",
                approval.getStatus().name(),
                approval.getRequestDate()
        );
    }

    public List<UpdateCarRequestDTO> getAllPendingApprovals() {
        return approvalRepository.findByStatus(ApprovalStatus.PENDING)
                .stream().map(this::mapToRequestDto).toList();
    }

    public List<UpdateCarRequestDTO> getMyUpdateHistory(String username) {
        return approvalRepository.findByUsernameOrderByRequestDateDesc(username)
                .stream().map(this::mapToRequestDto).toList();
    }

    public List<UpdateCarRequestDTO> getMyPendingRequests(String username) {
        return approvalRepository.findByUsernameAndStatus(username, ApprovalStatus.PENDING)
                .stream().map(this::mapToRequestDto).toList();
    }

    @Transactional
    public String cancelUpdateRequest(String username, Long carId) {
        approvalRepository.findByUsernameAndStatus(username, ApprovalStatus.PENDING)
                .stream()
                .filter(req -> req.getCarId().equals(carId))
                .findFirst()
                .ifPresent(approvalRepository::delete);
        return "Approval request deleted.";
    }

    public List<CarResponseDTO> getMyCars(String username, String status, CarFilterDTO filter) {
        User viewer = userRepository.findByUsername(username).orElse(null);
        Double vLat = (viewer != null) ? viewer.getLatitude() : null;
        Double vLng = (viewer != null) ? viewer.getLongitude() : null;

        Specification<Car> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("owner").get("username"), username));

            if (status != null && !status.equalsIgnoreCase("ALL")) {
                predicates.add(cb.equal(root.get("status"), status));
            } else {
                predicates.add(root.get("status").in(Arrays.asList("OWNED", "ON_SALE")));
            }

            if (filter != null) {
                if (filter.manufacturer() != null && !filter.manufacturer().isEmpty())
                    predicates.add(cb.like(cb.lower(root.get("manufacturer")), "%" + filter.manufacturer().toLowerCase() + "%"));
                if (filter.model() != null && !filter.model().isEmpty())
                    predicates.add(cb.like(cb.lower(root.get("model")), "%" + filter.model().toLowerCase() + "%"));
                if (filter.color() != null && !filter.color().isEmpty())
                    predicates.add(cb.like(cb.lower(root.get("color")), "%" + filter.color().toLowerCase() + "%"));
                if (filter.minYear() != null) predicates.add(cb.greaterThanOrEqualTo(root.get("year"), filter.minYear()));
                if (filter.maxYear() != null) predicates.add(cb.lessThanOrEqualTo(root.get("year"), filter.maxYear()));
                if (filter.minPrice() != null) predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.minPrice()));
                if (filter.maxPrice() != null) predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.maxPrice()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return carRepository.findAll(spec).stream()
                .map(car -> {
                    CarResponseDTO dto = carMapper.toResponseDto(car);

                    Double distance = null;
                    if (vLat != null && vLng != null && car.getOwner() != null &&
                            car.getOwner().getLatitude() != null && car.getOwner().getLongitude() != null) {
                        distance = calculateHaversine(vLat, vLng, car.getOwner().getLatitude(), car.getOwner().getLongitude());
                    }

                    boolean hasPending = approvalRepository.findByUsernameAndStatus(username, ApprovalStatus.PENDING)
                            .stream().anyMatch(p -> p.getCarId().equals(car.getId()));

                    return new CarResponseDTO(
                            dto.id(), dto.manufacturer(), dto.model(), dto.year(), dto.color(),
                            dto.price(), dto.mileage(), dto.status(), dto.sellerLocation(), dto.isUsed(), hasPending,
                            car.getOwner() != null ? car.getOwner().getLatitude() : null,
                            car.getOwner() != null ? car.getOwner().getLongitude() : null,
                            distance
                    );
                }).toList();
    }

    @Transactional
    public String listForSale(String username, Long carId) {
        Car car = carRepository.findById(carId).orElseThrow();
        if (car.getOwner() == null || !car.getOwner().getUsername().equals(username)) throw new RuntimeException("Unauthorized!");
        car.setStatus("ON_SALE");
        carRepository.save(car);
        return "Car listed for sale.";
    }

    @Transactional
    public String cancelSale(String username, Long carId) {
        Car car = carRepository.findById(carId).orElseThrow();
        if (car.getOwner() == null || !car.getOwner().getUsername().equals(username)) throw new RuntimeException("Unauthorized!");
        car.setStatus("OWNED");
        carRepository.save(car);
        return "Sale cancelled.";
    }

    public List<TransactionDTO> getSoldHistory(String username) {
        return transactionRepository.findAllBySellerNameIgnoreCaseOrderBySaleDateDesc(username)
                .stream().map(transactionMapper::toDto).toList();
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