package com.sw.tse.api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservaSemanaResponse {
    
    private Long idUtilizacaoContrato;
    private Long idPeriodoModeloCota;
    private String numeroContrato;
    private String tipoUtilizacao;
    private LocalDate checkin;
    private LocalDate checkout;
    private String descricaoPeriodo;
    private String status;
    private LocalDateTime dataCriacao;
    
    // Hóspedes (apenas se houver)
    private List<HospedeResponse> hospedes;
}

