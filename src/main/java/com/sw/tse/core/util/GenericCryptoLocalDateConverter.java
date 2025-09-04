package com.sw.tse.core.util;

import java.time.LocalDate;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Converter
public class GenericCryptoLocalDateConverter implements AttributeConverter<LocalDate, LocalDate>, ApplicationContextAware {

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
    public LocalDate convertToDatabaseColumn(LocalDate attribute) {
        if (attribute == null) {
            return null;
        }
        
        try {
            CriptografiaService service = getCriptografiaService();
            if (service != null) {
                String dataCripotograda = service.criptografarData(attribute.toString());
                return LocalDate.parse(dataCripotograda);
            }
            return attribute;
        } catch (Exception e) {
            log.error("Erro ao criptografar data para o banco de dados: {}", e.getMessage(), e);
            return attribute;
        }
    }

    @Override
    public LocalDate convertToEntityAttribute(LocalDate dbData) {
        if (dbData == null) {
            return null;
        }
        
        try {
            CriptografiaService service = getCriptografiaService();
            if (service != null) {
                return service.descriptografarData(dbData);
            }
            return dbData;
        } catch (Exception e) {
            log.error("Erro ao descriptografar data do banco de dados: {}", e.getMessage(), e);
            return dbData;
        }
    }

}