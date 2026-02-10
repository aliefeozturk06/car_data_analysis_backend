package com.infodif.car_data_analysis.repository;

import com.infodif.car_data_analysis.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllBySellerNameIgnoreCaseOrderBySaleDateDesc(String sellerName);
}