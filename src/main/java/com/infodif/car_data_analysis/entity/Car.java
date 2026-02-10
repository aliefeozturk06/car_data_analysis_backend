package com.infodif.car_data_analysis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "cars")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String manufacturer;

    @Column(nullable = false)
    private String model;

    private String color;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer mileage;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String status; // ON_SALE veya OWNED

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}