package com.sw.tse.api.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HospedeResponse {
    
    private Long idHospede;
    private String nome;
    private String sobrenome;
    private String cpf;
    private LocalDate dataNascimento;
    private String sexo;
    private Boolean isPrincipal;
    private String faixaEtaria;
    
    // Campos adicionais para hóspede principal
    private EnderecoResponse endereco;
    private String email;
    private TelefoneResponse telefone;
    private Boolean isProprietario; // Indica se o hóspede principal é proprietário do contrato
}

