package com.example.taskmanager.controller;

import com.example.taskmanager.model.dto.request.UserRequestDTO;
import com.example.taskmanager.model.dto.request.UserUpdateRequestDTO;
import com.example.taskmanager.model.dto.response.UserResponseDTO;
import com.example.taskmanager.model.enums.Role;
import com.example.taskmanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserRequestDTO request) {

        UserResponseDTO created = userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }



    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDTO request) {

        UserResponseDTO updated = userService.updateUser(id, request);

        return ResponseEntity.ok(updated);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {

        UserResponseDTO user = userService.getUserById(id);

        return ResponseEntity.ok(user);
    }


    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> searchUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10, sort = "fullName") Pageable pageable) {

        return ResponseEntity.ok(userService.searchUsers(search, role, active, pageable));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}