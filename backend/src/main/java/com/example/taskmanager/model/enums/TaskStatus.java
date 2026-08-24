package com.example.taskmanager.model.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TaskStatus {

    PENDING(1),
    IN_PROGRESS(2),
    COMPLETED(3);

    private final int id;

    TaskStatus(int id) {
        this.id = id;
    }

    public static TaskStatus fromId(int id) {
        return Arrays.stream(values())
                .filter(value -> value.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown TaskStatus id: " + id));
    }
}