package com.lrs.SSB.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "recurring_payments")
public class RecurringPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "iban", nullable = false)
    private String iban;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "frequency", nullable = false)
    private String frequency;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "card_id", nullable = false)
    private String cardId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "status", nullable = false)
    private int status = 1;

    @Column(name = "blockchain_key", nullable = false)
    private String blockchainPrivateKey;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public String getBlockchainPrivateKey() { return blockchainPrivateKey; }
    public void setBlockchainPrivateKey(String blockchainPrivateKey) { this.blockchainPrivateKey = blockchainPrivateKey; }
}