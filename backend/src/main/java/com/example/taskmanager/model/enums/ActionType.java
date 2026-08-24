package com.example.taskmanager.model.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ActionType {

    LOGIN(1),
    LOGOUT(2),
    USER_CREATED(3),
    USER_UPDATED(4),
    USER_DELETED(5),
    TASK_CREATED(6),
    TASK_UPDATED(7),
    TASK_STATUS_CHANGED(8),
    TASK_DELETED(9),
    COMMENT_ADDED(10),
    PROFILE_UPDATED(11);

    private final int id;

    ActionType(int id) {
        this.id = id;
    }

    public static ActionType fromId(int id) {
        return Arrays.stream(values())
                .filter(value -> value.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown ActionType id: " + id));
    }
}