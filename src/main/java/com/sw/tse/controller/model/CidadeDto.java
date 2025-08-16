package com.sw.tse.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CidadeDto {
	@JsonProperty("idcidade") private Long idCidade;
	@JsonProperty("nome") private String nome;
	@JsonProperty("codigoibge") private String codigoIbege;
	@JsonProperty("idcountrystate") private String idEstado;
	@JsonProperty("uf") private String uf;
	@JsonProperty("idcountry") private Long idPais;
}
