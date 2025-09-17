package com.sw.tse.domain.service.impl.api;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.client.LookupApiClient;
import com.sw.tse.domain.expection.BrasilApiException;
import com.sw.tse.domain.model.api.response.TipoEnderecoApiResponse;
import com.sw.tse.domain.service.interfaces.TipoEnderecoService;
import com.sw.tse.domain.service.interfaces.TokenTseService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(name = "database.enabled", havingValue = "false", matchIfMissing = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class TipoEnderecoApiServiceImpl implements TipoEnderecoService {
	

	private final LookupApiClient lookupApiClient;
	private final TokenTseService tokenTseService;

	@Override
	public List<TipoEnderecoApiResponse> listarTiposEndereco() {
		
		String bearerToken = "Bearer " + tokenTseService.gerarToken();
		try {
			return lookupApiClient.listarTiposEndereco(bearerToken);
		} catch (FeignException e) {
            log.error("Erro ao consultar os tipos de endereços");
            if (e.status() == 401 || e.status() == 403) {
                throw new BrasilApiException(e.contentUTF8());
            }

            throw new BrasilApiException("Erro de comunicação de busca cep", e);
		}
	}

}
