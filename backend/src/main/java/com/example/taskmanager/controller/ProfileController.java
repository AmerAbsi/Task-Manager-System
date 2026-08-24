package com.example.taskmanager.controller;

import com.example.taskmanager.model.dto.request.ProfileUpdateRequestDTO;
import com.example.taskmanager.model.dto.response.UserResponseDTO;
import com.example.taskmanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponseDTO> getProfile(Authentication authentication) {

        UserResponseDTO profile = userService.getProfile(authentication.getName());

        return ResponseEntity.ok(profile);
    }

    @PutMapping
    public ResponseEntity<UserResponseDTO> updateProfile(
            @Valid @RequestBody ProfileUpdateRequestDTO request,
            Authentication authentication) {

        UserResponseDTO updated = userService.updateProfile(
                authentication.getName(), request);

        return ResponseEntity.ok(updated);
    }
}