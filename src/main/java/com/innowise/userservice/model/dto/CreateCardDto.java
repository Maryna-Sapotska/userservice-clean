package com.innowise.userservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateCardDto {

    @NotNull(message = "User id is required")
    private Long userId;

    @NotBlank(message = "Card number is required")
    private String number;

    @NotBlank(message = "Holder is required")
    private String holder;

    @NotNull(message = "Expiration date is required")
    private LocalDate expirationDate;
}
