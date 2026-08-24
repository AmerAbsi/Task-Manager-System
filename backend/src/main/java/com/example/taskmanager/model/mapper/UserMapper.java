package com.example.taskmanager.model.mapper;

import com.example.taskmanager.model.dto.request.UserRequestDTO;
import com.example.taskmanager.model.dto.response.UserResponseDTO;
import com.example.taskmanager.model.entity.User;

import java.util.List;

public interface UserMapper {

    User toEntity(UserRequestDTO dto);

    UserResponseDTO toResponse(User user);

    List<UserResponseDTO> toResponseList(List<User> users);
}