package com.lrs.SSB.service;

import com.lrs.SSB.controller.LoanDTO;
import com.lrs.SSB.entity.Loan;
import com.lrs.SSB.entity.LoanStatus;
import com.lrs.SSB.entity.User;
import com.lrs.SSB.repository.LoanRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    private BigDecimal computeOwed(BigDecimal amount, BigDecimal interestRate) {
        BigDecimal factor = BigDecimal.ONE
                .add(interestRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        return amount.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    public LoanDTO createLoan(LoanDTO dto, User borrower, User lender) {
        Loan loan = new Loan();
        loan.setBorrower(borrower);
        loan.setLender(lender);
        loan.setAmount(dto.getAmount());
        loan.setInterestRate(dto.getInterestRate());
        loan.setOwedAmount(computeOwed(dto.getAmount(), dto.getInterestRate()));
        loan.setRepaymentMonths(dto.getRepaymentMonths());
        loan.setPurpose(dto.getPurpose());
        loan.setContactInfo(dto.getContactInfo());
        loan.setIdPhoto(dto.getIdPhoto());
        loan.setStatus(LoanStatus.PENDING);
        loan.setIban(dto.getIban());
        Loan saved = loanRepository.save(loan);

        return mapToDTO(saved);
    }

    public Optional<Loan> findEntityById(Integer id) {
        return loanRepository.findById(id);
    }
    public LoanDTO getLoanById(Integer id) {
        return loanRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
    }

    public List<LoanDTO> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public LoanDTO updateLoanStatus(Integer id, String status) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
        loan.setStatus(LoanStatus.valueOf(status));
        Loan updated = loanRepository.save(loan);
        return mapToDTO(updated);
    }

    public LoanDTO mapToDTO(Loan loan) {
        LoanDTO dto = new LoanDTO();
        dto.setId(loan.getId());
        dto.setBorrowerId(loan.getBorrower().getId());
        dto.setLenderId(loan.getLender().getId());
        dto.setAmount(loan.getAmount());
        dto.setInterestRate(loan.getInterestRate());
        dto.setOwedAmount(loan.getOwedAmount());
        dto.setRepaymentMonths(loan.getRepaymentMonths());
        dto.setPurpose(loan.getPurpose());
        dto.setContactInfo(loan.getContactInfo());
        dto.setIdPhoto(loan.getIdPhoto());
        dto.setStatus(loan.getStatus().name());
        dto.setIban(loan.getIban());

        return dto;
    }
    public Loan saveEntity(Loan loan) {
        return loanRepository.save(loan);
    }

    public void deleteById(Integer id) {
        loanRepository.deleteById(id);
    }

    public LoanDTO changeStatus(Integer id, LoanStatus newStatus) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (newStatus == LoanStatus.ARCHIVED) {
            if (loan.getStatus() != LoanStatus.REJECTED && loan.getStatus() != LoanStatus.PAYED) {
                throw new IllegalStateException("Only rejected or payed loans can be archived");
            }
        } else {
            if (loan.getStatus() != LoanStatus.PENDING) {
                throw new IllegalStateException("Only pending loans can be updated");
            }
        }

        loan.setStatus(newStatus);
        Loan updated = loanRepository.save(loan);
        return mapToDTO(updated);
    }
    @Transactional
    public LoanDTO applyPayment(Integer id, BigDecimal payAmount) {

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        BigDecimal newOwed = loan.getOwedAmount().subtract(payAmount);
        if (newOwed.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Payment bigger than outstanding balance");
        }

        loan.setOwedAmount(newOwed);
        if (newOwed.compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(LoanStatus.PAYED);
        }
        Loan updatedLoan = loanRepository.save(loan);
        return mapToDTO(updatedLoan);
    }


}
