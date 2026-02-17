package com.sw.tse.api.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EscolhaPeriodoModeloCotaDto {
    private Long id;
    private Integer ano;
    private Integer mes;
    private Boolean ativo;
    private Long idModeloCota;
    private LocalDateTime inicioPeriodo;
    private LocalDateTime fimPeriodo;
}
