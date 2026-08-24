package com.example.taskmanager.model.mapper.impl;

import com.example.taskmanager.model.dto.request.UserRequestDTO;
import com.example.taskmanager.model.dto.response.UserResponseDTO;
import com.example.taskmanager.model.entity.User;
import com.example.taskmanager.model.mapper.UserMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .role(dto.getRole())
                .active(dto.getActive())
                .build();
    }

    @Override
    public UserResponseDTO toResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public List<UserResponseDTO> toResponseList(List<User> users) {
        if (users == null) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(this::toResponse)
                .toList();
    }
}