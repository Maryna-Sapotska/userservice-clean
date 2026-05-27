package com.innowise.userservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.model.dto.CreateCardDto;
import com.innowise.userservice.model.dto.CreateUserDto;
import com.innowise.userservice.model.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CardIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCard_shouldReturn201() throws Exception {

        CreateUserDto userDto = new CreateUserDto();
        userDto.setName("John");
        userDto.setSurname("Doe");
        userDto.setEmail("john@test.com");
        userDto.setBirthDate(LocalDate.of(2000, 1, 1));

        String userResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDTO user = objectMapper.readValue(userResponse, UserDTO.class);

        CreateCardDto cardDto = new CreateCardDto();
        cardDto.setUserId(user.getId());
        cardDto.setNumber("1234123412341234");
        cardDto.setHolder("JOHN DOE");
        cardDto.setExpirationDate(LocalDate.now().plusYears(3));

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.holder")
                        .value("JOHN DOE"));
    }

    @Test
    void shouldNotAllowMoreThan5Cards() throws Exception {

        CreateUserDto userDto = new CreateUserDto();
        userDto.setName("John");
        userDto.setSurname("Doe");
        userDto.setEmail("max@test.com");
        userDto.setBirthDate(LocalDate.of(2000, 1, 1));

        String userResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDTO user = objectMapper.readValue(userResponse, UserDTO.class);

        CreateCardDto cardDto = new CreateCardDto();
        cardDto.setUserId(user.getId());
        cardDto.setNumber("1234123412341234");
        cardDto.setHolder("JOHN DOE");
        cardDto.setExpirationDate(LocalDate.now().plusYears(3));

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/cards")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cardDto)))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDto)))
                .andExpect(status().isBadRequest());
    }
}
