package com.sw.tse.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtilizacaoContratoCompletaDto {

    private Long idUtilizacaoContrato;
    private Long idContrato;
    private String numeroContrato;
    private Long idPeriodoUtilizacao;
    private Long idPeriodoModeloCota;
    private String tipoUtilizacao;
    private LocalDate dataInicial;
    private LocalDate dataFinal;
    private String descricaoPeriodo;
    private String empresa;
    private String nomeHotel;
    private Integer capacidade;
    private String status;
}
