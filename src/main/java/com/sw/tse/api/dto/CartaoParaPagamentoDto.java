package com.sw.tse.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartaoParaPagamentoDto {
    
    @JsonProperty("numeroCartao")
    private String numeroCartao;
    
    @JsonProperty("codigoSeguranca")
    private String codigoSeguranca;
    
    @JsonProperty("mesValidade")
    private String mesValidade;
    
    @JsonProperty("anoValidade")
    private String anoValidade;
    
    @JsonProperty("nomeNoCartao")
    private String nomeNoCartao;
    
    @JsonProperty("idBandeirasAceitas")
    private Integer idBandeirasAceitas;
}

