package com.sw.tse.domain.service.impl.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.tse.client.RelatorioCustomizadoApiClient;
import com.sw.tse.domain.expection.ApiTseException;
import com.sw.tse.domain.model.api.request.FiltroRelatorioCustomizadoApiRequest;
import com.sw.tse.domain.service.interfaces.RelatorioCustomizadoService;
import com.sw.tse.domain.service.interfaces.TokenTseService;

import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class RelatorioCustomizadoApiService implements RelatorioCustomizadoService {
	
	private final RelatorioCustomizadoApiClient relatorioApiClient;
	private final TokenTseService tokenTseService;
	private final ObjectMapper objectMapper;

	@Override
	public <T> List<T> buscarRelatorioGenerico(Long idRelatorio, List<FiltroRelatorioCustomizadoApiRequest> filtros,
			Class<T> tipoDoElemento) {
		String bearerToken = "Bearer " + tokenTseService.gerarToken();
		
		try {
			Response response = relatorioApiClient.obterDadosRelatorioRaw(idRelatorio, bearerToken, filtros);
			
			if (response.status() < 200 || response.status() > 299) {
				String responseBody = readResponseBody(response);
				log.error("API TSE - Response erro ao gerar relatorio customizado. idRelatorio={}, status={}, response={}",
						idRelatorio, response.status(), responseBody != null ? responseBody : "(vazio)");
				throw new ApiTseException(String.format("Erro ao gerar relatorio customizado com o id %d (HTTP %d). %s",
						idRelatorio, response.status(), responseBody != null && !responseBody.isBlank() ? responseBody : "Resposta da API sem detalhes."));
			}

			String responseBody = readResponseBody(response);
			log.debug("API TSE - Response sucesso ao gerar relatorio customizado. idRelatorio={}, status={}, response={}",
					idRelatorio, response.status(), responseBody != null ? responseBody : "(vazio)");
			JavaType tipoLista = objectMapper.getTypeFactory().constructCollectionType(List.class, tipoDoElemento);
			return objectMapper.readValue(responseBody != null ? responseBody : "[]", tipoLista);
			
		} catch (IOException e) {
			throw new ApiTseException("Falha ao desserializar a resposta do relatório.", e);
		}
	}

	private String readResponseBody(Response response) {
		if (response.body() == null) return null;
		try {
			return new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			log.warn("Não foi possível ler o body da resposta da API TSE: {}", e.getMessage());
			return null;
		}
	}

}
