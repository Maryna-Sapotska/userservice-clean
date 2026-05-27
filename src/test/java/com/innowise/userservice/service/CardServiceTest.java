package com.innowise.userservice.service;

import com.innowise.userservice.exception.BusinessException;
import com.innowise.userservice.exception.CardNotFoundException;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.mapper.CardMapper;
import com.innowise.userservice.model.dto.CardDTO;
import com.innowise.userservice.model.dto.CreateCardDto;
import com.innowise.userservice.model.dto.UpdateCardDto;
import com.innowise.userservice.model.entity.Card;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.CardRepository;
import com.innowise.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {
    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardService cardService;

    @Test
    void createCard_shouldCreateSuccessfully() {
        CreateCardDto dto = new CreateCardDto();
        dto.setUserId(1L);

        User user = new User();
        user.setId(1L);

        Card card = new Card();
        Card savedCard = new Card();

        CardDTO cardDTO = new CardDTO();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardRepository.countUserCards(1L)).thenReturn(1L);
        when(cardMapper.toEntity(dto)).thenReturn(card);
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);
        when(cardMapper.toDto(savedCard)).thenReturn(cardDTO);

        CardDTO result = cardService.create(dto);

        assertNotNull(result);

        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCard_shouldThrow_whenLimitExceeded() {
        CreateCardDto dto = new CreateCardDto();
        dto.setUserId(1L);

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardRepository.countUserCards(1L)).thenReturn(5L);

        assertThrows(BusinessException.class,
                () -> cardService.create(dto));

        verify(cardRepository).countUserCards(1L);
    }

    @Test
    void getById_shouldReturnCard() {

        Long id = 1L;

        Card card = new Card();
        card.setId(id);

        CardDTO dto = new CardDTO();

        when(cardRepository.findById(id)).thenReturn(Optional.of(card));
        when(cardMapper.toDto(card)).thenReturn(dto);

        CardDTO result = cardService.getById(id);

        assertNotNull(result);
    }

    @Test
    void update_shouldModifyCard() {

        Long id = 1L;

        UpdateCardDto dto = new UpdateCardDto();
        dto.setNumber("1234123412341234");
        dto.setHolder("JOHN DOE");
        dto.setExpirationDate(LocalDate.now().plusYears(1));

        User user = new User();
        user.setId(1L);

        Card card = new Card();
        card.setId(id);
        card.setUser(user);

        when(cardRepository.findById(id)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toDto(card)).thenReturn(new CardDTO());

        CardDTO result = cardService.update(id, dto);

        assertNotNull(result);
    }

    @Test
    void delete_shouldDeleteCard() {

        Long id = 1L;

        Long userId = 10L;

        User user = new User();
        user.setId(userId);

        Card card = new Card();
        card.setId(id);
        card.setUser(user);

        when(cardRepository.findById(id))
                .thenReturn(Optional.of(card));

        cardService.delete(id);

        verify(cardRepository).delete(card);
    }

    @Test
    void update_shouldActivateCard() {

        Card card = new Card();
        card.setActive(false);

        UpdateCardDto dto = new UpdateCardDto();
        dto.setActive(true);

        when(cardRepository.findById(1L))
                .thenReturn(Optional.of(card));
        when(cardRepository.save(card))
                .thenReturn(card);

        cardService.update(1L, dto);

        assertTrue(card.isActive());
        verify(cardRepository).save(card);
    }

    @Test
    void update_shouldDeactivateCard() {

        Card card = new Card();
        card.setActive(true);

        UpdateCardDto dto = new UpdateCardDto();
        dto.setActive(false);

        when(cardRepository.findById(1L))
                .thenReturn(Optional.of(card));

        when(cardRepository.save(card))
                .thenReturn(card);

        cardService.update(1L, dto);

        assertFalse(card.isActive());

        verify(cardRepository).save(card);
    }

    @Test
    void update_shouldThrow_whenCardNotFound() {

        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class,
                () -> cardService.update(1L, new UpdateCardDto()));
    }

    @Test
    void shouldReturnCardsByUserId() {

        Long userId = 1L;

        List<Card> cards = List.of(new Card(), new Card());

        when(cardRepository.findByUser_Id(userId)).thenReturn(cards);
        when(cardMapper.toDtoList(cards)).thenReturn(List.of(new CardDTO(), new CardDTO()));

        List<CardDTO> result = cardService.getByUserId(userId);

        assertEquals(2, result.size());
        verify(cardRepository).findByUser_Id(userId);
    }

    @Test
    void getAll_shouldReturnPagedCards() {

        Pageable pageable = PageRequest.of(0, 10);

        Card card = new Card();
        Page<Card> page = new PageImpl<>(List.of(card));

        when(cardRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(cardMapper.toDto(card)).thenReturn(new CardDTO());

        Page<CardDTO> result = cardService.getAll(null, null, pageable);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void delete_shouldThrow_whenCardNotFound() {

        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class,
                () -> cardService.delete(1L));
    }

    @Test
    void create_shouldThrow_whenUserNotFound() {
        CreateCardDto dto = new CreateCardDto();
        dto.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> cardService.create(dto));

        verify(userRepository).findById(1L);
    }

    @Test
    void create_shouldThrow_whenMapperReturnsNull() {
        CreateCardDto dto = new CreateCardDto();
        dto.setUserId(1L);

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardRepository.countUserCards(1L)).thenReturn(0L);
        when(cardMapper.toEntity(dto)).thenReturn(null);

        assertThrows(BusinessException.class,
                () -> cardService.create(dto));
    }

    @Test
    void getById_shouldThrow_whenCardNotFound() {

        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class,
                () -> cardService.getById(1L));
    }

    @Test
    void update_shouldChangeOnlyHolder() {

        Card card = new Card();
        card.setHolder("OLD");
        card.setNumber("1111");

        UpdateCardDto dto = new UpdateCardDto();
        dto.setHolder("NEW");

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toDto(card)).thenReturn(new CardDTO());

        cardService.update(1L, dto);

        assertEquals("NEW", card.getHolder());
        assertEquals("1111", card.getNumber()); // не должен измениться
    }
}