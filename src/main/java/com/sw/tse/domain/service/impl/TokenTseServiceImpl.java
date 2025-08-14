package com.sw.tse.domain.service.impl;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.sw.tse.domain.model.api.CachedToken;
import com.sw.tse.domain.model.api.request.TokenApiResponse;
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
			
			this.cachedToken = solicitarNovoToken(userName, password);
	        log.info("Novo token de acesso obtido e armazenado no cache.");
	        return this.cachedToken.accessToken();
		}
	}
	
	
	private CachedToken solicitarNovoToken(String userName, String password) {
		
		this.webClient = webClientBuilder.baseUrl(urlToken).build();
		
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("username", userName);
        formData.add("password", password);

        try {
            TokenApiResponse response = webClient.post()
                    .uri("/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(TokenApiResponse.class)
                    .block();

            if (response == null || response.accessToken() == null) {
                throw new IllegalStateException("A resposta da API de token foi inválida.");
            }
            Instant expirationTime = Instant.now().plusSeconds(12 * 3600);
            
            return new CachedToken(response.accessToken(), expirationTime);

        } catch (WebClientResponseException e) {
            log.error("Erro HTTP ao solicitar novo token: Status {}, Corpo {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Não foi possível obter o token de acesso da API.", e);
        }
    }

}
