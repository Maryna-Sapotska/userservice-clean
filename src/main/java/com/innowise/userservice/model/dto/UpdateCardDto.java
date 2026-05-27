package com.innowise.userservice.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateCardDto {

    @Size(min = 16, max = 16)
    private String number;

    private String holder;
    private LocalDate expirationDate;
    private Boolean active;
}
