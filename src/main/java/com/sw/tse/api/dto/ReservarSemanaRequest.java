package com.sw.tse.api.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ReservarSemanaRequest {
    
    // Para edição: informar o ID da utilização existente
    // Para criação: deixar null ou zero
    private Long idUtilizacaoContrato;
    
    @NotNull(message = "ID do contrato é obrigatório")
    private Long idContrato;
    
    @NotNull(message = "ID do período de utilização é obrigatório")
    private Long idPeriodoUtilizacao;
    
    @NotNull(message = "Tipo de utilização é obrigatório")
    @Pattern(regexp = "RESERVA|RCI|POOL", message = "Tipo deve ser RESERVA, RCI ou POOL")
    private String tipoUtilizacao;
    
    // Para RESERVA: obrigatório (HospedeDto com dados completos)
    // Para RCI: null ou vazio (hóspedes virão depois da RCI)
    // Para edição: idHospede preenchido = atualizar, null = criar novo
    @Valid
    private List<HospedeDto> hospedes;
    
    // ===== Parâmetros de Validação de Integralização (opcional) =====
    // Se não informado, a validação de integralização será pulada
    
    /**
     * Tipo de validação de integralização: "FIXO" ou "PERCENTUAL"
     * Se null, a validação de integralização será pulada
     */
    private String tipoValidacaoIntegralizacao;
    
    /**
     * Valor de integralização - interpretado conforme tipoValidacaoIntegralizacao:
     * - Se tipoValidacaoIntegralizacao = "FIXO": valor mínimo em reais (ex: 2300.00)
     * - Se tipoValidacaoIntegralizacao = "PERCENTUAL": percentual (ex: 10.5 para 10.5%)
     *   O valor mínimo será calculado como: valorNegociadoContrato * (valorIntegralizacao / 100)
     * Se null, a validação de integralização será pulada
     */
    private BigDecimal valorIntegralizacao;

    /**
     * Canal de atendimento (ex: "Portal Cliente" quando a utilização é criada pelo portal).
     */
    private String canalAtendimento;
}

