package com.innowise.userservice.controller;

import com.innowise.userservice.model.dto.CreateUserDto;
import com.innowise.userservice.model.dto.UpdateUserDto;
import com.innowise.userservice.model.dto.UserDTO;
import com.innowise.userservice.model.dto.UserWithCardsDto;
import com.innowise.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(UserController.REST_URL)
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management API")
public class UserController {

    public static final String REST_URL = "/users";

    private final UserService userService;

    @Operation(summary = "Create user")
    @ApiResponse(responseCode = "201", description = "User created")
    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody CreateUserDto dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.create(dto));
    }

    @Operation(summary = "Get user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        return ResponseEntity
                .ok(userService.getById(id));
    }

    @Operation(
            summary = "Get user with cards",
            description = "Returns user info together with all user cards"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User with cards retrieved"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userId}/cards")
    public ResponseEntity<UserWithCardsDto> getUserWithCards(
            @Parameter(description = "User id")
            @PathVariable Long userId){
        return ResponseEntity
                .ok(userService.getUserWithCards(userId));
    }

    @Operation(summary = "Get all users with filters and pagination")
    @ApiResponse(responseCode = "200", description = "Users retrieved")
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAll(
            @Parameter(description = "Filter by name")
            @RequestParam(required = false) String name,

            @Parameter(description = "Filter by surname")
            @RequestParam(required = false) String surname,

            @Parameter(description = "Filter by active status")
            @RequestParam(required = false) Boolean active,

            @Parameter(description = "Page number (0..N)")
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10")
            int size,

            @Parameter(description = "Sorting format: field,direction")
            @RequestParam(defaultValue = "id,asc")
            String sort) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.fromString(sort.split(",")[1]),
                        sort.split(",")[0]
                )
        );
        return ResponseEntity
                .ok(userService.getAll(name, surname, active, pageable));
    }

    @Operation(summary = "Update user")
    @PatchMapping("/{id}")
    public ResponseEntity<UserWithCardsDto> update(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateUserDto dto) {
        return ResponseEntity
                .ok(userService.update(id, dto));
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
