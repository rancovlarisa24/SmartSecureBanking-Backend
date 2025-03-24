package com.lrs.SSB.repository;
import com.lrs.SSB.entity.RecurringPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecurringPaymentRepository extends JpaRepository<RecurringPayment, Long> {
    List<RecurringPayment> findByUsername(String username);
}