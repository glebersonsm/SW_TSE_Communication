package com.sw.tse.domain.model.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidacaoDisponibilidadeParametros {
    
    /**
     * IDs dos tipos de tag que bloqueiam reserva (opcional)
     * Ex: [3L, 10L, 12L]
     */
    private List<Long> idsTipoTagBloqueio;
    
    /**
     * SysIds dos grupos que bloqueiam reserva (opcional)
     * Ex: ["CONTRATOTS_TIPOTAG_GRUPOTAG_PENDENTEUTILIZACAO"]
     */
    private List<String> sysIdsGrupoBloqueio;
    
    /**
     * Valor mínimo que o contrato deve ter integralizado (opcional)
     */
    private BigDecimal valorMinimoIntegralizacao;
    
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
