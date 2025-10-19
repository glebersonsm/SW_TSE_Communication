package com.sw.tse.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.sw.tse.domain.expection.ValorPadraoNaoConfiguradoException;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "sw.tse.periodo")
@Getter
@Setter
public class PeriodoUtilizacaoParametros {

    private Integer antecedenciaMinimaDias;
    
    private Integer rciDiasMinimos;
    
    private Integer poolDiaLimite;
    
    private Integer poolMesLimite;
    
    private Long intercambiadoraRciId;
    
    @PostConstruct
    public void validarConfiguracao() {

        if (antecedenciaMinimaDias == null) {
            throw new ValorPadraoNaoConfiguradoException(
                "Configuracao obrigatoria nao encontrada: sw.tse.periodo.antecedencia.minima.dias"
            );
        }
        if (rciDiasMinimos == null) {
            throw new ValorPadraoNaoConfiguradoException(
                "Configuracao obrigatoria nao encontrada: sw.tse.periodo.rci.dias.minimos"
            );
        }
        if (poolDiaLimite == null) {
            throw new ValorPadraoNaoConfiguradoException(
                "Configuracao obrigatoria nao encontrada: sw.tse.periodo.pool.dia.limite"
            );
        }
        if (poolMesLimite == null) {
            throw new ValorPadraoNaoConfiguradoException(
                "Configuracao obrigatoria nao encontrada: sw.tse.periodo.pool.mes.limite"
            );
        }
        if (intercambiadoraRciId == null) {
            throw new ValorPadraoNaoConfiguradoException(
                "Configuracao obrigatoria nao encontrada: sw.tse.periodo.intercambiadora.rci.id"
            );
        }
    }
}
