package com.lrs.SSB.service;

import com.lrs.SSB.entity.Transaction;
import com.lrs.SSB.entity.TransactionType;
import com.lrs.SSB.entity.Card;
import com.lrs.SSB.entity.User;
import com.lrs.SSB.repository.TransactionRepository;
import com.lrs.SSB.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.lrs.SSB.controller.TransactionSummary;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardService cardService;

    public void saveTransaction(Long cardId, BigDecimal amount, TransactionType type, String beneficiaryName, String beneficiaryIban) {
        Card fromCard = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        Transaction transaction = new Transaction();
        transaction.setCard(fromCard);
        transaction.setAmount(amount);
        transaction.setTransactionType(type);
        transaction.setBeneficiaryName(beneficiaryName);
        transaction.setBeneficiaryIban(beneficiaryIban);

        if (beneficiaryIban != null && !beneficiaryIban.trim().isEmpty()) {
            Optional<Card> toCardOpt = cardRepository.findByIban(beneficiaryIban);
            if (!toCardOpt.isPresent()) {
                throw new RuntimeException("Beneficiary card not found for IBAN: " + beneficiaryIban);
            }
            Card toCard = toCardOpt.get();
            transaction.setFromCard(fromCard);
            transaction.setToCard(toCard);
        }

        transactionRepository.save(transaction);
    }



    public List<Transaction> getTransactionsByCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        return transactionRepository.findByCard(card);
    }

    public List<Transaction> getTransactionsByUser(Long userId) {
        return transactionRepository.findByCard_UserId(userId);
    }
    public List<Transaction> getAllTransactionsForCard(Long cardId) {
        return transactionRepository.findAllByCardId(cardId);
    }

}
