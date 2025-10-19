package com.sw.tse.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HospedeResponse {
    
    private Long idHospede;
    private String nome;
    private String sobrenome;
    private String cpf;
    private Boolean isPrincipal;
    private String faixaEtaria;
}

