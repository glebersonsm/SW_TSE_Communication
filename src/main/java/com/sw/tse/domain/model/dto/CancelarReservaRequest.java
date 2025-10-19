package com.sw.tse.domain.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CancelarReservaRequest {
    
    @NotNull(message = "ID da utilizacao e obrigatorio")
    private Long idUtilizacaoContrato;
    
    @NotBlank(message = "Motivo do cancelamento e obrigatorio")
    @Size(min = 10, max = 500, message = "Motivo deve ter entre 10 e 500 caracteres")
    private String motivo;
}

