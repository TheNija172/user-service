package org.example.userservice.service;

import org.example.userservice.dto.UserRequestDto;
import org.example.userservice.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponseDto create(UserRequestDto requestDto);

    UserResponseDto getById(Long id);

    Page<UserResponseDto> getAll(String name, String surname, Pageable pageable);

    UserResponseDto update(Long id, UserRequestDto requestDto);

    void changeActiveStatus(Long id, Boolean active);
}