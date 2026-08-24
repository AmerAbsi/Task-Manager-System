package com.example.taskmanager.controller;

import com.example.taskmanager.model.dto.response.ActivityLogResponseDTO;
import com.example.taskmanager.model.enums.ActionType;
import com.example.taskmanager.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activity-logs")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<Page<ActivityLogResponseDTO>> searchLogs(
            @RequestParam(required = false) ActionType action,
            @RequestParam(required = false) String username,
            @PageableDefault(size = 20, sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ActivityLogResponseDTO> logs =
                activityLogService.searchLogs(action, username, pageable);

        return ResponseEntity.ok(logs);
    }
}