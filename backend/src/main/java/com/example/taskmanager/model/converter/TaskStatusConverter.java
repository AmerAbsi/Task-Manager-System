package com.example.taskmanager.model.converter;

import com.example.taskmanager.model.enums.TaskStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TaskStatusConverter implements AttributeConverter<TaskStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TaskStatus value) {
        return value == null ? null : value.getId();
    }

    @Override
    public TaskStatus convertToEntityAttribute(Integer id) {
        return id == null ? null : TaskStatus.fromId(id);
    }
}