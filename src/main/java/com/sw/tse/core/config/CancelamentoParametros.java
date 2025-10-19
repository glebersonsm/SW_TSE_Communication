package com.sw.tse.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.sw.tse.domain.expection.ValorPadraoNaoConfiguradoException;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "sw.tse.cancelamento")
@Getter
@Setter
public class CancelamentoParametros {
    
    private Boolean reservaPermitido;
    private Integer reservaDiasMinimos;
    private Boolean rciPermitido;
    private Integer rciDiasMinimos;
    private Boolean poolPermitido;
    private Integer poolDiasMinimos;
    
    @PostConstruct
    public void validarConfiguracao() {
        if (reservaPermitido == null) {
            throw new ValorPadraoNaoConfiguradoException(
                "Configuracao obrigatoria nao encontrada: sw.tse.cancelamento.reserva-permitido"
            );
        }
        if (reservaDiasMinimos == null) {
            throw new ValorPadraoNaoConfiguradoException(
                "Configuracao obrigatoria nao encontrada: sw.tse.cancelamento.reserva-dias-minimos"
            );
        }
        if (rciPermitido == null) {
            throw new ValorPadraoNaoConfiguradoException(
                "Configuracao obrigatoria nao encontrada: sw.tse.cancelamento.rci-permitido"
            );
        }
        if (rciDiasMinimos == null) {
            throw new ValorPadraoNaoConfiguradoException(
                "Configuracao obrigatoria nao encontrada: sw.tse.cancelamento.rci-dias-minimos"
            );
        }
        if (poolPermitido == null) {
            throw new ValorPadraoNaoConfiguradoException(
                "Configuracao obrigatoria nao encontrada: sw.tse.cancelamento.pool-permitido"
            );
        }
        if (poolDiasMinimos == null) {
            throw new ValorPadraoNaoConfiguradoException(
                "Configuracao obrigatoria nao encontrada: sw.tse.cancelamento.pool-dias-minimos"
            );
        }
    }
}
