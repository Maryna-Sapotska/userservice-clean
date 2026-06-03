package com.innowise.userservice.security;

import com.innowise.userservice.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("cardSecurity")
@RequiredArgsConstructor
public class CardSecurity {

    private final CardRepository cardRepository;

    public boolean isOwner(Long cardId, Authentication authentication) {

        String userId = (String) authentication.getPrincipal();

        return cardRepository.findById(cardId)
                .map(card -> card.getUser().getId().toString().equals(userId))
                .orElse(false);
    }
}
