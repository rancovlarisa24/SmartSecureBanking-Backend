package com.lrs.SSB.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users_insurances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInsurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "insurance_id", nullable = false)
    private Long insuranceId;

    @Column(name = "card_id", nullable = false)
    private Integer cardId;

    @Lob
    @Column(name = "extra_details")
    private String extraDetails;

    @Column(name = "purchased_at", nullable = false, updatable = false)
    private LocalDateTime purchasedAt;

    @PrePersist
    public void onCreate() {
        this.purchasedAt = LocalDateTime.now();
    }
}

