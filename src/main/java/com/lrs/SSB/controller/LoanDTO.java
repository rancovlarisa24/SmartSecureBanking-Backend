package com.lrs.SSB.controller;

import java.math.BigDecimal;

public class LoanDTO {
    private Integer id;
    private Integer borrowerId;
    private Integer lenderId;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private Integer repaymentMonths;
    private String purpose;
    private String contactInfo;
    private byte[] idPhoto;
    private String status;
    private String iban;
    private BigDecimal owedAmount;
    public LoanDTO() {}

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getBorrowerId() { return borrowerId; }
    public void setBorrowerId(Integer borrowerId) { this.borrowerId = borrowerId; }

    public Integer getLenderId() { return lenderId; }
    public void setLenderId(Integer lenderId) { this.lenderId = lenderId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    public Integer getRepaymentMonths() { return repaymentMonths; }
    public void setRepaymentMonths(Integer repaymentMonths) { this.repaymentMonths = repaymentMonths; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public byte[] getIdPhoto() { return idPhoto; }
    public void setIdPhoto(byte[] idPhoto) { this.idPhoto = idPhoto; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getOwedAmount() { return owedAmount; }
    public void setOwedAmount(BigDecimal owedAmount) { this.owedAmount = owedAmount; }
}
