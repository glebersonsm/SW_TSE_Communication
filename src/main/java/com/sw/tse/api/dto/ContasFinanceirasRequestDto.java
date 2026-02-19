package com.sw.tse.api.dto;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para solicitação de listagem de contas financeiras com filtros")
public class ContasFinanceirasRequestDto {

    @Schema(description = "Data inicial de vencimento (formato: yyyy-MM-dd)", example = "2024-01-01")
    private LocalDate vencimentoInicial;

    @Schema(description = "Data final de vencimento (formato: yyyy-MM-dd)", example = "2024-12-31")
    private LocalDate vencimentoFinal;

    @Schema(description = "Status da conta: B (Paga), P (Em aberto), V (Vencida), T (Todas)", example = "P")
    private String status;

    @Schema(description = "ID da empresa para filtrar contas", example = "1")
    private Long empresaId;

    @Schema(description = "Lista de feriados (formato yyyy-MM-dd) para cálculo de juros.")
    private List<LocalDate> feriados;

    @Schema(description = "Número da página (inicia em 1)", defaultValue = "1")
    private Integer numeroDaPagina;

    @Schema(description = "Quantidade de registros por página", defaultValue = "30")
    private Integer quantidadeRegistrosRetornar;
}
