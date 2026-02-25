package com.sw.tse.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sw.tse.api.jackson.FlexibleLocalDateDeserializer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContaFinanceiraParaPagamentoDto {
    private Long idContaFinanceiraTse;
    private BigDecimal valor;

    @JsonDeserialize(using = FlexibleLocalDateDeserializer.class)
    private LocalDate dataVencimento;

    private BigDecimal valorJuros; // Juros calculado em tela para esta conta
    private BigDecimal valorMulta; // Multa calculada em tela para esta conta
}


