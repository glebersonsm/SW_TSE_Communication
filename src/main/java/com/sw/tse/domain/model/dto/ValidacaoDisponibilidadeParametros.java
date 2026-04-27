package com.sw.tse.domain.model.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidacaoDisponibilidadeParametros {
    
    /**
     * IDs dos grupos de tags que bloqueiam reserva (opcional)
     * Corresponde ao idgrupotag na tabela contratotstipotaggrupotag (idconvencaosistema)
     */
    private List<Long> idsGrupoTagBloqueio;
    
    /**
     * Tipo de validação de integralização: "FIXO" ou "PERCENTUAL" (opcional)
     * Se null, a validação de integralização será pulada
     */
    private String tipoValidacaoIntegralizacao;
    
    /**
     * Valor de integralização - interpretado conforme tipoValidacaoIntegralizacao:
     * - Se tipoValidacaoIntegralizacao = "FIXO": valor mínimo em reais
     * - Se tipoValidacaoIntegralizacao = "PERCENTUAL": percentual (ex: 10.5 para 10.5%)
     * Se null, a validação de integralização será pulada
     */
    private BigDecimal valorIntegralizacao;
    
    /**
     * Se deve validar inadimplência de contrato (default: true)
     */
    @Builder.Default
    private Boolean validarInadimplencia = true;
    
    /**
     * Se deve validar inadimplência de condomínio (default: false)
     */
    @Builder.Default
    private Boolean validarInadimplenciaCondominio = false;
}
