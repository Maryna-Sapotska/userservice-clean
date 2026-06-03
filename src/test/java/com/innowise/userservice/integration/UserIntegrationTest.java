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

    private CreateUserDto createDto(String email) {
        CreateUserDto dto = new CreateUserDto();
        dto.setName("John");
        dto.setSurname("Doe");
        dto.setEmail(email);
        dto.setBirthDate(LocalDate.of(2000, 1, 1));
        return dto;
    }

    @Test
    void createUser_shouldReturn201() throws Exception {

        String email = "johnn@test.com";

        String response = mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + token(1L, "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto(email))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email")
                        .value(email))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDTO user = objectMapper.readValue(response, UserDTO.class);

        mockMvc.perform(get("/users/{id}", user.getId())
                .header("Authorization", "Bearer " + token(1L, "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void createUser_shouldReturn403_whenUserRole() throws Exception {

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + token(1L, "ROLE_USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto("user@test.com"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserById_shouldReturn200_whenOwner() throws Exception {

        String email = "johnn@test.com";

        String response = mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + token(1L, "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto(email))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDTO user =
                objectMapper.readValue(response, UserDTO.class);

        mockMvc.perform(get("/users/{id}", user.getId())
                        .header("Authorization", "Bearer " + token(user.getId(), "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email")
                        .value(email));
    }

    @Test
    void getUser_shouldReturn403_whenDifferentUser() throws Exception {

        mockMvc.perform(get("/users/{id}", 999L)
                        .header("Authorization", "Bearer " + token(1L, "ROLE_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUser_shouldReturn401_whenNoToken() throws Exception {

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }
}
