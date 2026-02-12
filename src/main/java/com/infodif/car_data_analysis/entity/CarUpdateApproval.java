package com.infodif.car_data_analysis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CarUpdateApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long carId;
    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User requestedBy;

    private Double newPrice;
    private String newColor;
    private Integer newMileage;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    private LocalDateTime requestDate;

    public CarUpdateApproval(Long carId, String username, User requestedBy,
                             Double newPrice, String newColor, Integer newMileage) {
        this.carId = carId;
        this.username = username;
        this.requestedBy = requestedBy;
        this.newPrice = newPrice;
        this.newColor = newColor;
        this.newMileage = newMileage;
        this.status = ApprovalStatus.PENDING;
        this.requestDate = LocalDateTime.now();
    }
}