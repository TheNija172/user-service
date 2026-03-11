package org.example.userservice.service;

import org.example.userservice.dto.PaymentCardRequestDto;
import org.example.userservice.dto.PaymentCardResponseDto;
import org.example.userservice.entity.PaymentCard;
import org.example.userservice.entity.User;
import org.example.userservice.exception.CardLimitExceededException;
import org.example.userservice.exception.ResourceNotFoundException;
import org.example.userservice.mapper.PaymentCardMapper;
import org.example.userservice.repository.PaymentCardRepository;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.service.impl.PaymentCardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceImplTest {

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private PaymentCardServiceImpl paymentCardService;

    @Test
    void create_shouldSaveCard_whenUserExistsAndCardLimitNotExceeded() {
        PaymentCardRequestDto requestDto = new PaymentCardRequestDto();
        requestDto.setUserId(1L);
        requestDto.setNumber("1111222233334444");
        requestDto.setHolder("IVAN PETROV");
        requestDto.setExpirationDate(LocalDate.of(2030, 12, 31));
        requestDto.setActive(true);

        User user = new User();
        user.setId(1L);

        PaymentCard card = new PaymentCard();
        PaymentCard savedCard = new PaymentCard();
        savedCard.setId(1L);

        PaymentCardResponseDto responseDto = new PaymentCardResponseDto();
        responseDto.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.countByUserId(1L)).thenReturn(0L);
        when(paymentCardMapper.toEntity(requestDto)).thenReturn(card);
        when(paymentCardRepository.save(card)).thenReturn(savedCard);
        when(paymentCardMapper.toDto(savedCard)).thenReturn(responseDto);
        when(cacheManager.getCache("users")).thenReturn(cache);

        PaymentCardResponseDto result = paymentCardService.create(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(paymentCardRepository).save(card);
        verify(cache).evict(1L);
    }

    @Test
    void create_shouldThrowException_whenUserNotFound() {
        PaymentCardRequestDto requestDto = new PaymentCardRequestDto();
        requestDto.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentCardService.create(requestDto));
    }

    @Test
    void create_shouldThrowException_whenCardLimitExceeded() {
        PaymentCardRequestDto requestDto = new PaymentCardRequestDto();
        requestDto.setUserId(1L);

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.countByUserId(1L)).thenReturn(5L);

        assertThrows(CardLimitExceededException.class,
                () -> paymentCardService.create(requestDto));
    }

    @Test
    void getById_shouldReturnCard_whenExists() {
        PaymentCard card = new PaymentCard();
        card.setId(1L);

        PaymentCardResponseDto responseDto = new PaymentCardResponseDto();
        responseDto.setId(1L);

        when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(paymentCardMapper.toDto(card)).thenReturn(responseDto);

        PaymentCardResponseDto result = paymentCardService.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getByUserId_shouldReturnCards() {
        User user = new User();
        user.setId(1L);

        PaymentCard card = new PaymentCard();
        card.setId(1L);

        PaymentCardResponseDto responseDto = new PaymentCardResponseDto();
        responseDto.setId(1L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(paymentCardRepository.findByUserId(1L)).thenReturn(List.of(card));
        when(paymentCardMapper.toDto(card)).thenReturn(responseDto);

        List<PaymentCardResponseDto> result = paymentCardService.getByUserId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void changeActiveStatus_shouldEvictCache() {
        User user = new User();
        user.setId(1L);

        PaymentCard card = new PaymentCard();
        card.setId(1L);
        card.setUser(user);

        when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cacheManager.getCache("users")).thenReturn(cache);

        paymentCardService.changeActiveStatus(1L, false);

        verify(paymentCardRepository).updateActive(1L, false);
        verify(cache).evict(1L);
    }
}