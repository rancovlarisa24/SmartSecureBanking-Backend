package com.lrs.SSB.service;

import com.lrs.SSB.entity.Card;
import com.lrs.SSB.entity.User;
import com.lrs.SSB.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    public Optional<Card> findByCardNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber);
    }

    public void saveCard(Card card) {
        cardRepository.save(card);
    }

    public List<Card> findCardsByUser(User user) {
        return cardRepository.findByUser(user);
    }

    public Optional<Card> findById(Long cardId) {
        return cardRepository.findById(cardId);
    }

    public void deleteCard(Long cardId) {
        cardRepository.deleteById(cardId);
    }

    public Optional<Card> findByIban(String iban) {
        return cardRepository.findByIban(iban);
    }
    public List<Card> findCardsByUserId(Long userId) {
        return cardRepository.findByUser_Id(userId);
    }

    public Optional<Card> findByUserAndCurrency(User user, String currency) {
        return cardRepository.findByUserAndCardCurrency(user, currency);
    }
}
