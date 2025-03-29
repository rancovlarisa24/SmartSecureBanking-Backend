package com.lrs.SSB.controller;

import com.lrs.SSB.entity.Card;
import com.lrs.SSB.entity.User;
import com.lrs.SSB.entity.Utility;
import com.lrs.SSB.service.CardService;
import com.lrs.SSB.service.PayPalService;
import com.lrs.SSB.service.UserService;
import com.lrs.SSB.service.UtilityService;
import com.lrs.SSB.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    private UtilityService utilityService;

    @Autowired
    private JwtUtil jwtUtil;
    private Utility utility;

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
        card.setActive(true);
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

    @GetMapping("/total-balance")
    public ResponseEntity<?> getTotalCardBalance(@RequestHeader(value = "Authorization", required = false) String token) {
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
        double totalBalance = userCards.stream()
                .mapToDouble(card -> card.getBalance().doubleValue())
                .sum();

        return ResponseEntity.ok(Map.of("totalBalance", totalBalance));
    }

    @PutMapping("/update-name")
    public ResponseEntity<?> updateCardName(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, String> request
    ) {

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Invalid or missing token.");
        }
        String jwtToken = token.substring(7).trim();
        if (!jwtUtil.validateToken(jwtToken)) {
            return ResponseEntity.status(401).body("Token is invalid or expired.");
        }
        String userContact = jwtUtil.extractContact(jwtToken);
        User user = userService.findByContact(userContact).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found.");
        }
        String cardIdStr = request.get("cardId");
        String newPersonalizedName = request.get("newPersonalizedName");
        if (cardIdStr == null || newPersonalizedName == null || newPersonalizedName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid input data.");
        }

        Long cardId;
        try {
            cardId = Long.parseLong(cardIdStr);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid card ID.");
        }
        Card card = cardService.findById(cardId).orElse(null);
        if (card == null) {
            return ResponseEntity.badRequest().body("Card not found.");
        }


        if (!card.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("You do not have permission to modify this card.");
        }

        card.setPersonalizedName(newPersonalizedName);
        cardService.saveCard(card);

        return ResponseEntity.ok(Map.of("message", "Card name updated successfully."));

    }

    @DeleteMapping("/delete/{cardId}")
    public ResponseEntity<?> deleteCard(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long cardId) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Invalid or missing token.");
        }

        String jwtToken = token.substring(7).trim();
        if (!jwtUtil.validateToken(jwtToken)) {
            return ResponseEntity.status(401).body("Token is invalid or expired.");
        }

        String userContact = jwtUtil.extractContact(jwtToken);
        User user = userService.findByContact(userContact).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        Card card = cardService.findById(cardId).orElse(null);
        if (card == null) {
            return ResponseEntity.badRequest().body("Card not found.");
        }
        if (!card.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("You do not have permission to delete this card.");
        }
        cardService.deleteCard(cardId);
        return ResponseEntity.ok(Map.of("message", "Card deleted successfully."));
    }

    @PutMapping("/block/{cardId}")
    public ResponseEntity<?> blockCard(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long cardId
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

        Optional<Card> cardOpt = cardService.findById(cardId);
        if (cardOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Card not found.");
        }
        Card card = cardOpt.get();

        if (!card.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("You do not have permission to modify this card.");
        }
        card.setActive(false);
        cardService.saveCard(card);

        return ResponseEntity.ok(Map.of("message", "Card blocked successfully."));
    }

    @PutMapping("/unblock/{cardId}")
    public ResponseEntity<?> unblockCard(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long cardId
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

        Optional<Card> cardOpt = cardService.findById(cardId);
        if (cardOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Card not found.");
        }
        Card card = cardOpt.get();

        if (!card.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("You do not have permission to modify this card.");
        }
        card.setActive(true);
        cardService.saveCard(card);

        return ResponseEntity.ok(Map.of("message", "Card unblocked successfully."));
    }

    @GetMapping("/status/{cardId}")
    public ResponseEntity<?> getCardById(@PathVariable Long cardId,
                                         @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Invalid or missing token.");
        }
        String jwtToken = token.substring(7).trim();
        if (!jwtUtil.validateToken(jwtToken)) {
            return ResponseEntity.status(401).body("Token is invalid or expired.");
        }

        Optional<Card> cardOpt = cardService.findById(cardId);
        if (cardOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Card not found.");
        }
        return ResponseEntity.ok(cardOpt.get());
    }

    @PostMapping("/verify-beneficiary")
    public ResponseEntity<?> verifyBeneficiary(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, String> payload
    ) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Invalid or missing token.");
        }
        String jwtToken = token.substring(7).trim();
        if (!jwtUtil.validateToken(jwtToken)) {
            return ResponseEntity.status(401).body("Token is invalid or expired.");
        }

        String beneficiaryName = payload.get("beneficiaryName");
        String iban = payload.get("iban");

        if (beneficiaryName == null || beneficiaryName.trim().isEmpty() ||
                iban == null || iban.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid beneficiary data.");
        }


        Optional<User> maybeUser = userService.findByNumeComplet(beneficiaryName);
        if (maybeUser.isEmpty()) {
            return ResponseEntity.badRequest().body("No user with that name.");
        }

        User user = maybeUser.get();
        List<Card> cards = cardService.findCardsByUser(user);
        boolean foundMatchingCard = cards.stream()
                .anyMatch(c -> c.getIban().equalsIgnoreCase(iban));

        if (!foundMatchingCard) {
            return ResponseEntity.badRequest().body("User has no card with the provided IBAN.");
        }

        return ResponseEntity.ok("Beneficiary verified.");
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferAmount(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, String> payload
    ) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Invalid or missing token.");
        }
        String jwtToken = token.substring(7).trim();
        if (!jwtUtil.validateToken(jwtToken)) {
            return ResponseEntity.status(401).body("Token is invalid or expired.");
        }

        String userContact = jwtUtil.extractContact(jwtToken);
        Optional<User> userOpt = userService.findByContact(userContact);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found.");
        }
        User user = userOpt.get();

        String fromCardIdStr = payload.get("fromCardId");
        String toCardIdStr = payload.get("toCardId");
        String amountStr = payload.get("amount");

        String beneficiaryName = payload.get("beneficiaryName");
        String iban = payload.get("iban");

        if (fromCardIdStr == null || fromCardIdStr.isEmpty() ||
                amountStr == null || amountStr.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing transfer details (fromCardId / amount).");
        }
        double amount;
        long fromCardId, toCardId = -1;
        try {
            fromCardId = Long.parseLong(fromCardIdStr);
            amount = Double.parseDouble(amountStr);
            if (toCardIdStr != null && !toCardIdStr.isEmpty()) {
                toCardId = Long.parseLong(toCardIdStr);
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid number format.");
        }
        if (amount <= 0) {
            return ResponseEntity.badRequest().body("Amount must be > 0.");
        }
        Optional<Card> fromCardOpt = cardService.findById(fromCardId);
        if (fromCardOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Source card not found.");
        }
        Card fromCard = fromCardOpt.get();
        if (!fromCard.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("You do not own the source card.");
        }

        if (fromCard.getBalance().doubleValue() < amount) {
            return ResponseEntity.badRequest().body("Insufficient balance in source card.");
        }

        if (toCardId != -1) {
            Optional<Card> toCardOpt = cardService.findById(toCardId);
            if (toCardOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Destination card not found.");
            }
            Card toCard = toCardOpt.get();
            if (!toCard.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().body("Destination card does not belong to you.");
            }
            fromCard.setBalance(fromCard.getBalance().subtract(BigDecimal.valueOf(amount)));
            toCard.setBalance(toCard.getBalance().add(BigDecimal.valueOf(amount)));

            cardService.saveCard(fromCard);
            cardService.saveCard(toCard);

        } else {
            if (beneficiaryName == null || beneficiaryName.trim().isEmpty()
                    || iban == null || iban.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Missing beneficiary details for external transfer.");
            }

            Optional<User> beneficiaryUserOpt = userService.findByNumeComplet(beneficiaryName);
            if (beneficiaryUserOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("No user with that name.");
            }
            User beneficiaryUser = beneficiaryUserOpt.get();
            List<Card> beneficiaryCards = cardService.findCardsByUser(beneficiaryUser);
            Optional<Card> destinationOpt = beneficiaryCards.stream()
                    .filter(c -> c.getIban().equalsIgnoreCase(iban))
                    .findFirst();

            if (destinationOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Beneficiary user has no card with the provided IBAN.");
            }
            Card destinationCard = destinationOpt.get();
            fromCard.setBalance(fromCard.getBalance().subtract(BigDecimal.valueOf(amount)));
            destinationCard.setBalance(destinationCard.getBalance().add(BigDecimal.valueOf(amount)));

            cardService.saveCard(fromCard);
            cardService.saveCard(destinationCard);
        }

        if (user.isSavingsActive()) {
            int multiple = user.getRoundingMultiple();
            if (multiple > 0) {
                BigDecimal bdAmount = BigDecimal.valueOf(amount);
                BigDecimal bdMultiple = BigDecimal.valueOf(multiple);
                BigDecimal remainder = bdAmount.remainder(bdMultiple);
                BigDecimal differenceToSave = BigDecimal.ZERO;
                if (remainder.compareTo(BigDecimal.ZERO) != 0) {
                    differenceToSave = bdMultiple.subtract(remainder);
                }

                if (differenceToSave.compareTo(BigDecimal.ZERO) > 0 &&
                        fromCard.getBalance().compareTo(differenceToSave) >= 0)
                {
                    Card savingsCard = findSavingsCard(user);
                    fromCard.setBalance(fromCard.getBalance().subtract(differenceToSave));
                    savingsCard.setBalance(savingsCard.getBalance().add(differenceToSave));

                    cardService.saveCard(fromCard);
                    cardService.saveCard(savingsCard);

                }
            }
        }

        return ResponseEntity.ok("Transfer completed successfully.");
    }

    private Card findSavingsCard(User user) {
        List<Card> userCards = cardService.findCardsByUser(user);
        return userCards.stream()
                .filter(c -> "Savings".equalsIgnoreCase(c.getPersonalizedName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No Savings card found for user: " + user.getId()));
    }


    @PostMapping("/find-by-iban")
    public ResponseEntity<?> findCardIdByIban(@RequestBody Map<String, String> request) {
        String iban = request.get("iban");
        Optional<Card> card = cardService.findByIban(iban);

        if (card.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User or service not found."));
        }
        Card cardId = card.get();
        return ResponseEntity.ok(Collections.singletonMap("cardId", cardId));
    }

    @PostMapping("/pay-bill")
    public ResponseEntity<?> payBill(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, String> payload
    ) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Invalid or missing token.");
            }
            String jwtToken = token.substring(7).trim();
            if (!jwtUtil.validateToken(jwtToken)) {
                return ResponseEntity.status(401).body("Token is invalid or expired.");
            }

            String userContact = jwtUtil.extractContact(jwtToken);
            Optional<User> userOpt = userService.findByContact(userContact);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found.");
            }
            User user = userOpt.get();
            String fromCardIdStr = payload.get("fromCardId");
            String providerName   = payload.get("providerName");
            String providerIban   = payload.get("providerIban");
            String amountStr      = payload.get("amount");

            if (fromCardIdStr == null || fromCardIdStr.isEmpty() ||
                    providerName   == null || providerName.trim().isEmpty() ||
                    amountStr      == null || amountStr.isEmpty()
            ) {
                return ResponseEntity.badRequest().body("Missing bill payment details.");
            }

            double amount;
            long fromCardId;
            try {
                fromCardId = Long.parseLong(fromCardIdStr);
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Invalid format for fromCardId or amount.");
            }
            if (amount <= 0) {
                return ResponseEntity.badRequest().body("Amount must be > 0.");
            }

            Optional<Card> fromCardOpt = cardService.findById(fromCardId);
            if (fromCardOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Source card not found.");
            }
            Card fromCard = fromCardOpt.get();
            if (!fromCard.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("You do not own the source card.");
            }
            if (fromCard.getBalance().doubleValue() < amount) {
                return ResponseEntity.badRequest().body("Insufficient balance on source card.");
            }

            Optional<Utility> maybeService = utilityService.getAllUtilities().stream()
                    .filter(u -> u.getServiceName().equalsIgnoreCase(providerName))
                    .findFirst();

            if (maybeService.isEmpty()) {
                return ResponseEntity.badRequest().body("Provider not found in the system.");
            }
            Utility service = maybeService.get();
            if (providerIban != null && !providerIban.isEmpty()) {
                if (!service.getIban().equalsIgnoreCase(providerIban)) {
                    return ResponseEntity.badRequest().body("Provided IBAN does not match the chosen provider's IBAN.");
                }
            }
            Long providerCardId = service.getDestinationCardId();
            if (providerCardId == null) {
                return ResponseEntity.badRequest().body("Provider has no destination card ID set.");
            }

            Optional<Card> providerCardOpt = cardService.findById(providerCardId);
            if (providerCardOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Destination card for this provider not found.");
            }
            Card providerCard = providerCardOpt.get();

            fromCard.setBalance(fromCard.getBalance().subtract(BigDecimal.valueOf(amount)));
            providerCard.setBalance(providerCard.getBalance().add(BigDecimal.valueOf(amount)));

            cardService.saveCard(fromCard);
            cardService.saveCard(providerCard);

            if (user.isSavingsActive()) {
                int multiple = user.getRoundingMultiple();
                if (multiple > 0) {
                    BigDecimal bdAmount = BigDecimal.valueOf(amount);
                    BigDecimal bdMultiple = BigDecimal.valueOf(multiple);

                    BigDecimal remainder = bdAmount.remainder(bdMultiple);
                    BigDecimal differenceToSave = BigDecimal.ZERO;
                    if (remainder.compareTo(BigDecimal.ZERO) != 0) {
                        differenceToSave = bdMultiple.subtract(remainder);
                    }
                    if (differenceToSave.compareTo(BigDecimal.ZERO) > 0 &&
                            fromCard.getBalance().compareTo(differenceToSave) >= 0)
                    {

                        Card savingsCard = findSavingsCard(user);
                        fromCard.setBalance(
                                fromCard.getBalance().subtract(differenceToSave)
                        );
                        savingsCard.setBalance(
                                savingsCard.getBalance().add(differenceToSave)
                        );

                        cardService.saveCard(fromCard);
                        cardService.saveCard(savingsCard);
                    }
                }
            }
            return ResponseEntity.ok("Bill payment completed successfully.");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/savings-total")
    public ResponseEntity<?> getSavingsTotal(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid token");
        }
        String jwt = token.substring(7).trim();
        if (!jwtUtil.validateToken(jwt)) {
            return ResponseEntity.status(401).body("Invalid token");
        }

        String contact = jwtUtil.extractContact(jwt);
        User user = userService.findByContact(contact).orElse(null);
        if (user == null) return ResponseEntity.status(404).body("User not found");

        Card savingsCard = cardService.findCardsByUser(user).stream()
                .filter(c -> "Savings".equalsIgnoreCase(c.getPersonalizedName()))
                .findFirst()
                .orElse(null);

        if (savingsCard == null) return ResponseEntity.status(404).body("Savings card not found");

        return ResponseEntity.ok(Map.of("totalSaved", savingsCard.getBalance()));
    }



}

