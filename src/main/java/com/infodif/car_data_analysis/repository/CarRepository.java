package com.infodif.car_data_analysis.repository;

import com.infodif.car_data_analysis.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection; // 👈 Bu import önemli
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {

    List<Car> findByOwnerUsernameAndStatus(String ownerUsername, String status);

    List<Car> findByOwnerUsernameAndStatusIn(String ownerUsername, Collection<String> statuses);
}