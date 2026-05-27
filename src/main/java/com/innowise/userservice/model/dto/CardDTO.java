package com.innowise.userservice.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CardDTO {

    private Long id;
    private Long userId;
    private String number;
    private String holder;
    private LocalDate expirationDate;
    private boolean active;
}
