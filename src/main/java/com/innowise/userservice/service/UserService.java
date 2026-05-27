package com.innowise.userservice.service;

import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.CreateUserDto;
import com.innowise.userservice.model.dto.UpdateUserDto;
import com.innowise.userservice.model.dto.UserDTO;
import com.innowise.userservice.model.dto.UserWithCardsDto;
import com.innowise.userservice.model.entity.CacheNames;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.repository.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private static final String USER_NOT_FOUND = "User not found";

    public UserDTO create(CreateUserDto dto) {
        User user = userMapper.toEntity(dto);
        user.setActive(true);
        return userMapper.toDTO(userRepository.save(user));
    }

    @Cacheable(value = CacheNames.USERS, key = "#id")
    public UserDTO getById(Long id) {
        return userMapper.toDTO(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND)));
    }

    public Page<UserDTO> getAll(String name, String surname, Boolean active, Pageable pageable) {

        return userRepository.findAll(Specification
                .where(UserSpecification
                        .hasName(name))
                .and(UserSpecification
                        .hasSurname(surname))
                .and(UserSpecification
                        .isActive(active)),
                pageable)
                .map(userMapper::toDTO);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheNames.USERS, key = "#id"),
            @CacheEvict(value = CacheNames.USERS_WITH_CARDS, key = "#id")
            })
    public UserWithCardsDto update(Long id, UpdateUserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }

        if (dto.getSurname() != null) {
            user.setSurname(dto.getSurname());
        }

        if (dto.getBirthDate() != null) {
            user.setBirthDate(dto.getBirthDate());
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getActive() != null) {
            user.setActive(dto.getActive());
        }

        User saved = userRepository.save(user);

        User reloaded = userRepository.findByIdWithCards(saved.getId())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        return userMapper.toUserWithCardsDto(reloaded);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.USERS_WITH_CARDS, key = "#id")
    public UserWithCardsDto getUserWithCards(Long id) {
        User user = userRepository.findByIdWithCards(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        return userMapper.toUserWithCardsDto(user);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheNames.USERS, key = "#id"),
            @CacheEvict(value = CacheNames.USERS_WITH_CARDS, key = "#id")
    })
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }
        userRepository.deleteById(id);
    }
}
