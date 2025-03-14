package com.lrs.SSB.controller;

import com.lrs.SSB.controller.TransactionRequest;
import com.lrs.SSB.entity.Transaction;
import com.lrs.SSB.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @PostMapping("/save")
    public ResponseEntity<String> saveTransaction(@RequestBody TransactionRequest request) {
        transactionService.saveTransaction(
                request.getCardId(),
                request.getAmount(),
                request.getTransactionType(),
                request.getBeneficiaryName(),
                request.getBeneficiaryIban()
        );
        return ResponseEntity.ok("Transaction saved successfully");
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionsByUser(@PathVariable Long userId) {
        List<Transaction> transactions = transactionService.getTransactionsByUser(userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<Transaction>> getAllTransactionsForCard(@PathVariable Long cardId) {
        List<Transaction> transactions = transactionService.getAllTransactionsForCard(cardId);
        return ResponseEntity.ok(transactions);
    }
}
