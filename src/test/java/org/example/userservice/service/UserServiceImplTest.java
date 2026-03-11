package org.example.userservice.service;

import org.example.userservice.dto.UserRequestDto;
import org.example.userservice.dto.UserResponseDto;
import org.example.userservice.entity.User;
import org.example.userservice.exception.ResourceNotFoundException;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void create_shouldReturnSavedUser() {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setName("Ivan");
        requestDto.setSurname("Petrov");
        requestDto.setBirthDate(LocalDate.of(2000, 5, 12));
        requestDto.setEmail("ivan@test.com");
        requestDto.setActive(true);

        User user = new User();
        User savedUser = new User();
        savedUser.setId(1L);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(1L);
        responseDto.setName("Ivan");

        when(userMapper.toEntity(requestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(responseDto);

        UserResponseDto result = userService.create(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Ivan", result.getName());

        verify(userRepository).save(user);
    }

    @Test
    void getById_shouldReturnUser_whenUserExists() {
        User user = new User();
        user.setId(1L);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(1L);

        when(userRepository.findWithCardsById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getById_shouldThrowException_whenUserNotFound() {
        when(userRepository.findWithCardsById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void getAll_shouldReturnPageOfUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        User user = new User();
        Page<User> userPage = new PageImpl<>(java.util.List.of(user));

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(1L);

        when(userRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageable)))
                .thenReturn(userPage);
        when(userMapper.toDto(user)).thenReturn(responseDto);

        Page<UserResponseDto> result = userService.getAll("Ivan", "Petrov", pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void update_shouldUpdateAndReturnUser() {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setName("Updated");
        requestDto.setSurname("User");
        requestDto.setBirthDate(LocalDate.of(2001, 1, 1));
        requestDto.setEmail("updated@test.com");
        requestDto.setActive(false);

        User user = new User();
        user.setId(1L);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(1L);
        responseDto.setName("Updated");

        when(userRepository.findWithCardsById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.update(1L, requestDto);

        assertEquals("Updated", user.getName());
        assertEquals("User", user.getSurname());
        assertEquals("updated@test.com", user.getEmail());
        assertFalse(user.getActive());
        assertEquals(1L, result.getId());
    }

    @Test
    void changeActiveStatus_shouldCallRepository_whenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.changeActiveStatus(1L, false);

        verify(userRepository).updateActive(1L, false);
    }

    @Test
    void changeActiveStatus_shouldThrowException_whenUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> userService.changeActiveStatus(1L, false));
    }
}