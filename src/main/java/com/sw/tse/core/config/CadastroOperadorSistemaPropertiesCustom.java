package com.sw.tse.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.sw.tse.domain.model.api.enums.TipoTelefone;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "sw.tse.cadastro")
public class CadastroOperadorSistemaPropertiesCustom {
	
	private Long operador;
	
}
