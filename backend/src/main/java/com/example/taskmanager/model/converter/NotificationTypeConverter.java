package com.example.taskmanager.model.converter;

import com.example.taskmanager.model.enums.NotificationType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class NotificationTypeConverter implements AttributeConverter<NotificationType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(NotificationType value) {
        return value == null ? null : value.getId();
    }

    @Override
    public NotificationType convertToEntityAttribute(Integer id) {
        return id == null ? null : NotificationType.fromId(id);
    }
}