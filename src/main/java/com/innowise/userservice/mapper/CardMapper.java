package com.innowise.userservice.mapper;

import com.innowise.userservice.model.dto.CardDTO;
import com.innowise.userservice.model.dto.CreateCardDto;
import com.innowise.userservice.model.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(source = "user.id", target = "userId")
    CardDTO toDto(Card card);

    List<CardDTO> toDtoList(List<Card> cards);

    @Mapping(target = "user", ignore = true)
    Card toEntity(CreateCardDto dto);
}
