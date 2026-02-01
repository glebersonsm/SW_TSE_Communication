package com.sw.tse.api.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.api.dto.ApiResponseDto;
import com.sw.tse.api.dto.LoginClienteDto;
import com.sw.tse.domain.model.api.response.LoginResponse;
import com.sw.tse.domain.model.api.response.LoginUnificadoResponse;
import com.sw.tse.domain.service.interfaces.LoginService;
import com.sw.tse.security.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class LoginController {
	
	private final LoginService loginService;
	private final JwtService jwtService;
	
	@Value("${api.tse.username:}")
	private String apiTseUsername;
	@Value("${api.tse.password:}")
	private String apiTsePassword;
	@Value("${sw.tse.cadastro.operador:44}")
	private Long operadorPadraoId;
	
	@PostMapping("/logar")
	public ResponseEntity<ApiResponseDto<LoginUnificadoResponse>> logar(@RequestBody LoginClienteDto loginRequest){
		
		LoginResponse loginResponse = loginService.logarOperadorCliente(loginRequest.login(), loginRequest.password());
		
		// Converte para resposta unificada
		LoginUnificadoResponse responseData = loginResponse.toLoginUnificado();
		
		String message = loginResponse.isNovoOperadorCriado() 
			? "Operador criado com sucesso. Senha enviada para o email cadastrado." 
			: "Login realizado com sucesso";
		
        ApiResponseDto<LoginUnificadoResponse> responseApi = new ApiResponseDto<>(
            HttpStatus.OK.value(),
            true,
            responseData,
            message
        );
		
        return ResponseEntity.ok(responseApi);
	}
	
	/**
	 * Gera JWT para autenticação de serviço (jobs de background).
	 * Valida login/senha contra api.tse.username/password (credenciais padrão TSE).
	 * Retorna JWT assinado com idUsuarioCliente = operador padrão (aceito pelo processar-aprovado).
	 */
	@PostMapping("/token-servico")
	public ResponseEntity<ApiResponseDto<Map<String, String>>> tokenServico(@RequestBody LoginClienteDto loginRequest) {
		if (apiTseUsername == null || apiTseUsername.isBlank() || apiTsePassword == null) {
			log.warn("token-servico: api.tse.username ou api.tse.password não configurados");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ApiResponseDto<>(401, false, null, "Endpoint de serviço não configurado"));
		}
		if (!apiTseUsername.equals(loginRequest.login()) || !apiTsePassword.equals(loginRequest.password())) {
			log.warn("token-servico: credenciais inválidas para login={}", loginRequest.login());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ApiResponseDto<>(401, false, null, "Credenciais inválidas"));
		}
		String jwt = jwtService.gerarTokenServico(operadorPadraoId);
		Map<String, String> data = Map.of("access_token", jwt);
		log.info("token-servico: JWT gerado para serviço (operador padrão {})", operadorPadraoId);
		return ResponseEntity.ok(new ApiResponseDto<>(200, true, data, "Token obtido"));
	}

}
