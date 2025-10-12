package com.sw.tse.domain.service.impl.api;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.client.ContratoClienteApiClient;
import com.sw.tse.domain.expection.TokenJwtInvalidoException;
import com.sw.tse.domain.model.api.dto.ContratoClienteApiResponse;
import com.sw.tse.domain.service.interfaces.ContratoClienteService;
import com.sw.tse.security.JwtTokenUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(name = "database.enabled", havingValue = "false", matchIfMissing = true)
@Service
@Slf4j
@RequiredArgsConstructor
public class ContratoClienteApiServiceImpl implements ContratoClienteService{
	
	private final ContratoClienteApiClient contratoClienteApiClient;

	@Override
	public List<ContratoClienteApiResponse> buscarContratosCliente() {
		String tokenCliente = JwtTokenUtil.getTokenUsuarioCliente();
		
		if (tokenCliente == null) {
			log.error("Token do cliente não encontrado no contexto JWT");
			throw new TokenJwtInvalidoException("Token do cliente não está disponível no contexto de autenticação");
		}
		
		log.info("Buscando contratos usando token do JWT");
		return contratoClienteApiClient.buscarMeusContratos(tokenCliente);
	}

	@Override
	public List<ContratoClienteApiResponse> buscarContratosPorIdUsuario(Long idUsuario) {
		return null;
	}
	
	

}
