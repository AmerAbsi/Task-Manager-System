package com.example.taskmanager.service.impl;

import com.example.taskmanager.exception.DuplicateResourceException;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.model.dto.request.ProfileUpdateRequestDTO;
import com.example.taskmanager.model.dto.request.UserRequestDTO;
import com.example.taskmanager.model.dto.request.UserUpdateRequestDTO;
import com.example.taskmanager.model.dto.response.UserResponseDTO;
import com.example.taskmanager.model.entity.User;
import com.example.taskmanager.model.enums.NotificationType;
import com.example.taskmanager.model.enums.Role;
import com.example.taskmanager.model.mapper.UserMapper;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.CurrentUserProvider;
import com.example.taskmanager.service.NotificationService;
import com.example.taskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.taskmanager.model.enums.ActionType;
import com.example.taskmanager.service.ActivityLogService;
import java.time.LocalDateTime;
import org.springframework.security.access.AccessDeniedException;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;
    private final CurrentUserProvider currentUserProvider;
    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException(
                    "Username already taken: " + request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email already registered: " + request.getEmail());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User saved = userRepository.save(user);
        User actor = currentUserProvider.getCurrentUser();
        activityLogService.log(ActionType.USER_CREATED,
                "Created user '" + saved.getUsername() + "' with role " + saved.getRole(),
                actor);

        notificationService.notifyAdmins(NotificationType.USER_MODIFIED,
                "New user created: " + saved.getFullName() + " (" + saved.getRole() + ")",
                null,
                actor);

        return userMapper.toResponse(saved);
    }



    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateRequestDTO request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));

        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new DuplicateResourceException(
                    "Email already registered: " + request.getEmail());
        }

        User actor = currentUserProvider.getCurrentUser();
        boolean isSelf = actor.getId().equals(user.getId());

        if (isSelf && Boolean.FALSE.equals(request.getActive())) {
            throw new AccessDeniedException("You cannot deactivate your own account");
        }

        if (isSelf && request.getRole() != user.getRole()) {
            throw new AccessDeniedException("You cannot change your own role");
        }

        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setRole(request.getRole());
        user.setActive(request.getActive());

        User saved = userRepository.save(user);

        activityLogService.log(ActionType.USER_UPDATED,
                "Updated user '" + saved.getUsername() + "'",
                actor);

        notificationService.notifyUser(
                saved,
                NotificationType.USER_MODIFIED,
                "Your account details were updated by an administrator",
                null);

        notificationService.notifyAdmins(NotificationType.USER_MODIFIED,
                "User updated: " + saved.getFullName(),
                null,
                actor);

        return userMapper.toResponse(saved);
    }
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> searchUsers(String search,
                                             Role role,
                                             Boolean active,
                                             Pageable pageable) {

        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();

        Page<User> users = userRepository.searchUsers(normalizedSearch, role, active, pageable);

        return users.map(userMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));

        User actor = currentUserProvider.getCurrentUser();

        if (actor.getId().equals(user.getId())) {
            throw new AccessDeniedException("You cannot delete your own account");
        }

        user.setDeletedAt(LocalDateTime.now());

        userRepository.save(user);

        activityLogService.log(ActionType.USER_DELETED,
                "Deleted user '" + user.getUsername() + "' (" + user.getEmail() + ")",
                actor);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getProfile(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + username));

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponseDTO updateProfile(String username, ProfileUpdateRequestDTO request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + username));

        if (userRepository.existsByEmailAndIdNot(request.getEmail(), user.getId())) {
            throw new DuplicateResourceException(
                    "Email already registered: " + request.getEmail());
        }

        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {

            if (request.getCurrentPassword() == null
                    || !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new AccessDeniedException("Current password is incorrect");
            }

            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User saved = userRepository.save(user);

        activityLogService.log(ActionType.PROFILE_UPDATED, "Updated own profile", saved);

        return userMapper.toResponse(saved);
    }




}