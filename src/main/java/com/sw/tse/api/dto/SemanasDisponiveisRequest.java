package com.sw.tse.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SemanasDisponiveisRequest {

    @NotNull(message = "ID do contrato é obrigatório")
    private Long idcontrato;

    private Integer ano;
}
