package com.example.taskmanager.model.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum NotificationType {

    TASK_ASSIGNED(1),
    TASK_STATUS_CHANGED(2),
    COMMENT_ADDED(3),
    USER_MODIFIED(4);

    private final int id;

    NotificationType(int id) {
        this.id = id;
    }

    public static NotificationType fromId(int id) {
        return Arrays.stream(values())
                .filter(value -> value.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown NotificationType id: " + id));
    }
}