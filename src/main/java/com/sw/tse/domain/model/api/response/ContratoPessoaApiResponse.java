package com.sw.tse.domain.model.api.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ContratoPessoaApiResponse(
	    @JsonProperty("IdContrato") Long idContrato,
	    @JsonProperty("NumeroContrato") String numeroContrato,
	    @JsonProperty("Status") String status,
	    @JsonProperty("ValorNegociado") BigDecimal valorContrato,
	    @JsonProperty("IdEmpresa") Long idEmpresa,
	    @JsonProperty("SiglaEmpresa") String siglaEmpresa
) {}
