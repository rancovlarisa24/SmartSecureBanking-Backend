package com.lrs.SSB.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "borrower_id", nullable = false)
    private User borrower;

    @ManyToOne
    @JoinColumn(name = "lender_id", nullable = false)
    private User lender;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "repayment_months", nullable = false)
    private Integer repaymentMonths;

    @Column(columnDefinition = "TEXT")
    private String purpose;

    @Column(name = "contact_info", length = 255)
    private String contactInfo;

    @Lob
    @Column(name = "id_photo", columnDefinition = "LONGBLOB")
    private byte[] idPhoto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status = LoanStatus.PENDING;

    @Column(length = 34)
    private String iban;

    @Column(name = "owed_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal owedAmount;

    public BigDecimal getOwedAmount() { return owedAmount; }
    public void setOwedAmount(BigDecimal owedAmount) { this.owedAmount = owedAmount; }

    public Loan() {}

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getBorrower() {
        return borrower;
    }

    public void setBorrower(User borrower) {
        this.borrower = borrower;
    }

    public User getLender() {
        return lender;
    }

    public void setLender(User lender) {
        this.lender = lender;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getRepaymentMonths() {
        return repaymentMonths;
    }

    public void setRepaymentMonths(Integer repaymentMonths) {
        this.repaymentMonths = repaymentMonths;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public byte[] getIdPhoto() {
        return idPhoto;
    }
    public void setIdPhoto(byte[] idPhoto) {
        this.idPhoto = idPhoto;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    @PrePersist
    private void calculateOwedAmount() {
        if (amount != null && interestRate != null) {
            BigDecimal factor = BigDecimal.ONE
                    .add(interestRate.divide(BigDecimal.valueOf(100),  4, RoundingMode.HALF_UP));
            this.owedAmount = amount.multiply(factor)
                    .setScale(2, RoundingMode.HALF_UP);
        }
    }
}
