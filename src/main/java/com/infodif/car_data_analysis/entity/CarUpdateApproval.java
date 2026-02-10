package com.infodif.car_data_analysis.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "car_update_approvals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarUpdateApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long carId;

    private String username;

    @ManyToOne
    @JoinColumn(name = "requested_by_id", nullable = false)
    private User requestedBy;

    private Double newPrice;
    private String newColor;
    private Integer newMileage;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    private LocalDateTime requestDate;
}