package com.lrs.SSB.controller;
import java.time.LocalDate;

public class RecurringPaymentDTO {
    private Long id;
    private String serviceName;
    private String iban;
    private double amount;
    private String frequency;
    private String description;
    private LocalDate startDate;
    private String cardId;
    private String username;
    private Integer status;
    private String blockchainPrivateKey;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public String getCardId() { return cardId; }
    public void setCardId(String cardId) { this.cardId = cardId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    public String getBlockchainPrivateKey() { return blockchainPrivateKey; }
    public void setBlockchainPrivateKey(String blockchainPrivateKey) { this.blockchainPrivateKey = blockchainPrivateKey; }
}