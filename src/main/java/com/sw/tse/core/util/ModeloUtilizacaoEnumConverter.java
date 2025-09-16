package com.sw.tse.core.util;

import com.sw.tse.domain.model.api.enums.ModeloUtilizacaoEnum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ModeloUtilizacaoEnumConverter implements AttributeConverter<ModeloUtilizacaoEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ModeloUtilizacaoEnum attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getCodigo();
    }

    @Override
    public ModeloUtilizacaoEnum convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return ModeloUtilizacaoEnum.fromCodigo(dbData);
    }
}