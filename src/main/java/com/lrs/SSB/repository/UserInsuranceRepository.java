package com.lrs.SSB.repository;
import com.lrs.SSB.entity.UserInsurance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInsuranceRepository extends JpaRepository<UserInsurance, Long> {
    List<UserInsurance> findByUserId(Integer userId);
    boolean existsByUserIdAndInsuranceIdAndExtraDetails(Integer userId, Long insuranceId, String extraDetails);

}
