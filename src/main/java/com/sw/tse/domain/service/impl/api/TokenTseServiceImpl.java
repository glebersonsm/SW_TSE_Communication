package com.sw.tse.domain.service.impl.api;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.tse.domain.expection.ApiTseException;
import com.sw.tse.domain.model.api.CachedToken;
import com.sw.tse.domain.model.api.response.TokenApiResponse;
import com.sw.tse.domain.service.interfaces.TokenTseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenTseServiceImpl implements TokenTseService{

	private WebClient webClient;
	private final WebClient.Builder webClientBuilder;
	private final Object tokenLock = new Object();
	private final ObjectMapper objectMapper;
	
	@Value("${api.tse.url}")
	private String urlToken;
	@Value("${api.tse.username}")
	private String userName;
	@Value("${api.tse.password}")
	private String password;
	
	private volatile CachedToken cachedToken;
	
	@Override
	public String gerarToken() {
		
		if (cachedToken != null && cachedToken.isValido()) {
            log.debug("Retornando token de acesso do cache.");
            return cachedToken.accessToken();
        }
		
		synchronized (tokenLock) {
			if (cachedToken != null && cachedToken.isValido()) {
                return cachedToken.accessToken();
            }
			
			log.info("Cache de token expirado ou inválido. Solicitando um novo token...");
			
			TokenApiResponse response = this.logar(userName, password);
			
			if(response.isError() || response.accessToken() == null) {
				if(response.erro().equals("invalid_grant")) {
					throw new ApiTseException("Usuário ou senha inválidos");
				}
				throw new ApiTseException(response.erro());
			}
			
			Instant expirationTime = Instant.now().plusSeconds(12 * 3600);
			
			this.cachedToken = new CachedToken(response.accessToken(), expirationTime);
			
			log.info("Novo token de acesso interno obtido e armazenado no cache.");
			
	        return this.cachedToken.accessToken();
		}
	}
	
	
	@Override
	public TokenApiResponse gerarTokenClient(String clientUserName, String clientPassword) {
		log.info("Solicitando token para o cliente {}", clientUserName);	
		return this.logar(clientUserName, clientPassword);
	}
	
	
	private TokenApiResponse logar(String username, String password) {
		
		this.webClient = webClientBuilder.baseUrl(urlToken).build();
		
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("username", username);
        formData.add("password", password);

        try {
            return webClient.post()
                    .uri("/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(TokenApiResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.warn("Erro HTTP ao solicitar token para o usuário '{}'. Status: {}", username, e.getStatusCode());
            return parseErrorResponse(e);
        } catch (Exception e) {
            log.error("Erro de comunicação inesperado ao solicitar token para '{}'", username, e);
            return new TokenApiResponse(null, 0, null, null, e.getMessage(), e.getMessage());
        }
    }
	
    private TokenApiResponse parseErrorResponse(WebClientResponseException e) {
        try {
            return objectMapper.readValue(e.getResponseBodyAsString(), TokenApiResponse.class);
        } catch (JsonProcessingException jsonEx) {
            log.error("Não foi possível desserializar o corpo do erro HTTP: {}", e.getResponseBodyAsString());
            String errorDescription = "Erro inesperado do servidor de autenticação. Status: " + e.getStatusCode().value();
            return new TokenApiResponse(null, 0, null, null, e.getMessage(), errorDescription);
        }
    }
}
