package com.sw.tse.domain.model.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para mapear o resultado da consulta de períodos disponíveis
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoUtilizacaoDisponivel {

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
}
