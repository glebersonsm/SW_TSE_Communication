package com.sw.tse.domain.model.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sw.tse.domain.model.api.enums.TipoValorRelatorioCustomizado;

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
public class FiltroRelatorioCustomizadoApiRequest {

	@JsonProperty("NomeParametro") private String nomeParametro;
	@JsonProperty("Value") private String valor;
	@JsonProperty("Type") private TipoValorRelatorioCustomizado tipo;
	@JsonProperty("Criptografar") private boolean criptografar;
}
