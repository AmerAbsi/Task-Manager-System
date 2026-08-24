package com.example.taskmanager.model.converter;

import com.example.taskmanager.model.enums.ActionType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ActionTypeConverter implements AttributeConverter<ActionType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ActionType value) {
        return value == null ? null : value.getId();
    }

    @Override
    public ActionType convertToEntityAttribute(Integer id) {
        return id == null ? null : ActionType.fromId(id);
    }
}