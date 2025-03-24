package com.lrs.SSB.service;
import com.lrs.SSB.entity.Card;
import com.lrs.SSB.entity.RecurringPayment;
import com.lrs.SSB.entity.TransactionType;
import com.lrs.SSB.repository.RecurringPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RecurringPaymentService {

    private final RecurringPaymentRepository repository;

    @Autowired
    private CardService cardService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BlockchainService blockchainService;

    @Autowired
    public RecurringPaymentService(RecurringPaymentRepository repository) {
        this.repository = repository;
    }

    public RecurringPayment save(RecurringPayment payment) {
        return repository.save(payment);
    }

    public List<RecurringPayment> getByUsername(String username) {
        return repository.findByUsername(username);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public List<RecurringPayment> getAll() {
        return repository.findAll();
    }

    public Optional<RecurringPayment> findById(Long id) {
        return repository.findById(id);
    }
    public void executeRecurringPayment(RecurringPayment payment) throws Exception {

        if (payment.getStatus() == 0) {
            System.out.println("Plata recurentă este oprită; transferul nu se va efectua.");
            return;
        }

        Long cardId = Long.parseLong(payment.getCardId());
        Optional<Card> fromCardOpt = cardService.findById(cardId);
        if (fromCardOpt.isEmpty()) {
            throw new Exception("Cardul sursă nu a fost găsit pentru plata cu id " + payment.getId());
        }
        Card fromCard = fromCardOpt.get();

        double amount = payment.getAmount();
        if (fromCard.getBalance().doubleValue() < amount) {
            throw new Exception("Fonduri insuficiente pe cardul sursă pentru plata cu id " + payment.getId());
        }

        String blockchainPrivateKey = payment.getBlockchainPrivateKey();
        if (blockchainPrivateKey == null || blockchainPrivateKey.isEmpty()) {
            throw new Exception("Blockchain key not provided for payment id " + payment.getId());
        }
        Optional<Card> toCardOpt = cardService.findByIban(payment.getIban());
        if (toCardOpt.isEmpty()) {
            throw new Exception("Cardul destinatar nu a fost găsit pentru plata cu id " + payment.getId());
        }
        Card toCard = toCardOpt.get();
        String sourceId = fromCard.getId().toString();
        String destId = toCard.getId().toString();
        BigInteger amountBI = BigInteger.valueOf((long) amount);
        boolean validated = blockchainService.validateTransferOnBlockchain(sourceId, destId, amountBI, blockchainPrivateKey);
        if (!validated) {
            throw new Exception("Validarea pe blockchain a eșuat pentru plata cu id " + payment.getId());
        }
        fromCard.setBalance(fromCard.getBalance().subtract(BigDecimal.valueOf(amount)));
        toCard.setBalance(toCard.getBalance().add(BigDecimal.valueOf(amount)));
        cardService.saveCard(fromCard);
        cardService.saveCard(toCard);
        transactionService.saveTransaction(
                cardId,
                BigDecimal.valueOf(amount),
                TransactionType.valueOf("RECURRENT"),
                payment.getServiceName(),
                payment.getIban()
        );
    }

    public LocalDate calculateNextExecutionDate(LocalDate currentDate, String frequency) {
        switch (frequency.toLowerCase()) {
            case "daily":
                return currentDate.plusDays(1);
            case "weekly":
                return currentDate.plusWeeks(1);
            case "monthly":
                return currentDate.plusMonths(1);
            case "annually":
                return currentDate.plusYears(1);
            default:
                throw new IllegalArgumentException("Frecvență necunoscută: " + frequency);
        }
    }
}
