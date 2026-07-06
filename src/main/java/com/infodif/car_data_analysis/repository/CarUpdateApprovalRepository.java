package com.infodif.car_data_analysis.repository;

import com.infodif.car_data_analysis.entity.ApprovalStatus;
import com.infodif.car_data_analysis.entity.CarUpdateApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CarUpdateApprovalRepository extends JpaRepository<CarUpdateApproval, Long> {
    long countByUsernameAndStatus(String username, ApprovalStatus status);
    List<CarUpdateApproval> findByUsernameAndStatus(String username, ApprovalStatus status);
    List<CarUpdateApproval> findByStatus(ApprovalStatus status);
    List<CarUpdateApproval> findByUsernameOrderByRequestDateDesc(String username);

    @Query("SELECT a.username, COUNT(a) FROM CarUpdateApproval a WHERE a.status = 'PENDING' GROUP BY a.username")
    List<Object[]> countPendingGroupedByUsername();
}