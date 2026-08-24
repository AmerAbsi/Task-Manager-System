package com.example.taskmanager.model.converter;

import com.example.taskmanager.model.enums.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Role value) {
        return value == null ? null : value.getId();
    }

    @Override
    public Role convertToEntityAttribute(Integer id) {
        return id == null ? null : Role.fromId(id);
    }
}