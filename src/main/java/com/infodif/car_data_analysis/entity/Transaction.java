package com.infodif.car_data_analysis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
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

    public Transaction(Long carId, String sellerName, String buyerName, String manufacturer,
                       String model, String color, Integer year, Integer mileage, Double price) {
        this.carId = carId;
        this.sellerName = sellerName;
        this.buyerName = buyerName;
        this.manufacturer = manufacturer;
        this.model = model;
        this.color = color;
        this.year = year;
        this.mileage = mileage;
        this.price = price;
        this.saleDate = LocalDateTime.now();
    }
}