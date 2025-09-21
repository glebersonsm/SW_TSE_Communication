
package com.sw.tse.domain.model.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OperadorCriadoComEmailResponse(
    @JsonProperty("IdOperador") Long idOperador,
    @JsonProperty("NomeOperador") String nomeOperador,
    @JsonProperty("Login") String login,
    @JsonProperty("IdFuncionario") Long idFuncionario,
    @JsonProperty("Email") String email
) {
    

    public static OperadorCriadoComEmailResponse fromOperadorResponse(OperadorSistemaCriadoApiResponse operadorResponse, String email) {
        return new OperadorCriadoComEmailResponse(
            operadorResponse.idOperador(),
            operadorResponse.nomeOperador(),
            operadorResponse.login(),
            operadorResponse.idFuncionario(),
            email
        );
    }
}