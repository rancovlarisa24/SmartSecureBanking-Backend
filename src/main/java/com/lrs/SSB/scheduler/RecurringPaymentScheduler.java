package com.lrs.SSB.scheduler;

import com.lrs.SSB.entity.RecurringPayment;
import com.lrs.SSB.repository.RecurringPaymentRepository;
import com.lrs.SSB.service.RecurringPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecurringPaymentScheduler {

    @Autowired
    private RecurringPaymentRepository recurringPaymentRepository;

    @Autowired
    private RecurringPaymentService recurringPaymentService;
    @Scheduled(fixedRate = 60000)
    public void processRecurringPayments() {
        LocalDate today = LocalDate.now();
        List<RecurringPayment> duePayments = recurringPaymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getStatus() == 1 &&
                        (payment.getStartDate().isBefore(today) || payment.getStartDate().isEqual(today)))
                .collect(Collectors.toList());

        for (RecurringPayment payment : duePayments) {
            try {

                recurringPaymentService.executeRecurringPayment(payment);
                LocalDate nextExecutionDate = recurringPaymentService.calculateNextExecutionDate(payment.getStartDate(), payment.getFrequency());
                payment.setStartDate(nextExecutionDate);
                recurringPaymentRepository.save(payment);
            } catch (Exception e) {
                System.err.println("Error processing recurring payment with id " + payment.getId() + ": " + e.getMessage());
            }
        }
    }
}
