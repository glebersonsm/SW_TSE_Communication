package com.sw.tse.core.util;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

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
            if (service == null) {
                log.warn("CriptografiaService não está disponível. A data não será transformada.");
                return attribute; 
            }


            String dataFormatadaParaApi = attribute.format(DateTimeFormatter.ISO_LOCAL_DATE);
            
            String dataTransformadaComAspas = service.criptografarData(dataFormatadaParaApi);

            if (dataTransformadaComAspas == null || dataTransformadaComAspas.length() < 2) {
                log.error("API de criptografia retornou uma data inválida: {}", dataTransformadaComAspas);
                throw new IllegalStateException("Resposta inválida da API de transformação de data.");
            }
            String dataLimpa = dataTransformadaComAspas.replaceAll("^\"|\"$", "");

            OffsetDateTime odt = OffsetDateTime.parse(dataLimpa);

            return odt.toLocalDate();

        } catch (Exception e) {
        	log.error("Erro ao transformar a data '{}' para o banco de dados.", attribute, e);
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