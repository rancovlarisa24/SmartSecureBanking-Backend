package com.lrs.SSB.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "virtual_cards")
public class VirtualCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "source_card_id", nullable = false)
    private Integer sourceCardId;

    @Column(name = "transaction_limit", nullable = false)
    private BigDecimal transactionLimit;

    @Column(name = "selected_card_id", nullable = false)
    private Integer selectedCardId;

    public VirtualCard() {}

    public VirtualCard(Integer userId, Integer sourceCardId, BigDecimal transactionLimit, Integer selectedCardId) {
        this.userId = userId;
        this.sourceCardId = sourceCardId;
        this.transactionLimit = transactionLimit;
        this.selectedCardId = selectedCardId;
    }

    public Integer getId() {
        return id;
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

    public Integer getSelectedCardId() {
        return selectedCardId;
    }
    public void setSelectedCardId(Integer selectedCardId) {
        this.selectedCardId = selectedCardId;
    }
}