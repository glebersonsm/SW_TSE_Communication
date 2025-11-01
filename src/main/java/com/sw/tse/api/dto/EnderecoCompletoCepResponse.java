package com.sw.tse.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoCompletoCepResponse {
    private String cep;
    private String logradouro;
    private String bairro;
    private String cidade;
    private String uf;
}

