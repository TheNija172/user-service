package org.example.userservice.controller;

import org.example.userservice.dto.PaymentCardRequestDto;
import org.example.userservice.dto.PaymentCardResponseDto;
import org.example.userservice.service.PaymentCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    @PostMapping
    public ResponseEntity<PaymentCardResponseDto> create(
            @Valid @RequestBody PaymentCardRequestDto requestDto
    ) {
        PaymentCardResponseDto responseDto = paymentCardService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentCardService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<PaymentCardResponseDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(paymentCardService.getAll(pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentCardResponseDto>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentCardService.getByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentCardResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody PaymentCardRequestDto requestDto
    ) {
        return ResponseEntity.ok(paymentCardService.update(id, requestDto));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> changeActiveStatus(
            @PathVariable Long id,
            @RequestParam Boolean active
    ) {
        paymentCardService.changeActiveStatus(id, active);
        return ResponseEntity.noContent().build();
    }
}