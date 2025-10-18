package com.sw.tse.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservarSemanaRequest {
    
    @NotNull(message = "ID do contrato é obrigatório")
    private Long idContrato;
    
    @NotNull(message = "ID do período de utilização é obrigatório")
    private Long idPeriodoUtilizacao;
}

