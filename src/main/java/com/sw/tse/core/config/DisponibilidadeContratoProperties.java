package com.sw.tse.core.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "sw.tse.disponibilidade")
@Data
public class DisponibilidadeContratoProperties {
    
    @Data
    public static class Bloqueio {
        private List<Long> idsTipoTag = new ArrayList<Long>();
        private List<String> sysidsGrupo = new ArrayList<String>();
    }
    
    @Data
    public static class Inadimplencia {
        private Boolean validarContrato = true;
        private Boolean validarCondominio = false;
    }
    
    private Bloqueio bloqueio = new Bloqueio();
    // Integralizacao removido - agora deve vir via request
    private Inadimplencia inadimplencia = new Inadimplencia();
}
