package com.sw.tse.core.config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "sw.tse.disponibilidade")
@Data
public class DisponibilidadeContratoProperties {
    
    private Bloqueio bloqueio = new Bloqueio();
    private Integralizacao integralizacao = new Integralizacao();
    private Inadimplencia inadimplencia = new Inadimplencia();
    
    @Data
    public static class Bloqueio {
        private List<Long> idsTipoTag = new ArrayList<>();
        private List<String> sysidsGrupo = new ArrayList<>();
    }
    
    @Data
    public static class Integralizacao {
        private BigDecimal valorMinimo;
    }
    
    @Data
    public static class Inadimplencia {
        private Boolean validarContrato = true;
        private Boolean validarCondominio = false;
    }
}
