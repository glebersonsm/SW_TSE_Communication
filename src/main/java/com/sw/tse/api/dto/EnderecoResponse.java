package com.sw.tse.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EnderecoResponse {
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cep;
    private String cidade;
    private String uf;
}
