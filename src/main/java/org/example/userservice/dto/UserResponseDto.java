package org.example.userservice.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
public class UserResponseDto {

    private Long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private List<PaymentCardResponseDto> cards;
}
