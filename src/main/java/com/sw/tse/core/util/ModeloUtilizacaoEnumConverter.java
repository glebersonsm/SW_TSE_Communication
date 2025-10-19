package com.sw.tse.core.util;

import com.sw.tse.domain.model.api.enums.ModeloUtilizacaoEnum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ModeloUtilizacaoEnumConverter implements AttributeConverter<ModeloUtilizacaoEnum, String> {

    @Override
    public String convertToDatabaseColumn(ModeloUtilizacaoEnum attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name(); // Retorna "COTA" ou "PONTOS"
    }

    @Override
    public ModeloUtilizacaoEnum convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        try {
            return ModeloUtilizacaoEnum.valueOf(dbData.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Valor inválido para ModeloUtilizacaoEnum: " + dbData, e);
        }
    }
}