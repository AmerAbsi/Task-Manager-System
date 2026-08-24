package com.example.taskmanager.controller;
import com.example.taskmanager.model.dto.request.LoginRequestDTO;
import com.example.taskmanager.model.dto.response.LoginResponseDTO;
import com.example.taskmanager.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {

        LoginResponseDTO response = authService.login(request);

        return ResponseEntity.ok(response);
    }



    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {

        authService.logout(authentication.getName());

        return ResponseEntity.noContent().build();
    }
}