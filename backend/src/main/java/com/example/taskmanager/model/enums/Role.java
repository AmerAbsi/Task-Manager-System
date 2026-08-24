package com.example.taskmanager.model.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Role {

    ADMIN(1),
    USER(2);

    private final int id;

    Role(int id) {
        this.id = id;
    }

    public static Role fromId(int id) {
        return Arrays.stream(values())
                .filter(value -> value.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown Role id: " + id));
    }
}