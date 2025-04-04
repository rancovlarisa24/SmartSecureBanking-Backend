package com.lrs.SSB.service;

import com.lrs.SSB.controller.VirtualCardDTO;
import com.lrs.SSB.entity.Card;
import com.lrs.SSB.entity.User;
import com.lrs.SSB.entity.VirtualCard;
import com.lrs.SSB.repository.VirtualCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class VirtualCardService {

    @Autowired
    private VirtualCardRepository virtualCardRepository;

    @Autowired
    private CardService cardService;

    @Autowired
    private UserService userService;

    public VirtualCard createVirtualCard(VirtualCardDTO dto) {
        Optional<User> userOpt = userService.findUserById(dto.getUserId().longValue());
        String cardholderName = userOpt.map(User::getNumeComplet).orElse("Unknown");
        Card card = new Card();
        card.setUser(userOpt.orElse(null));
        card.setCardholderName(cardholderName);
        card.setCardNumber(generateCardNumber());
        card.setIban(generateIban());
        card.setExpiryDate(generateExpiryDate());
        card.setCvv(generateCvv());
        card.setBalance(dto.getTransactionLimit());
        card.setPersonalizedName(dto.getPersonalizedName());
        card.setBankIssuer("Virtual SSB");
        card.setCardCurrency("USD");
        card.setActive(true);
        cardService.saveCard(card);
        Integer savedCardId = card.getId();
        VirtualCard virtualCard = new VirtualCard();
        virtualCard.setUserId(dto.getUserId());
        virtualCard.setSourceCardId(savedCardId);
        virtualCard.setTransactionLimit(dto.getTransactionLimit());
        virtualCard.setSelectedCardId(dto.getSelectedCardId());
        VirtualCard savedVirtualCard = virtualCardRepository.save(virtualCard);

        return savedVirtualCard;
    }


    private String generateCardNumber() {
        long number = (long)(Math.random() * 1_0000_0000_0000_0000L);
        return String.format("%016d", number);
    }

    private String generateIban() {
        long randomPart = (long)(Math.random() * 1_000_000_000L);
        return "RO99SSB" + String.format("%09d", randomPart);
    }

    private String generateExpiryDate() {
        return "12/30";
    }

    private String generateCvv() {
        int cvvNum = (int)(Math.random() * 900) + 100;
        return String.valueOf(cvvNum);
    }

    public void deleteVirtualCardBySourceCardId(Integer sourceCardId) {
        virtualCardRepository.deleteBySourceCardId(sourceCardId);
    }

    public Optional<Card> getUnderlyingCard(Long virtualCardId) {
        Optional<VirtualCard> virtualCardOpt = virtualCardRepository.findBySourceCardId(virtualCardId.intValue());
        if (virtualCardOpt.isPresent()) {
            VirtualCard virtualCard = virtualCardOpt.get();
            Long underlyingCardId = Long.valueOf(virtualCard.getSelectedCardId());
            return cardService.findById(underlyingCardId);
        }
        return Optional.empty();
    }



}

