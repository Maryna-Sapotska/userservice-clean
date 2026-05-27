package com.innowise.userservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.model.dto.CreateUserDto;
import com.innowise.userservice.model.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_shouldReturn201() throws Exception {

        String email = "johnn@test.com";

        CreateUserDto dto = new CreateUserDto();
        dto.setName("John");
        dto.setSurname("Doe");
        dto.setEmail(email);
        dto.setBirthDate(LocalDate.of(2000, 1, 1));

        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email")
                        .value(email))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDTO user = objectMapper.readValue(response, UserDTO.class);

        mockMvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void getUserById_shouldReturn200() throws Exception {

        String email = "johnn@test.com";

        CreateUserDto dto = new CreateUserDto();
        dto.setName("John");
        dto.setSurname("Doe");
        dto.setEmail(email);
        dto.setBirthDate(LocalDate.of(2000, 1, 1));

        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("johnn@test.com"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDTO user =
                objectMapper.readValue(response, UserDTO.class);

        mockMvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email")
                        .value(email));
    }
}
