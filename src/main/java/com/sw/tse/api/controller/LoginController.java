package com.sw.tse.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.api.dto.ApiResponseDto;
import com.sw.tse.api.dto.LoginClienteDto;
import com.sw.tse.domain.model.api.response.TokenApiResponse;
import com.sw.tse.domain.service.interfaces.LoginService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class LoginController {
	
	private final LoginService loginService;
	
	@PostMapping("/logar")
	public ResponseEntity<ApiResponseDto<TokenApiResponse>> logar(@RequestBody LoginClienteDto loginRequest){
		
		TokenApiResponse loginResponse =loginService.logar(loginRequest.login(), loginRequest.password());
		
        ApiResponseDto<TokenApiResponse> responseApi = new ApiResponseDto<>(
            HttpStatus.OK.value(),
            true,
            loginResponse,
            "Login realizado com sucesso"
        );
		
        return ResponseEntity.ok(responseApi);
	}

}
