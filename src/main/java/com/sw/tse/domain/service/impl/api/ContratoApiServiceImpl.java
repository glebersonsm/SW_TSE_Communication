package com.sw.tse.domain.service.impl.api;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.client.ContratoApiClient;
import com.sw.tse.core.util.CpfUtil;
import com.sw.tse.core.util.StringUtil;
import com.sw.tse.domain.expection.LoginInvalidoTseException;
import com.sw.tse.domain.model.api.response.ContratoPessoaApiResponse;
import com.sw.tse.domain.service.interfaces.ContratoService;
import com.sw.tse.domain.service.interfaces.TokenTseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(name = "database.enabled", havingValue = "false", matchIfMissing = true)
@Service
@Slf4j
@RequiredArgsConstructor
public class ContratoApiServiceImpl implements ContratoService{
	
	private final ContratoApiClient contratoApiClient;
	private final TokenTseService tokenTseService;

	@Override
	public List<ContratoPessoaApiResponse> buscarContratoPorCPF(String cpf) {
		
		String bearerToken = "Bearer " + tokenTseService.gerarToken();
		
		if(!CpfUtil.isValid(cpf)) {
			throw new LoginInvalidoTseException("CPF inválido");
		}
		
		String cpfLimpo = StringUtil.removeMascaraCpf(cpf);
		
		List<ContratoPessoaApiResponse> listaContrato = contratoApiClient.buscarContratoPorCpf(cpfLimpo, bearerToken);
		
		return listaContrato;
	}

}
