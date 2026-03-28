package com.sw.tse.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BandeiraCartaoDto {
    private Integer id;
    private String bandeira;
    private String operacao;
    private Double taxaOperacao;
    private String nomeEstabelecimento;
    private Integer idBandeirasAceitas;
}
