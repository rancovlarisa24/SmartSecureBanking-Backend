package com.lrs.SSB.controller;

import com.lrs.SSB.controller.LoanDTO;
import com.lrs.SSB.entity.Loan;
import com.lrs.SSB.entity.LoanStatus;
import com.lrs.SSB.entity.User;
import com.lrs.SSB.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanDTO> createLoan(@RequestBody LoanDTO dto, @RequestParam Integer borrowerId, @RequestParam Integer lenderId) {
        User borrower = new User(); borrower.setId(borrowerId);
        User lender = new User(); lender.setId(lenderId);
        LoanDTO created = loanService.createLoan(dto, borrower, lender);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanDTO> getLoan(@PathVariable Integer id) {
        LoanDTO dto = loanService.getLoanById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<LoanDTO>> getAllLoans() {
        List<LoanDTO> list = loanService.getAllLoans();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<LoanDTO> updateStatus(@PathVariable Integer id, @RequestParam String status) {
        LoanDTO updated = loanService.updateLoanStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{loanId}/upload-id-photo")
    public ResponseEntity<?> uploadLoanIdPhoto(
            @PathVariable Integer loanId,
            @RequestParam("image") MultipartFile imageFile
    ) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body("No file provided.");
        }
        Optional<Loan> opt = loanService.findEntityById(loanId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loan not found.");
        }
        Loan loan = opt.get();
        loan.setIdPhoto(imageFile.getBytes());
        loanService.saveEntity(loan);
        return ResponseEntity.ok(Map.of("message", "ID photo uploaded."));
    }

    @GetMapping("/{loanId}/id-photo")
    public ResponseEntity<byte[]> getLoanIdPhoto(@PathVariable Integer loanId) {
        Optional<Loan> opt = loanService.findEntityById(loanId);
        if (opt.isEmpty() || opt.get().getIdPhoto() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        byte[] img = opt.get().getIdPhoto();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setCacheControl(CacheControl.noCache());
        return new ResponseEntity<>(img, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Integer id) {
        Optional<Loan> opt = loanService.findEntityById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        loanService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoanDTO> updateLoan(
            @PathVariable Integer id,
            @RequestBody LoanDTO dto
    ) {
        Loan existing = loanService.findEntityById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        existing.setAmount(dto.getAmount());
        existing.setInterestRate(dto.getInterestRate());
        existing.setRepaymentMonths(dto.getRepaymentMonths());
        existing.setPurpose(dto.getPurpose());
        existing.setContactInfo(dto.getContactInfo());
        BigDecimal factor = BigDecimal.ONE
                .add(dto.getInterestRate()
                        .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        existing.setOwedAmount(
                dto.getAmount()
                        .multiply(factor)
                        .setScale(2, RoundingMode.HALF_UP)
        );

        Loan updated = loanService.saveEntity(existing);
        return ResponseEntity.ok(loanService.mapToDTO(updated));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<LoanDTO> approveLoan(@PathVariable Integer id) {
        LoanDTO dto = loanService.changeStatus(id, LoanStatus.APPROVED);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<LoanDTO> rejectLoan(@PathVariable Integer id) {
        LoanDTO dto = loanService.changeStatus(id, LoanStatus.REJECTED);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<LoanDTO> archiveLoan(@PathVariable Integer id) {
        LoanDTO dto = loanService.changeStatus(id, LoanStatus.ARCHIVED);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<LoanDTO> payLoan(
            @PathVariable Integer id,
            @RequestParam BigDecimal amount) {

        LoanDTO dto = loanService.applyPayment(id, amount);
        return ResponseEntity.ok(dto);
    }


}
