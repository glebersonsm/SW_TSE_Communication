package com.sw.tse.api.jackson;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Desserializador flexível para LocalDate que aceita:
 * - ISO_LOCAL_DATE              (ex: 2026-02-10)
 * - ISO_LOCAL_DATE_TIME         (ex: 2026-02-10T00:00:00)
 * - ISO_OFFSET_DATE_TIME        (ex: 2026-02-10T00:00:00-03:00)
 */
public class FlexibleLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        if (text == null) {
            return null;
        }

        text = text.trim();
        if (text.isEmpty()) {
            return null;
        }

        // Tenta LocalDate direto
        try {
            return LocalDate.parse(text);
        } catch (Exception ignored) {
        }

        // Tenta LocalDateTime e pega apenas a data
        try {
            LocalDateTime ldt = LocalDateTime.parse(text);
            return ldt.toLocalDate();
        } catch (Exception ignored) {
        }

        // Tenta OffsetDateTime e pega apenas a data
        try {
            OffsetDateTime odt = OffsetDateTime.parse(text);
            return odt.toLocalDate();
        } catch (Exception ignored) {
        }

        throw JsonMappingException.from(p,
                "Não foi possível converter valor '" + text + "' para LocalDate em FlexibleLocalDateDeserializer");
    }
}

