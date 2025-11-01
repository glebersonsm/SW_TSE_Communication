package com.sw.tse.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PessoaComProprietarioResponse {
    
    private Long idPessoa;
    private String nome;
    private String sobrenome;
    private String cpf;
    private String dataNascimento;
    private String sexo;
    private String email;
    private TelefoneResponse telefone;
    private EnderecoResponse endereco;
    private Boolean isProprietario;
}

