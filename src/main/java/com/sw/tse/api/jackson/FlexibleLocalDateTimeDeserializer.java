package com.sw.tse.api.jackson;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Desserializador flexível para LocalDateTime que aceita múltiplos formatos:
 * - ISO_OFFSET_DATE_TIME (ex: 2026-02-24T22:18:43.771-03:00)
 * - ISO_LOCAL_DATE_TIME   (ex: 2026-02-24T22:18:43.771)
 * - Instant ISO           (ex: 2026-02-24T22:18:43.771Z)
 * - ISO_LOCAL_DATE        (ex: 2026-02-24) -> início do dia
 */
public class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        if (text == null) {
            return null;
        }

        text = text.trim();
        if (text.isEmpty()) {
            return null;
        }

        // Tenta OffsetDateTime (ex: 2026-02-24T22:18:43.771-03:00)
        try {
            OffsetDateTime odt = OffsetDateTime.parse(text);
            return odt.toLocalDateTime();
        } catch (Exception ignored) {
        }

        // Tenta LocalDateTime padrão (sem offset)
        try {
            return LocalDateTime.parse(text);
        } catch (Exception ignored) {
        }

        // Tenta Instant em UTC (ex: ...Z)
        try {
            Instant instant = Instant.parse(text);
            return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        } catch (Exception ignored) {
        }

        // Tenta somente data (yyyy-MM-dd) -> começo do dia
        try {
            LocalDate date = LocalDate.parse(text);
            return date.atStartOfDay();
        } catch (Exception ignored) {
        }

        throw JsonMappingException.from(p,
                "Não foi possível converter valor '" + text + "' para LocalDateTime em FlexibleLocalDateTimeDeserializer");
    }
}

