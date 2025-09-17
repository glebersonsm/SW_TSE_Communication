package com.sw.tse.domain.model.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EnderecoPessoaApiRequest(
		@JsonProperty("IdEndereco") Long idEndereco,
	    @JsonProperty("DescricaoEndereco") String descricaoEndereco,
	    @JsonProperty("IdTipoEndereco") Long idTipoEndereco,
	    @JsonProperty("TipoEndereco") String tipoEndereco,
	    @JsonProperty("IdTipoLogradouro") Integer idTipoLogradouro,
	    @JsonProperty("TipoLogradouro") String tipoLogradouro,
	    @JsonProperty("Logradouro") String logradouro,
	    @JsonProperty("Complemento") String complemento,
	    @JsonProperty("Bairro") String bairro,
	    @JsonProperty("Numero") String numero,
	    @JsonProperty("IdPais") Integer idPais,
	    @JsonProperty("IdCidade") Integer idCidade,
	    @JsonProperty("Cidade") String cidade,
	    @JsonProperty("IdUf") Integer idUf,
	    @JsonProperty("Cep") Integer cep,
	    @JsonProperty("UsarCorrespondencia") boolean usarCorrespondencia) {}
