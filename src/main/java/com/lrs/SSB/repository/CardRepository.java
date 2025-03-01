package com.lrs.SSB.repository;

import com.lrs.SSB.entity.Card;
import com.lrs.SSB.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByCardNumber(String cardNumber);
    List<Card> findByUser(User user);
}
