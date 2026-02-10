package com.infodif.car_data_analysis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long carId;

    @Column(nullable = false)
    private String sellerName;

    @Column(nullable = false)
    private String buyerName;

    private String manufacturer;
    private String model;
    private String color;
    private Integer year;
    private Integer mileage;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private LocalDateTime saleDate;

    @PrePersist
    protected void onCreate() {
        this.saleDate = LocalDateTime.now();
    }
}