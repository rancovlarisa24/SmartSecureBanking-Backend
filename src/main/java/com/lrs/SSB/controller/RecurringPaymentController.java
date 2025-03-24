package com.lrs.SSB.controller;
import com.lrs.SSB.entity.RecurringPayment;
import com.lrs.SSB.repository.RecurringPaymentRepository;
import com.lrs.SSB.service.RecurringPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recurring-payments")
public class RecurringPaymentController {

    @Autowired
    private RecurringPaymentService service;

    @PostMapping
    public ResponseEntity<RecurringPaymentDTO> create(@RequestBody RecurringPaymentDTO dto) {
        RecurringPayment payment = new RecurringPayment();
        payment.setServiceName(dto.getServiceName());
        payment.setIban(dto.getIban());
        payment.setAmount(dto.getAmount());
        payment.setFrequency(dto.getFrequency());
        payment.setDescription(dto.getDescription());
        payment.setStartDate(dto.getStartDate());
        payment.setCardId(dto.getCardId());
        payment.setUsername(dto.getUsername());
        payment.setBlockchainPrivateKey(dto.getBlockchainPrivateKey());
        if (dto.getStatus() == null) {
            payment.setStatus(1);
        } else {
            payment.setStatus(dto.getStatus());
        }

        RecurringPayment saved = service.save(payment);
        RecurringPaymentDTO responseDto = new RecurringPaymentDTO();
        responseDto.setServiceName(saved.getServiceName());
        responseDto.setIban(saved.getIban());
        responseDto.setAmount(saved.getAmount());
        responseDto.setFrequency(saved.getFrequency());
        responseDto.setDescription(saved.getDescription());
        responseDto.setStartDate(saved.getStartDate());
        responseDto.setCardId(saved.getCardId());
        responseDto.setUsername(saved.getUsername());
        responseDto.setStatus(saved.getStatus());
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<RecurringPaymentDTO>> getByUsername(@PathVariable String username) {
        List<RecurringPayment> payments = service.getByUsername(username);

        List<RecurringPaymentDTO> dtos = payments.stream().map(p -> {
            RecurringPaymentDTO dto = new RecurringPaymentDTO();
            dto.setId(p.getId());
            dto.setServiceName(p.getServiceName());
            dto.setIban(p.getIban());
            dto.setAmount(p.getAmount());
            dto.setFrequency(p.getFrequency());
            dto.setDescription(p.getDescription());
            dto.setStartDate(p.getStartDate());
            dto.setCardId(p.getCardId());
            dto.setUsername(p.getUsername());
            dto.setStatus(p.getStatus());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    public ResponseEntity<List<RecurringPaymentDTO>> getAll() {
        List<RecurringPayment> payments = service.getAll();

        List<RecurringPaymentDTO> dtos = payments.stream().map(p -> {
            RecurringPaymentDTO dto = new RecurringPaymentDTO();
            dto.setServiceName(p.getServiceName());
            dto.setIban(p.getIban());
            dto.setAmount(p.getAmount());
            dto.setFrequency(p.getFrequency());
            dto.setDescription(p.getDescription());
            dto.setStartDate(p.getStartDate());
            dto.setCardId(p.getCardId());
            dto.setUsername(p.getUsername());
            dto.setStatus(p.getStatus());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Optional<RecurringPayment> paymentOpt = service.findById(id);
        if (paymentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RecurringPayment payment = paymentOpt.get();
        Integer newStatus = body.get("status");
        if (newStatus == null || (newStatus != 0 && newStatus != 1)) {
            return ResponseEntity.badRequest().body("Invalid status value.");
        }

        payment.setStatus(newStatus);
        service.save(payment);
        return ResponseEntity.ok("Recurring payment status updated.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecurringPayment(@PathVariable Long id, @RequestBody RecurringPaymentDTO dto) {
        Optional<RecurringPayment> existingOpt = service.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RecurringPayment existing = existingOpt.get();

        existing.setAmount(dto.getAmount());
        existing.setFrequency(dto.getFrequency());
        existing.setStartDate(dto.getStartDate());
        existing.setCardId(dto.getCardId());
        existing.setDescription(dto.getDescription());

        service.save(existing);
        return ResponseEntity.ok("Recurring payment updated");
    }


}