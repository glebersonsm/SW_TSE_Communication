package com.sw.tse.domain.service.impl.api;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.api.dto.OperadorSistemaRequestDto;
import com.sw.tse.client.LookupApiClient;
import com.sw.tse.client.OperadorSistemaApiClient;
import com.sw.tse.domain.expection.ApiTseException;
import com.sw.tse.domain.expection.BrasilApiException;
import com.sw.tse.domain.expection.OperadorSistemaNotFoundException;
import com.sw.tse.domain.model.api.request.OperadorSistemaApiRequest;
import com.sw.tse.domain.model.api.response.OperadorSistemaApiResponse;
import com.sw.tse.domain.model.api.response.OperadorSistemaListaApiResponse;
import com.sw.tse.domain.model.db.OperadorSistema;
import com.sw.tse.domain.service.interfaces.OperadorSistemaService;
import com.sw.tse.domain.service.interfaces.TokenTseService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(name = "database.enabled", havingValue = "false", matchIfMissing = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class OperadorSistemaApiServiceImpl implements OperadorSistemaService {
	

	private final LookupApiClient lookupApiClient;
	private final TokenTseService tokenTseService;
	private final OperadorSistemaApiClient operadorSistemaApiClient;

	@Override
	public List<OperadorSistemaListaApiResponse> listarTodos() {
		try {
			String bearerToken = "Bearer " + tokenTseService.gerarToken();
			return lookupApiClient.listarOperadoresSistema(bearerToken);
		} catch (FeignException e) {
            log.error("Erro ao consultar os tipos de endereços");
            if (e.status() == 401 || e.status() == 403) {
                throw new BrasilApiException(e.contentUTF8());
            }

            throw new BrasilApiException("Erro de comunicação de busca cep", e);
		}
	}

	@Override
	public OperadorSistemaListaApiResponse buscarPorId(Long idOperadorSistema) {
	
		return this.listarTodos()
				.stream().filter(operador -> operador.id().equals(idOperadorSistema))
				.findFirst().orElseThrow(() -> new OperadorSistemaNotFoundException(String.format("Não existe operador sistema com id %d", idOperadorSistema)));
		
	}

	@Override
	public OperadorSistema operadorSistemaPadraoCadastro() {
		throw new UnsupportedOperationException("Operação não suportada na implementação API");
	}

	@Override
	public OperadorSistemaApiResponse criarOperadorSistema(OperadorSistemaRequestDto requestDto) {
		log.info("Iniciando processo de criação de operador de sistema via API...");
		try {
			String bearerToken = "Bearer " + tokenTseService.gerarToken();
			

			if (requestDto.listaIdEmpresasPermitidas() == null || requestDto.listaIdEmpresasPermitidas().isEmpty()) {
				throw new IllegalArgumentException("É necessário fornecer ao menos uma empresa permitida.");
			}
			
			Long idEmpresaPrincipal = requestDto.listaIdEmpresasPermitidas().get(0);
			log.info("Setando empresa de sessão na API para o ID: {}", idEmpresaPrincipal);
			operadorSistemaApiClient.setEmpresaSessao(idEmpresaPrincipal, bearerToken, Collections.emptyMap());
			

			OperadorSistemaApiRequest apiRequest = new OperadorSistemaApiRequest(
				requestDto.login(),
				requestDto.tipoOperador(),
				requestDto.email(),
				requestDto.listaIdEmpresasPermitidas(),
				requestDto.idPessoaVincular()
			);
			
			log.info("Enviando requisição para criar operador com login: {}", apiRequest.login() );
			OperadorSistemaApiResponse response = operadorSistemaApiClient.criarOperadorSistema(bearerToken, apiRequest);
			
			log.info("Operador de sistema criado com sucesso via API. Novo ID: {}", response.idOperador());
			return response;
			
		} catch (ApiTseException e) {
			log.error("Falha de negócio ao criar operador: {}", e.getMessage());
			throw new ApiTseException(e.getMessage());
		} catch (FeignException e) {
            log.error("Erro de comunicação ao criar operador. Status: {}, Corpo: {}", e.status(), e.contentUTF8(), e);
            throw new ApiTseException("Erro de comunicação com a API de Operadores.", e);
        }
	}

}
