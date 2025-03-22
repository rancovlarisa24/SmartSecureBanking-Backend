package com.lrs.SSB.controller;

import com.lrs.SSB.entity.TransactionType;
import java.math.BigDecimal;

public class TransactionRequest {
    private Long cardId;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String beneficiaryName;
    private String beneficiaryIban;
    private String sourceId;
    private String destId;
    private String blockchainPrivateKey;

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getBeneficiaryIban() {
        return beneficiaryIban;
    }

    public void setBeneficiaryIban(String beneficiaryIban) {
        this.beneficiaryIban = beneficiaryIban;
    }

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public String getDestId() { return destId; }
    public void setDestId(String destId) { this.destId = destId; }

    public String getBlockchainPrivateKey() {
        return blockchainPrivateKey;
    }

    public void setBlockchainPrivateKey(String blockchainPrivateKey) {
        this.blockchainPrivateKey = blockchainPrivateKey;
    }

}
