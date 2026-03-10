package org.example.userservice.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class PaymentCardResponseDto {

    private Long id;
    private String number;
    private String holder;
    private LocalDate expirationDate;
    private Boolean active;
    private Long userId;
    private Instant createdAt;
    private Instant updatedAt;
}