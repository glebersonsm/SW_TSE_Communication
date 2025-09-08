package com.sw.tse.core.util;

import com.sw.tse.domain.model.api.enums.SexoEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SexoEnumConverter implements AttributeConverter<SexoEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(SexoEnum sexoEnum) {
        if (sexoEnum == null) {
            return null;
        }
        return sexoEnum.getCodigo();
    }

    @Override
    public SexoEnum convertToEntityAttribute(Integer codigo) {
        if (codigo == null) {
            return null;
        }
        
        for (SexoEnum sexo : SexoEnum.values()) {
            if (sexo.getCodigo() == codigo) {
                return sexo;
            }
        }
        
        throw new IllegalArgumentException("Código de sexo inválido: " + codigo);
    }
}