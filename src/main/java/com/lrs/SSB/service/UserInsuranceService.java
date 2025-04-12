package com.lrs.SSB.service;

import com.lrs.SSB.controller.UserInsuranceDTO;
import com.lrs.SSB.entity.Card;
import com.lrs.SSB.entity.Insurance;
import com.lrs.SSB.entity.User;
import com.lrs.SSB.entity.UserInsurance;
import com.lrs.SSB.repository.UserInsuranceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
@Service
public class UserInsuranceService {

    private final UserInsuranceRepository repository;
    private final CardService cardService;
    private final InsuranceService insuranceService;
    private final VirtualCardService virtualCardService;
    private final UserService userService;

    public UserInsuranceService(UserInsuranceRepository repository,
                                CardService cardService,
                                InsuranceService insuranceService,
                                VirtualCardService virtualCardService,
                                UserService userService) {
        this.repository = repository;
        this.cardService = cardService;
        this.insuranceService = insuranceService;
        this.virtualCardService = virtualCardService;
        this.userService = userService;
    }

    public UserInsurance save(UserInsuranceDTO dto) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

        String normalizedDetails;
        try {
            ObjectNode jsonNode = (ObjectNode) mapper.readTree(dto.getExtraDetails());
            normalizedDetails = mapper.writeValueAsString(jsonNode);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid extraDetails JSON", e);
        }

        boolean alreadyExists = repository.existsByUserIdAndInsuranceIdAndExtraDetails(
                dto.getUserId(),
                dto.getInsuranceId(),
                normalizedDetails
        );
        if (alreadyExists) {
            throw new IllegalStateException("Insurance already purchased with the same details.");
        }

        Card selectedCard = cardService.findById((long) dto.getCardId())
                .orElseThrow(() -> new IllegalArgumentException("Card not found."));

        Long dtoUserId = dto.getUserId() != null ? dto.getUserId().longValue() : null;
        Long cardUserId = Long.valueOf(selectedCard.getUser().getId());

        if (!cardUserId.equals(dtoUserId)) {
            throw new SecurityException("User does not own the selected card.");
        }


        if (!selectedCard.isActive()) {
            throw new IllegalStateException("Selected card is blocked/inactive.");
        }

        Card effectiveSourceCard = selectedCard;
        boolean isVirtual = selectedCard.getBankIssuer() != null
                && selectedCard.getBankIssuer().equalsIgnoreCase("Virtual SSB");

        if (isVirtual) {
            Optional<Card> underlyingCardOpt = virtualCardService.getUnderlyingCard((long) selectedCard.getId());

            if (underlyingCardOpt.isEmpty()) {
                throw new IllegalStateException("Underlying funding card not found for this virtual card.");
            }
            effectiveSourceCard = underlyingCardOpt.get();

            if (!effectiveSourceCard.isActive()) {
                throw new IllegalStateException("Underlying (funding) card is blocked/inactive.");
            }
        }

        Insurance insurance = insuranceService.findById(dto.getInsuranceId())
                .orElseThrow(() -> new IllegalArgumentException("Insurance not found."));

        String priceStr = insurance.getPrice().replaceAll("[^\\d.]", "");
        double price = Double.parseDouble(priceStr);

        if (effectiveSourceCard.getBalance().doubleValue() < price) {
            throw new IllegalStateException("Insufficient balance on card.");
        }

        effectiveSourceCard.setBalance(
                effectiveSourceCard.getBalance().subtract(BigDecimal.valueOf(price))
        );
        cardService.saveCard(effectiveSourceCard);


        Optional<User> userOpt = userService.findUserById(dto.getUserId().longValue());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.isSavingsActive()) {
                int multiple = user.getRoundingMultiple();
                if (multiple > 0) {
                    BigDecimal bdPrice = BigDecimal.valueOf(price);
                    BigDecimal bdMultiple = BigDecimal.valueOf(multiple);

                    BigDecimal remainder = bdPrice.remainder(bdMultiple);
                    BigDecimal differenceToSave = BigDecimal.ZERO;

                    if (remainder.compareTo(BigDecimal.ZERO) != 0) {
                        differenceToSave = bdMultiple.subtract(remainder);
                    }

                    if (differenceToSave.compareTo(BigDecimal.ZERO) > 0 &&
                            effectiveSourceCard.getBalance().compareTo(differenceToSave) >= 0) {

                        Card savingsCard = findSavingsCard(user);
                        effectiveSourceCard.setBalance(
                                effectiveSourceCard.getBalance().subtract(differenceToSave)
                        );
                        savingsCard.setBalance(
                                savingsCard.getBalance().add(differenceToSave)
                        );

                        cardService.saveCard(effectiveSourceCard);
                        cardService.saveCard(savingsCard);
                    }
                }
            }
        }

        UserInsurance insuranceToSave = UserInsurance.builder()
                .userId(dto.getUserId())
                .insuranceId(dto.getInsuranceId())
                .cardId(dto.getCardId())
                .extraDetails(dto.getExtraDetails())
                .build();

        return repository.save(insuranceToSave);
    }

    private Card findSavingsCard(User user) {
        List<Card> userCards = cardService.findCardsByUser(user);
        return userCards.stream()
                .filter(c -> "Savings".equalsIgnoreCase(c.getPersonalizedName()))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("No Savings card found for user: " + user.getId())
                );
    }

    public List<UserInsurance> getAllForUser(Integer userId) {
        return repository.findByUserId(userId);
    }
}
