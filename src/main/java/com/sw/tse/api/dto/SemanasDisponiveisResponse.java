package com.sw.tse.api.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemanasDisponiveisResponse {

    private Long idPeriodoUtilizacao;
    private String descricaoPeriodo;
    private LocalDate checkin;
    private LocalDate checkout;
    private Long idTipoPeriodoUtilizacao;
    private String descricaoTipoPeriodo;
    private Integer ano;
    private Integer reserva;
    private Integer rci;
    private Integer pool;
    private Integer capacidade;
}
