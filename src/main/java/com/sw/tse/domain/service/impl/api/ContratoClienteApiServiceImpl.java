package com.sw.tse.domain.service.impl.api;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.client.ContratoClienteApiClient;
import com.sw.tse.domain.model.api.dto.ContratoClienteApiResponse;
import com.sw.tse.domain.service.interfaces.ContratoClienteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(name = "database.enabled", havingValue = "false", matchIfMissing = true)
@Service
@Slf4j
@RequiredArgsConstructor
public class ContratoClienteApiServiceImpl implements ContratoClienteService{
	
	private final ContratoClienteApiClient contratoClienteApiClient;
	
	@Override
	public List<ContratoClienteApiResponse> buscarContratosPorToken(String token) {
		return contratoClienteApiClient.buscarMeusContratos(token);
	}

	@Override
	public List<ContratoClienteApiResponse> buscarContratosPorIdUsuario(Long idUsuario) {
		return null;
	}
	
	

}
