package org.example.userservice.service;

import org.example.userservice.dto.PaymentCardRequestDto;
import org.example.userservice.dto.PaymentCardResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentCardService {

    PaymentCardResponseDto create(PaymentCardRequestDto requestDto);

    PaymentCardResponseDto getById(Long id);

    Page<PaymentCardResponseDto> getAll(Pageable pageable);

    List<PaymentCardResponseDto> getByUserId(Long userId);

    PaymentCardResponseDto update(Long id, PaymentCardRequestDto requestDto);

    void changeActiveStatus(Long id, Boolean active);
}