package com.infodif.car_data_analysis.specification;

import com.infodif.car_data_analysis.entity.Car;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;

public class CarSpecifications {

    public static Specification<Car> hasManufacturer(String manufacturer) {
        return (root, query, cb) -> (manufacturer == null || manufacturer.isEmpty())
                ? cb.conjunction()
                : cb.equal(cb.lower(root.get("manufacturer")), manufacturer.toLowerCase());
    }

    public static Specification<Car> hasColor(String color) {
        return (root, query, cb) -> (color == null || color.isEmpty())
                ? cb.conjunction()
                : cb.equal(cb.lower(root.get("color")), color.toLowerCase());
    }

    public static Specification<Car> hasModel(String model) {
        return (root, query, cb) -> (model == null || model.isEmpty())
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("model")), "%" + model.toLowerCase() + "%");
    }

    public static Specification<Car> hasYearBetween(Integer minYear, Integer maxYear) {
        return (root, query, cb) -> {
            if (minYear == null && maxYear == null) return cb.conjunction();
            if (minYear == null) return cb.lessThanOrEqualTo(root.get("year"), maxYear);
            if (maxYear == null) return cb.greaterThanOrEqualTo(root.get("year"), minYear);
            return cb.between(root.get("year"), minYear, maxYear);
        };
    }

    public static Specification<Car> hasPriceBetween(Double minPrice, Double maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) return cb.conjunction();
            if (minPrice == null) return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
            if (maxPrice == null) return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            return cb.between(root.get("price"), minPrice, maxPrice);
        };
    }

    public static Specification<Car> hasMileageBetween(Integer minMileage, Integer maxMileage) {
        return (root, query, cb) -> {
            if (minMileage == null && maxMileage == null) return cb.conjunction();
            if (minMileage == null) return cb.lessThanOrEqualTo(root.get("mileage"), maxMileage);
            if (maxMileage == null) return cb.greaterThanOrEqualTo(root.get("mileage"), minMileage);
            return cb.between(root.get("mileage"), minMileage, maxMileage);
        };
    }

    public static Specification<Car> hasStatus(String status) {
        return (root, query, cb) -> (status == null || status.isEmpty())
                ? cb.conjunction()
                : cb.equal(root.get("status"), status);
    }
}