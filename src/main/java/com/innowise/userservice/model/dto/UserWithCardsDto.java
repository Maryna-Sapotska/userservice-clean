package com.innowise.userservice.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class UserWithCardsDto {

    private Long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
    private boolean active;
    private List<CardDTO> cards;
}
