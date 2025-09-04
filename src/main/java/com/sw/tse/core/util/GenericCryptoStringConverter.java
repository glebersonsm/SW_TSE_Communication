package com.sw.tse.core.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Converter
public class GenericCryptoStringConverter implements AttributeConverter<String, String>, ApplicationContextAware {

    private static ApplicationContext applicationContext;
    
    @Override
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }
    
    private CriptografiaService getCriptografiaService() {
        if (applicationContext != null) {
            return applicationContext.getBean(CriptografiaService.class);
        }
        return null;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.trim().isEmpty()) {
            return null;
        }
        
        try {
            CriptografiaService service = getCriptografiaService();
            if (service != null) {
                return service.criptografarValor(attribute);
            }
            return attribute;
        } catch (Exception e) {
            log.error("Erro ao criptografar valor para o banco de dados: {}", e.getMessage(), e);
            return attribute; 
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        
        try {
            CriptografiaService service = getCriptografiaService();
            if (service != null) {
                return service.descriptografarValor(dbData);
            }
            return dbData;
        } catch (Exception e) {
            log.error("Erro ao descriptografar valor do banco de dados: {}", e.getMessage(), e);
            return dbData;
        }
    }
}