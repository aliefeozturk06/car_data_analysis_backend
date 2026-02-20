package com.infodif.car_data_analysis.repository;

import com.infodif.car_data_analysis.entity.Car;
import com.infodif.car_data_analysis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {
    long countByOwnerAndStatus(User owner, String status);}