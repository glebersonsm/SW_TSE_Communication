package com.sw.tse.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DadosPessoaDto {
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private EnderecoDto endereco;
}

