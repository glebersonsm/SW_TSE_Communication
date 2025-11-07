package com.sw.tse.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContaMovimentacaoBancariaDto {
    private Long id;
    private Long idEmpresa;
    private Long idBanco;
    private String nomeBanco;
    private String titularConta;
    private String agencia;
    private String digitoAgencia;
    private String numeroConta;
    private String digitoConta;
    private String tipoConta;
    private String operacao;
    private Boolean inativa;
}

