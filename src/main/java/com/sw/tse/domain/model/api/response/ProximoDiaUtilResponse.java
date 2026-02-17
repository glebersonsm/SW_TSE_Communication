package com.sw.tse.domain.model.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Resposta do endpoint GET /api/feriado/proximo-dia-util do Portal API.
 * O campo Data contém a data no formato yyyy-MM-dd.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProximoDiaUtilResponse {

    @JsonProperty("Data")
    private String data;
}
