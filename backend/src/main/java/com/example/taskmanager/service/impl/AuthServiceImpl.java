package com.example.taskmanager.service.impl;

import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.model.dto.request.LoginRequestDTO;
import com.example.taskmanager.model.dto.response.LoginResponseDTO;
import com.example.taskmanager.model.entity.User;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.JwtService;
import com.example.taskmanager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.taskmanager.model.enums.ActionType;
import com.example.taskmanager.service.ActivityLogService;
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {

        UsernamePasswordAuthenticationToken credentials =
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword());

        UserDetails userDetails;

        try {
            userDetails = (UserDetails) authenticationManager
                    .authenticate(credentials)
                    .getPrincipal();
        } catch (AuthenticationException ex) {
            activityLogService.log(ActionType.LOGIN,
                    "Failed login attempt", request.getUsername());
            throw ex;
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + request.getUsername()));

        activityLogService.log(ActionType.LOGIN, "User logged in", user);

        String token = jwtService.generateToken(
                userDetails,
                user.getRole().name(),
                user.getId());

        return LoginResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationMs())
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }


    @Override
    @Transactional
    public void logout(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + username));

        activityLogService.log(ActionType.LOGOUT, "User logged out", user);
    }
}