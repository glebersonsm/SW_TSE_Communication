package com.sw.tse.api.dto;

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
}

