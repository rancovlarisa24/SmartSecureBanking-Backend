package com.lrs.SSB.repository;

import com.lrs.SSB.entity.Transaction;
import com.lrs.SSB.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCard(Card card);
    List<Transaction> findByCard_UserId(Long userId);

    @Query("SELECT t FROM Transaction t WHERE t.fromCard.id = :cardId OR t.toCard.id = :cardId")
    List<Transaction> findAllByCardId(@Param("cardId") Long cardId);
}
