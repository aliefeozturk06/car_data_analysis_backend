package com.infodif.car_data_analysis.service;

import com.infodif.car_data_analysis.dto.CarFilterDTO;
import com.infodif.car_data_analysis.dto.CarResponseDTO;
import com.infodif.car_data_analysis.dto.UpdateCarRequestDTO;
import com.infodif.car_data_analysis.entity.*;
import com.infodif.car_data_analysis.repository.*;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final CarRepository carRepository;
    private final CarUpdateApprovalRepository approvalRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public String buyCar(String username, Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Araç bulunamadı!"));

        User buyer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        boolean isBrandNew = car.getOwner() == null;
        boolean isOnSale = "ON_SALE".equals(car.getStatus());

        if (!isBrandNew && !isOnSale) {
            throw new RuntimeException("Bu araç şu an satılık değil!");
        }

        if (car.getOwner() != null && car.getOwner().getUsername().equals(username)) {
            throw new RuntimeException("Zaten kendi aracınızı satın alamazsınız!");
        }

        BigDecimal carPrice = BigDecimal.valueOf(car.getPrice());
        if (buyer.getBalance().compareTo(carPrice) < 0) {
            throw new RuntimeException("Bakiyeniz yetersiz!");
        }

        String sellerName = (car.getOwner() != null) ? car.getOwner().getUsername() : "GALLERY";

        buyer.setBalance(buyer.getBalance().subtract(carPrice));

        if (car.getOwner() != null) {
            User oldOwner = car.getOwner();
            oldOwner.setBalance(oldOwner.getBalance().add(carPrice));
            userRepository.save(oldOwner);
        }

        Transaction transaction = Transaction.builder()
                .carId(car.getId())
                .sellerName(sellerName)
                .buyerName(buyer.getUsername())
                .manufacturer(car.getManufacturer())
                .model(car.getModel())
                .year(car.getYear())
                .color(car.getColor())
                .mileage(car.getMileage())
                .price(carPrice.doubleValue())
                .saleDate(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        car.setStatus("OWNED");
        car.setOwner(buyer);

        carRepository.save(car);
        userRepository.save(buyer);

        return "Araç başarıyla satın alındı!";
    }

    @Transactional
    public String createUpdateRequest(UpdateCarRequestDTO dto) {
        if (dto.newPrice() == null && dto.newColor() == null && dto.newMileage() == null) {
            throw new RuntimeException("Fiyat, Renk veya Kilometre alanlarından en az biri doldurulmalıdır!");
        }

        User user = userRepository.findByUsername(dto.username())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        Car car = carRepository.findById(dto.carId())
                .orElseThrow(() -> new RuntimeException("Araç bulunamadı!"));

        if (user.getRole() == Role.ROLE_MODERATOR) {
            if (car.getOwner() != null && car.getOwner().getUsername().equals(dto.username())) {
                if (dto.newPrice() != null) car.setPrice(dto.newPrice());
                if (dto.newColor() != null) car.setColor(dto.newColor());
                if (dto.newMileage() != null) car.setMileage(dto.newMileage());
                carRepository.save(car);
                return "Sayın Moderatör, aracınız anında güncellendi!";
            }
        }

        boolean alreadyPending = approvalRepository.findByUsernameAndStatus(dto.username(), ApprovalStatus.PENDING)
                .stream().anyMatch(req -> req.getCarId().equals(dto.carId()));

        if (alreadyPending) {
            throw new RuntimeException("Bu araç için zaten onay bekleyen bir güncelleme isteğiniz var!");
        }

        CarUpdateApproval approval = CarUpdateApproval.builder()
                .carId(dto.carId())
                .username(dto.username())
                .requestedBy(user) // 👈 DÜZELTME BURADA: User entity'si veritabanı ilişkisi için eklendi
                .newPrice(dto.newPrice()) // Frontend'den newPrice olarak gelmeli
                .newColor(dto.newColor())
                .newMileage(dto.newMileage())
                .status(ApprovalStatus.PENDING)
                .requestDate(LocalDateTime.now())
                .build();

        approvalRepository.save(approval);
        return "Güncelleme talebi başarıyla oluşturuldu, moderatör onayı bekleniyor.";
    }

    @Transactional
    public String cancelUpdateRequest(String username, Long carId) {
        List<CarUpdateApproval> requests = approvalRepository.findByUsernameAndStatus(username, ApprovalStatus.PENDING);
        CarUpdateApproval targetRequest = requests.stream()
                .filter(req -> req.getCarId().equals(carId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Bu araç için bekleyen bir güncelleme isteği bulunamadı!"));

        approvalRepository.delete(targetRequest);
        return "Güncelleme isteği geri çekildi.";
    }

    public List<CarResponseDTO> getMyCars(String username, String status, CarFilterDTO filter) {
        Specification<Car> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("owner").get("username"), username));

            if (status != null && !status.equalsIgnoreCase("ALL")) {
                predicates.add(cb.equal(root.get("status"), status));
            } else {
                predicates.add(root.get("status").in(Arrays.asList("OWNED", "ON_SALE")));
            }

            if (filter != null) {
                if (filter.manufacturer() != null && !filter.manufacturer().isEmpty()) {
                    predicates.add(cb.like(cb.lower(root.get("manufacturer")), "%" + filter.manufacturer().toLowerCase() + "%"));
                }
                if (filter.model() != null && !filter.model().isEmpty()) {
                    predicates.add(cb.like(cb.lower(root.get("model")), "%" + filter.model().toLowerCase() + "%"));
                }
                if (filter.color() != null && !filter.color().isEmpty()) {
                    predicates.add(cb.like(cb.lower(root.get("color")), "%" + filter.color().toLowerCase() + "%"));
                }
                if (filter.minYear() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("year"), filter.minYear()));
                }
                if (filter.maxYear() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("year"), filter.maxYear()));
                }
                if (filter.minPrice() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.minPrice()));
                }
                if (filter.maxPrice() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.maxPrice()));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return carRepository.findAll(spec)
                .stream()
                .map(this::mapToCarResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public String listForSale(String username, Long carId) {
        Car car = carRepository.findById(carId).orElseThrow(() -> new RuntimeException("Araç bulunamadı!"));
        if (car.getOwner() == null || !car.getOwner().getUsername().equals(username)) {
            throw new RuntimeException("Yetkisiz işlem!");
        }
        car.setStatus("ON_SALE");
        carRepository.save(car);
        return "Araç satışa çıkarıldı!";
    }

    @Transactional
    public String cancelSale(String username, Long carId) {
        Car car = carRepository.findById(carId).orElseThrow(() -> new RuntimeException("Araç bulunamadı!"));
        if (car.getOwner() == null || !car.getOwner().getUsername().equals(username)) {
            throw new RuntimeException("Yetkisiz işlem!");
        }
        car.setStatus("OWNED");
        carRepository.save(car);
        return "Satış iptal edildi, araç galerinizde.";
    }

    public List<CarResponseDTO> getSoldHistory(String username) {
        List<Transaction> transactions = transactionRepository.findAllBySellerNameIgnoreCaseOrderBySaleDateDesc(username);
        return transactions.stream().map(t -> CarResponseDTO.builder()
                .id(t.getCarId())
                .manufacturer(t.getManufacturer())
                .model(t.getModel())
                .year(t.getYear())
                .price(t.getPrice().doubleValue())
                .color(t.getColor())
                .mileage(t.getMileage())
                .status("SOLD")
                .isUsed(true)
                .build()
        ).collect(Collectors.toList());
    }

    public List<UpdateCarRequestDTO> getAllPendingApprovals() {
        return approvalRepository.findByStatus(ApprovalStatus.PENDING)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public String approveCarUpdate(Long approvalId) {
        CarUpdateApproval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("İstek bulunamadı!"));

        Car car = carRepository.findById(approval.getCarId())
                .orElseThrow(() -> new RuntimeException("Araç bulunamadı!"));

        // 🚨 SAHİPLİK KONTROLÜ (GÜNCELLENDİ)
        boolean isOwnerChanged = false;
        if (car.getOwner() == null) {
            isOwnerChanged = true; // Araç galeriye düşmüş
        } else if (!car.getOwner().getUsername().equals(approval.getUsername())) {
            isOwnerChanged = true; // Araç başkasına satılmış
        }

        if (isOwnerChanged) {
            approvalRepository.delete(approval);
            return "OWNER_CHANGED";
        }

        if (approval.getNewPrice() != null) car.setPrice(approval.getNewPrice());
        if (approval.getNewColor() != null) car.setColor(approval.getNewColor());
        if (approval.getNewMileage() != null) car.setMileage(approval.getNewMileage());

        approval.setStatus(ApprovalStatus.APPROVED);
        carRepository.save(car);
        approvalRepository.delete(approval);

        return "SUCCESS";
    }

    @Transactional
    public String rejectCarUpdate(Long approvalId) {
        CarUpdateApproval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("İstek bulunamadı!"));
        approval.setStatus(ApprovalStatus.REJECTED);
        approvalRepository.delete(approval); // veya status update
        return "Reddedildi.";
    }

    public List<UpdateCarRequestDTO> getMyPendingRequests(String username) {
        return approvalRepository.findByUsernameAndStatus(username, ApprovalStatus.PENDING)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private CarResponseDTO mapToCarResponseDTO(Car car) {
        boolean hasPending = false;
        try {
            List<CarUpdateApproval> pendings = approvalRepository.findByUsernameAndStatus(
                    car.getOwner() != null ? car.getOwner().getUsername() : "", ApprovalStatus.PENDING);
            hasPending = pendings.stream().anyMatch(p -> p.getCarId().equals(car.getId()));
        } catch (Exception e) {}

        return CarResponseDTO.builder()
                .id(car.getId())
                .manufacturer(car.getManufacturer())
                .model(car.getModel())
                .year(car.getYear())
                .color(car.getColor())
                .price(car.getPrice())
                .mileage(car.getMileage())
                .status(car.getStatus())
                .isUsed(car.getMileage() != null && car.getMileage() > 0)
                .hasPendingUpdate(hasPending)
                .build();
    }

    private UpdateCarRequestDTO mapToDTO(CarUpdateApproval entity) {
        Car car = carRepository.findById(entity.getCarId()).orElse(null);

        return UpdateCarRequestDTO.builder()
                .id(entity.getId())
                .carId(entity.getCarId())
                .username(entity.getUsername())

                .newPrice(entity.getNewPrice())
                .newColor(entity.getNewColor())
                .newMileage(entity.getNewMileage())

                .oldPrice(car != null ? car.getPrice() : 0.0)
                .oldColor(car != null ? car.getColor() : "Bilinmiyor")
                .oldMileage(car != null ? car.getMileage() : 0)

                .status(entity.getStatus().name())
                .manufacturer(car != null ? car.getManufacturer() : "Bilinmiyor")
                .model(car != null ? car.getModel() : "Bilinmiyor")
                .requestDate(entity.getRequestDate())
                .build();
    }
}