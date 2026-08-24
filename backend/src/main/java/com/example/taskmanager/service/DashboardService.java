package com.example.taskmanager.service;

import com.example.taskmanager.model.dto.response.DashboardStatsResponseDTO;

public interface DashboardService {
    DashboardStatsResponseDTO getStats();
}