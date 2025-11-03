package com.sw.tse.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartaoVinculadoResponseDto {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("ultimosDigitos")
    private String ultimosDigitos;
    
    @JsonProperty("mesValidade")
    private String mesValidade;
    
    @JsonProperty("anoValidade")
    private String anoValidade;
    
    @JsonProperty("nomeNoCartao")
    private String nomeNoCartao;
    
    @JsonProperty("bandeira")
    private String bandeira;
}

