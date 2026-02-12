package com.infodif.car_data_analysis.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
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

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", seller='" + sellerName + '\'' +
                ", buyer='" + buyerName + '\'' +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}