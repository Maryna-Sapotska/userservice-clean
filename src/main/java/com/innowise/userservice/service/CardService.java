package com.innowise.userservice.service;

import com.innowise.userservice.exception.BusinessException;
import com.innowise.userservice.exception.CardNotFoundException;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.mapper.CardMapper;
import com.innowise.userservice.model.dto.CardDTO;
import com.innowise.userservice.model.dto.CreateCardDto;
import com.innowise.userservice.model.dto.UpdateCardDto;
import com.innowise.userservice.model.entity.CacheNames;
import com.innowise.userservice.model.entity.Card;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.CardRepository;
import com.innowise.userservice.repository.CardSpecification;
import com.innowise.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    private static final int MAX_CARDS = 5;

    private static final String MAX_CARDS_ALLOWED = "Max 5 cards allowed";
    private static final String USER_NOT_FOUND = "User not found";
    private static final String CARD_NOT_FOUND = "Card not found";

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheNames.USERS_WITH_CARDS, key = "#dto.userId"),
            @CacheEvict(value = CacheNames.USERS, key = "#dto.userId"),
            @CacheEvict(value = CacheNames.CARDS, allEntries = true)
    })
    public CardDTO create(CreateCardDto dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        long cardsCount = cardRepository.countUserCards(dto.getUserId());

        if (cardsCount >= MAX_CARDS) {
            throw new BusinessException(MAX_CARDS_ALLOWED);
        }

        Card card = cardMapper.toEntity(dto);

        if (card == null) {
            throw new BusinessException("Card mapping failed");
        }

        card.setUser(user);
        card.setActive(true);

        Card savedCard = cardRepository.save(card);

        return cardMapper.toDto(savedCard);
    }

    @Cacheable(value = CacheNames.CARDS, key = "#id")
    public CardDTO getById(Long id) {
        return cardMapper.toDto(cardRepository.findById(id)
                .orElseThrow(() ->
                        new CardNotFoundException(CARD_NOT_FOUND)));
    }

    public List<CardDTO> getByUserId(Long userId) {
        return cardMapper.toDtoList(cardRepository
                .findByUser_Id(userId));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheNames.USERS_WITH_CARDS, allEntries = true),
            @CacheEvict(value = CacheNames.CARDS, key = "#id")
    })
    public CardDTO update(Long id, UpdateCardDto dto) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() ->
                        new CardNotFoundException(CARD_NOT_FOUND));

        if (dto.getNumber() != null) {
            card.setNumber(dto.getNumber());
        }

        if (dto.getHolder() != null) {
            card.setHolder(dto.getHolder());
        }

        if (dto.getExpirationDate() != null) {
            card.setExpirationDate(dto.getExpirationDate());
        }

        if (dto.getActive() != null) {
            card.setActive(dto.getActive());
        }

        Card saved = cardRepository.save(card);

        return cardMapper.toDto(saved);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheNames.CARDS, key = "#id"),
            @CacheEvict(value = CacheNames.USERS_WITH_CARDS, allEntries = true)
    })
    public void delete(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(CARD_NOT_FOUND));

        cardRepository.delete(card);
    }

    public Page<CardDTO> getAll(String holder, Boolean active, Pageable pageable) {
        return cardRepository.findAll(Specification
                .where(CardSpecification
                        .hasHolder(holder))
                .and(CardSpecification
                        .isActive(active)),
                pageable)
                .map(cardMapper::toDto);
    }
}
