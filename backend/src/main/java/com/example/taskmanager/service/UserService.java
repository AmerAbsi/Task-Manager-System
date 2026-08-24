package com.example.taskmanager.service;

import com.example.taskmanager.model.dto.request.ProfileUpdateRequestDTO;
import com.example.taskmanager.model.dto.request.UserRequestDTO;
import com.example.taskmanager.model.dto.request.UserUpdateRequestDTO;
import com.example.taskmanager.model.dto.response.UserResponseDTO;
import com.example.taskmanager.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponseDTO createUser(UserRequestDTO request);

    UserResponseDTO updateUser(Long id, UserUpdateRequestDTO request);

    UserResponseDTO getUserById(Long id);

    Page<UserResponseDTO> searchUsers(String search,
                                      Role role,
                                      Boolean active,
                                      Pageable pageable);

    void deleteUser(Long id);

    UserResponseDTO getProfile(String username);

    UserResponseDTO updateProfile(String username, ProfileUpdateRequestDTO request);
}