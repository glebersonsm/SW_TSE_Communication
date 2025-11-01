package com.sw.tse.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.sw.tse.domain.expection.ValorPadraoNaoConfiguradoException;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "sw.tse.edicao")
@Getter
@Setter
public class EdicaoParametros {
    
    private Boolean rciPermitido;
    private Boolean poolPermitido;
    
    @PostConstruct
    public void validarConfiguracao() {
        if (rciPermitido == null) {
            throw new ValorPadraoNaoConfiguradoException(
                "Configuracao obrigatoria nao encontrada: sw.tse.edicao.rci-permitido"
            );
        }
        if (poolPermitido == null) {
            throw new ValorPadraoNaoConfiguradoException(
                "Configuracao obrigatoria nao encontrada: sw.tse.edicao.pool-permitido"
            );
        }
    }
}

