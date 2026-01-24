package com.sw.tse.api.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoucherReservaResponse {

    private Long idUtilizacaoContrato;
    private Long idContrato;
    private String numeroContrato;
    private String tipoUtilizacao;
    private String descricaoPeriodo;
    private LocalDate checkin;
    private LocalDate checkout;
    private Integer capacidade;
    private String empresa;
    private Long idEmpresa;

    private String nomeCessionario;
    private String cpfCessionario;
    private String nomeCocessionario;
    private String cpfCocessionario;

    private List<HospedeResponse> hospedes;
}

