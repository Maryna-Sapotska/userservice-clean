package com.innowise.userservice.controller;

import com.innowise.userservice.model.dto.CardDTO;
import com.innowise.userservice.model.dto.CreateCardDto;
import com.innowise.userservice.model.dto.UpdateCardDto;
import com.innowise.userservice.service.CardService;
import com.innowise.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

import java.util.List;

@RestController
@RequestMapping(CardController.REST_URL)
@RequiredArgsConstructor
@Tag(name = "Cards", description = "Card management API")
public class CardController {

    public static final String REST_URL = "/cards";

    private final CardService cardService;

    @Operation(summary = "Create card")
    @PostMapping
    public ResponseEntity<CardDTO> create(@Valid @RequestBody CreateCardDto dto){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cardService.create(dto));
    }

    @Operation(summary = "Get card by id")
    @GetMapping("/{id}")
    public ResponseEntity<CardDTO> getById(@PathVariable Long id){
        return ResponseEntity
                .ok(cardService.getById(id));
    }

    @Operation(summary = "Get all cards with filters and pagination")
    @ApiResponse(responseCode = "200", description = "Cards retrieved")
    @GetMapping
    public ResponseEntity<Page<CardDTO>> getAll(
            @Parameter(description = "Filter by holder name")
            @RequestParam(required = false)
            String holder,

            @Parameter(description = "Filter by active status")
            @RequestParam(required = false)
            Boolean active,

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
        return ResponseEntity.ok(cardService.getAll(holder, active, pageable));
    }

    @Operation(summary = "Get cards by id")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CardDTO>> getByUserId(@PathVariable Long userId){
        return ResponseEntity.ok(cardService.getByUserId(userId));
    }

    @Operation(summary = "Update card")
    @PatchMapping("/{id}")
    public ResponseEntity<CardDTO> update(@PathVariable Long id,
                                          @Valid @RequestBody UpdateCardDto dto){
        return ResponseEntity.ok(cardService.update(id, dto));
    }

    @Operation(summary = "Delete card")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
