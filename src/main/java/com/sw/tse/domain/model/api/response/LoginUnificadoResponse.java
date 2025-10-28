package com.sw.tse.domain.model.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginUnificadoResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("IdOperador") Long idOperador,
    @JsonProperty("IdPessoa") Long idPessoa,
    @JsonProperty("NomeOperador") String nomeOperador,
    @JsonProperty("Login") String login,
    @JsonProperty("Email") String email,
    @JsonProperty("CPF") String cpf,
    @JsonProperty("NovoUsuarioCriado") boolean novoUsuarioCriado,
    @JsonProperty("EmailEnviadoSenha") String emailEnviadoSenha
) {}

