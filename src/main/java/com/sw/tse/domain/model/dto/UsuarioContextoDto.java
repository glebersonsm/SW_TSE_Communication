package com.sw.tse.domain.model.dto;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO com o contexto agregado do usuário para filtragem de tags de visualização.
 * Usado pelo Portal do Proprietário para decidir quais imagens/documentos o usuário pode ver.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioContextoDto {

    @JsonProperty("idPessoa")
    private Long idPessoa;

    /**
     * true se TODOS os contratos estão adimplentes (nenhum tem inadimplência).
     */
    @JsonProperty("temContratoAdimplente")
    private Boolean temContratoAdimplente;

    /**
     * true se pelo menos UM contrato está inadimplente (contrato ou condomínio).
     */
    @JsonProperty("temContratoInadimplente")
    private Boolean temContratoInadimplente;

    /**
     * IDs de grupo cota dos contratos do usuário (idCotaUh quando não nulo).
     */
    @JsonProperty("idsGrupoCota")
    @Builder.Default
    private List<Long> idsGrupoCota = Collections.emptyList();

    /**
     * IDs de empresa dos contratos do usuário.
     */
    @JsonProperty("idsEmpresa")
    @Builder.Default
    private List<Long> idsEmpresa = Collections.emptyList();

    /**
     * Data do próximo checkin mais próximo entre todos os contratos.
     */
    @JsonProperty("proximoCheckin")
    private LocalDate proximoCheckin;

    /**
     * Período de abertura de calendário, se aplicável (para uso futuro).
     */
    @JsonProperty("periodoAberturaCalendario")
    private LocalDate periodoAberturaCalendario;
}
