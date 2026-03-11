package org.example.userservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@Data
public class PaymentCardResponseDto implements Serializable {

    private Long id;
    private String number;
    private String holder;
    private LocalDate expirationDate;
    private Boolean active;
    private Long userId;
    private Instant createdAt;
    private Instant updatedAt;
}