package com.sw.tse.api.controller;

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

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class LoginController {
	
	private final LoginService loginService;
	
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

}
