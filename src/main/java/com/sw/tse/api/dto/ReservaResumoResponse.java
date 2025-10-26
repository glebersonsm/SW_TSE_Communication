package com.sw.tse.api.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservaResumoResponse {
    private Long idUtilizacaoContrato;
    private String tipoUtilizacao;
    private LocalDate checkin;
    private LocalDate checkout;
    private String descricaoPeriodo;
    private String status;
}
