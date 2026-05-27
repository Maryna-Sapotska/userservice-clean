package com.innowise.userservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.model.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FullFlowIntegrationTest extends AbstractIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullFlow_userCardLifecycle_shouldWork() throws Exception {

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
        cardDto.setExpirationDate(LocalDate.now().plusYears(2));

        String cardResponse = mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CardDTO card = objectMapper.readValue(cardResponse, CardDTO.class);

        mockMvc.perform(get("/users/{userId}/cards", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cards").isArray())
                .andExpect(jsonPath("$.cards[0].holder").value("JOHN DOE"));

        UpdateCardDto updateDto = new UpdateCardDto();
        updateDto.setActive(false);

        mockMvc.perform(patch("/cards/{id}", card.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/cards/{id}", card.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        mockMvc.perform(delete("/users/{id}", user.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isNotFound());
    }
}
