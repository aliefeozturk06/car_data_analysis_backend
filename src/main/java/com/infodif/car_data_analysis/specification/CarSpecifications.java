package com.infodif.car_data_analysis.specification;

import com.infodif.car_data_analysis.entity.ApprovalStatus;
import com.infodif.car_data_analysis.entity.Car;
import org.springframework.data.jpa.domain.Specification;

public final class CarSpecifications {

    private CarSpecifications() {}

    public static Specification<Car> hasManufacturer(String manufacturer) {
        return (root, query, cb) -> (manufacturer == null || manufacturer.isBlank())
                ? cb.conjunction()
                : cb.equal(cb.lower(root.get("manufacturer")), manufacturer.toLowerCase().trim());
    }

    public static Specification<Car> hasColor(String color) {
        return (root, query, cb) -> (color == null || color.isBlank())
                ? cb.conjunction()
                : cb.equal(cb.lower(root.get("color")), color.toLowerCase().trim());
    }

    public static Specification<Car> hasModel(String model) {
        return (root, query, cb) -> (model == null || model.isBlank())
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("model")), "%" + model.toLowerCase().trim() + "%");
    }

    public static Specification<Car> hasYearBetween(Integer minYear, Integer maxYear) {
        return (root, query, cb) -> {
            if (minYear == null && maxYear == null) return cb.conjunction();
            if (minYear != null && maxYear != null) return cb.between(root.get("year"), minYear, maxYear);
            return minYear != null
                    ? cb.greaterThanOrEqualTo(root.get("year"), minYear)
                    : cb.lessThanOrEqualTo(root.get("year"), maxYear);
        };
    }

    public static Specification<Car> hasPriceBetween(Double minPrice, Double maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) return cb.conjunction();
            if (minPrice != null && maxPrice != null) return cb.between(root.get("price"), minPrice, maxPrice);
            return minPrice != null
                    ? cb.greaterThanOrEqualTo(root.get("price"), minPrice)
                    : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    public static Specification<Car> hasMileageBetween(Integer minMileage, Integer maxMileage) {
        return (root, query, cb) -> {
            if (minMileage == null && maxMileage == null) return cb.conjunction();
            if (minMileage != null && maxMileage != null) return cb.between(root.get("mileage"), minMileage, maxMileage);
            return minMileage != null
                    ? cb.greaterThanOrEqualTo(root.get("mileage"), minMileage)
                    : cb.lessThanOrEqualTo(root.get("mileage"), maxMileage);
        };
    }

    public static Specification<Car> hasStatus(String status) {
        return (root, query, cb) -> (status == null || status.isBlank())
                ? cb.conjunction()
                : cb.equal(root.get("status"), status); // ✅ String kıyaslaması daha garantidir
    }
}