package org.example.userservice.service.impl;

import org.example.userservice.dto.PaymentCardRequestDto;
import org.example.userservice.dto.PaymentCardResponseDto;
import org.example.userservice.entity.PaymentCard;
import org.example.userservice.entity.User;
import org.example.userservice.exception.CardLimitExceededException;
import org.example.userservice.exception.ResourceNotFoundException;
import org.example.userservice.mapper.PaymentCardMapper;
import org.example.userservice.repository.PaymentCardRepository;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.service.PaymentCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentCardServiceImpl implements PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;
    private final PaymentCardMapper paymentCardMapper;

    @Override
    @Transactional
    public PaymentCardResponseDto create(PaymentCardRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + requestDto.getUserId()));

        long cardCount = paymentCardRepository.countByUserId(user.getId());
        if (cardCount >= 5) {
            throw new CardLimitExceededException("User cannot have more than 5 cards");
        }

        PaymentCard paymentCard = paymentCardMapper.toEntity(requestDto);
        paymentCard.setUser(user);

        PaymentCard savedCard = paymentCardRepository.save(paymentCard);
        return paymentCardMapper.toDto(savedCard);
    }

    @Override
    public PaymentCardResponseDto getById(Long id) {
        PaymentCard paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));

        return paymentCardMapper.toDto(paymentCard);
    }

    @Override
    public Page<PaymentCardResponseDto> getAll(Pageable pageable) {
        return paymentCardRepository.findAll(pageable)
                .map(paymentCardMapper::toDto);
    }

    @Override
    public List<PaymentCardResponseDto> getByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return paymentCardRepository.findByUserId(userId)
                .stream()
                .map(paymentCardMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public PaymentCardResponseDto update(Long id, PaymentCardRequestDto requestDto) {
        PaymentCard paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + requestDto.getUserId()));

        paymentCard.setNumber(requestDto.getNumber());
        paymentCard.setHolder(requestDto.getHolder());
        paymentCard.setExpirationDate(requestDto.getExpirationDate());
        paymentCard.setActive(requestDto.getActive());
        paymentCard.setUser(user);

        return paymentCardMapper.toDto(paymentCard);
    }

    @Override
    @Transactional
    public void changeActiveStatus(Long id, Boolean active) {
        if (!paymentCardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Card not found with id: " + id);
        }

        paymentCardRepository.updateActive(id, active);
    }
}