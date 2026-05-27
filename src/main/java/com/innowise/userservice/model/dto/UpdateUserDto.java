package com.innowise.userservice.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateUserDto {

    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
    private Boolean active;
}
