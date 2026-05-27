package com.innowise.userservice.mapper;

import com.innowise.userservice.model.dto.CreateUserDto;
import com.innowise.userservice.model.dto.UserDTO;
import com.innowise.userservice.model.dto.UserWithCardsDto;
import com.innowise.userservice.model.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = CardMapper.class)
public interface UserMapper {

    User toEntity(CreateUserDto dto);

    UserDTO toDTO(User user);

    List<UserDTO> toDtoList(List<User> users);

    UserWithCardsDto toUserWithCardsDto(User user);
}
