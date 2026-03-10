package org.example.userservice.service.impl;

import org.example.userservice.dto.UserRequestDto;
import org.example.userservice.dto.UserResponseDto;
import org.example.userservice.entity.User;
import org.example.userservice.exception.ResourceNotFoundException;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.repository.specification.UserSpecification;
import org.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto create(UserRequestDto requestDto) {
        User user = userMapper.toEntity(requestDto);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserResponseDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return userMapper.toDto(user);
    }

    @Override
    public Page<UserResponseDto> getAll(String name, String surname, Pageable pageable) {
        Specification<User> specification = Specification
                .where(UserSpecification.hasName(name))
                .and(UserSpecification.hasSurname(surname));

        return userRepository.findAll(specification, pageable)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional
    public UserResponseDto update(Long id, UserRequestDto requestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setName(requestDto.getName());
        user.setSurname(requestDto.getSurname());
        user.setBirthDate(requestDto.getBirthDate());
        user.setEmail(requestDto.getEmail());
        user.setActive(requestDto.getActive());

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public void changeActiveStatus(Long id, Boolean active) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        userRepository.updateActive(id, active);
    }
}