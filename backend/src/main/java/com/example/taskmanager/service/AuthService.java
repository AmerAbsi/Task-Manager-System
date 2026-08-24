package com.example.taskmanager.service;

import com.example.taskmanager.model.dto.request.LoginRequestDTO;
import com.example.taskmanager.model.dto.response.LoginResponseDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO request);
    void logout(String username);
}