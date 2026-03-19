package org.example.userservice.dto;

import lombok.Data;
import org.example.userservice.entity.UserStatus;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
public class UserResponseDto implements Serializable {

    private Long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
    private UserStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private List<PaymentCardResponseDto> cards;
}
