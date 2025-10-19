package com.sw.tse.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.sw.tse.domain.expection.ValorPadraoNaoConfiguradoException;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "sw.tse.utilizacao")
public class UtilizacaoContratoPropertiesCustom {
    
    private Long idPensaoPadrao;
    private Integer idadeMinimaPagante;
    
    @PostConstruct
    public void validarConfiguracoes() {
        if (idPensaoPadrao == null) {
            throw new ValorPadraoNaoConfiguradoException(
                "Configuração obrigatória não encontrada: sw.tse.utilizacao.id.pensao-padrao"
            );
        }
        
        if (idadeMinimaPagante == null) {
            throw new ValorPadraoNaoConfiguradoException(
                "Configuração obrigatória não encontrada: sw.tse.utilizacao.idade-minima-pagante"
            );
        }
    }
}

