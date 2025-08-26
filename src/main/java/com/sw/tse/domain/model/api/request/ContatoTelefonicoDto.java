package com.sw.tse.domain.model.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sw.tse.domain.model.api.enums.TipoTelefone;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ContatoTelefonicoDto (
		//@JsonProperty("IdContatoTelefonico") Long idContatoTefonico,
	    @JsonProperty("IdTipo") TipoTelefone idTipo,
	    @JsonProperty("Ddi") Integer ddi,
	    @JsonProperty("Ddd") Integer ddd,
	    @JsonProperty("Numero") Integer numero,
	    @JsonProperty("Ramal") Integer ramal,
	    @JsonProperty("DescricaoObs") String descricaoObs,
	    @JsonProperty("ContemWhatsApp") boolean contemWhatsApp
	) {}
