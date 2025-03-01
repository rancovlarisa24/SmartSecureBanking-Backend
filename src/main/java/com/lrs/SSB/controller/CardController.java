package com.lrs.SSB.controller;

import com.lrs.SSB.entity.Card;
import com.lrs.SSB.entity.User;
import com.lrs.SSB.service.CardService;
import com.lrs.SSB.service.PayPalService;
import com.lrs.SSB.service.UserService;
import com.lrs.SSB.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/card")
public class CardController {

    @Autowired
    private PayPalService PayPalService;

    @Autowired
    private CardService cardService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/validate")
    public ResponseEntity<?> validateCard(@RequestBody CardDto cardDto) {
        boolean isValid = PayPalService.validateCard(cardDto);
        if (isValid) {
            return ResponseEntity.ok("Card is valid in PayPal.");
        } else {
            return ResponseEntity.badRequest().body("Invalid card or error from PayPal.");
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCard(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody CardDto cardDto
    ) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Invalid or missing token.");
        }

        String jwtToken = token.substring(7).trim();
        String userContact;
        try {
            if (!jwtUtil.validateToken(jwtToken)) {
                return ResponseEntity.status(401).body("Token is invalid or expired.");
            }
            userContact = jwtUtil.extractContact(jwtToken);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token: " + e.getMessage());
        }

        Optional<User> userOpt = userService.findByContact(userContact);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        User user = userOpt.get();

        Optional<Card> existingCard = cardService.findByCardNumber(cardDto.getCardNumber());
        if (existingCard.isPresent()) {
            return ResponseEntity.badRequest().body("A card with this number already exists.");
        }

        Card card = createCard(cardDto, user);
        cardService.saveCard(card);

        return ResponseEntity.ok("Card added successfully.");
    }

    private Card createCard(CardDto cardDto, User user) {
        Card card = new Card();
        card.setUser(user);
        card.setCardholderName(cardDto.getCardholderName());
        card.setCardNumber(cardDto.getCardNumber());
        card.setIban(cardDto.getIban());
        card.setExpiryDate(cardDto.getExpiryDate());
        card.setCvv(cardDto.getCvv());
        card.setBalance(cardDto.getBalance());
        card.setPersonalizedName(cardDto.getPersonalizedName());
        card.setBankIssuer(cardDto.getBankIssuer());
        card.setCardCurrency(cardDto.getCardCurrency());
        return card;
    }

    @GetMapping("/user-cards")
    public ResponseEntity<?> getUserCards(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Invalid or missing token.");
        }

        String jwtToken = token.substring(7).trim();
        String userContact;
        try {
            if (!jwtUtil.validateToken(jwtToken)) {
                return ResponseEntity.status(401).body("Token is invalid or expired.");
            }
            userContact = jwtUtil.extractContact(jwtToken);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token: " + e.getMessage());
        }

        Optional<User> userOpt = userService.findByContact(userContact);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        User user = userOpt.get();
        List<Card> userCards = cardService.findCardsByUser(user);

        if (userCards.isEmpty()) {
            return ResponseEntity.ok(userCards);
        }


        return ResponseEntity.ok(userCards);
    }


}

