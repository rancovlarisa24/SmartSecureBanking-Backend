package com.lrs.SSB.controller;

import java.math.BigDecimal;

public class VirtualCardDTO {
    private Integer userId;
    private Integer sourceCardId;
    private BigDecimal transactionLimit;
    private String personalizedName;
    private Integer selectedCardId;
    public VirtualCardDTO() {}

    public VirtualCardDTO(Integer userId, Integer sourceCardId, BigDecimal transactionLimit, String personalizedName, Integer selectedCardId) {
        this.userId = userId;
        this.sourceCardId = sourceCardId;
        this.transactionLimit = transactionLimit;
        this.personalizedName = personalizedName;
        this.selectedCardId = selectedCardId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getSourceCardId() {
        return sourceCardId;
    }

    public void setSourceCardId(Integer sourceCardId) {
        this.sourceCardId = sourceCardId;
    }

    public BigDecimal getTransactionLimit() {
        return transactionLimit;
    }

    public void setTransactionLimit(BigDecimal transactionLimit) {
        this.transactionLimit = transactionLimit;
    }

    public String getPersonalizedName() {
        return personalizedName;
    }

    public void setPersonalizedName(String personalizedName) {
        this.personalizedName = personalizedName;
    }

    public Integer getSelectedCardId() {
        return selectedCardId;
    }
    public void setSelectedCardId(Integer selectedCardId) {
        this.selectedCardId = selectedCardId;
    }
}
