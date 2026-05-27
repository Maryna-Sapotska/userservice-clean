package com.innowise.userservice.service;

import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.CreateUserDto;
import com.innowise.userservice.model.dto.UpdateUserDto;
import com.innowise.userservice.model.dto.UserDTO;
import com.innowise.userservice.model.dto.UserWithCardsDto;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void create_shouldCreateUser() {

        CreateUserDto dto = new CreateUserDto();
        dto.setName("John");
        dto.setSurname("Doe");
        dto.setEmail("john@test.com");
        dto.setBirthDate(LocalDate.now());

        User user = new User();
        User savedUser = new User();
        savedUser.setId(1L);

        UserDTO response = new UserDTO();
        response.setId(1L);

        when(userMapper.toEntity(dto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDTO(savedUser)).thenReturn(response);

        UserDTO result = userService.create(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(userRepository).save(user);
    }

    @Test
    void getById_shouldReturnUser() {
        User user = new User();
        user.setId(1L);

        UserDTO dto = new UserDTO();
        dto.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(userMapper.toDTO(user))
                .thenReturn(dto);

        UserDTO result = userService.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getById_shouldThrowException() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getById(1L));
    }

    @Test
    void update_shouldActivateUser() {

        User user = new User();
        user.setId(1L);
        user.setActive(false);

        UpdateUserDto dto = new UpdateUserDto();
        dto.setActive(true);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.findByIdWithCards(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        userService.update(1L, dto);

        assertTrue(user.isActive());

        verify(userRepository).save(user);
    }

    @Test
    void update_shouldDeactivateUser() {

        User user = new User();
        user.setId(1L);
        user.setActive(true);

        UpdateUserDto dto = new UpdateUserDto();
        dto.setActive(false);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.findByIdWithCards(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        userService.update(1L, dto);

        assertFalse(user.isActive());

        verify(userRepository).save(user);
    }

    @Test
    void update_shouldUpdateUserAndReturnDto() {

        Long id = 1L;

        UpdateUserDto dto = new UpdateUserDto();
        dto.setName("New");
        dto.setSurname("New");
        dto.setEmail("new@mail.com");
        dto.setBirthDate(LocalDate.of(2000, 1, 1));

        User user = new User();
        user.setId(id);

        UserWithCardsDto response = new UserWithCardsDto();

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        when(userRepository.findByIdWithCards(id))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        when(userMapper.toUserWithCardsDto(any(User.class)))
                .thenReturn(response);

        UserWithCardsDto result = userService.update(id, dto);

        assertNotNull(result);

        verify(userRepository).findById(id);
        verify(userRepository).findByIdWithCards(id);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void delete_shouldRemoveUser_whenExists() {

        Long id = 1L;

        when(userRepository.existsById(id)).thenReturn(true);

        userService.delete(id);
        verify(userRepository).deleteById(id);
    }

    @Test
    void delete_shouldThrow_whenUserNotFound() {

        Long id = 1L;

        when(userRepository.existsById(id)).thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> userService.delete(id));
    }

    @Test
    void getAll_shouldReturnPage() {

        Pageable pageable = Pageable.unpaged();
        Page<User> page = new PageImpl<>(List.of(new User()));

        when(userRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(userMapper.toDTO(any(User.class)))
                .thenReturn(new UserDTO());

        Page<UserDTO> result = userService.getAll(null, null, null, pageable);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void shouldReturnUserWithCards() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        when(userRepository.findByIdWithCards(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserWithCardsDto(user)).thenReturn(new UserWithCardsDto());

        UserWithCardsDto result = userService.getUserWithCards(userId);

        assertNotNull(result);
        verify(userRepository).findByIdWithCards(userId);
    }

    @Test
    void update_shouldThrow_whenUserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UpdateUserDto dto = new UpdateUserDto();

        assertThrows(UserNotFoundException.class,
                () -> userService.update(1L, dto));
    }

    @Test
    void update_shouldThrow_whenReloadUserNotFound() {

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        when(userRepository.findByIdWithCards(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.update(1L, new UpdateUserDto()));
    }

    @Test
    void getUserWithCards_shouldThrow_whenNotFound() {

        when(userRepository.findByIdWithCards(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserWithCards(1L));
    }

    @Test
    void create_shouldSetActiveTrue() {

        CreateUserDto dto = new CreateUserDto();

        User user = new User();
        User saved = new User();

        when(userMapper.toEntity(dto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(saved);
        when(userMapper.toDTO(saved)).thenReturn(new UserDTO());

        userService.create(dto);

        assertTrue(user.isActive());
    }
}