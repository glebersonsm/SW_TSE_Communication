package com.sw.tse.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.domain.service.interfaces.TokenTseService;

import lombok.RequiredArgsConstructor;


@RequestMapping("/api/v1/tokentse")
@RestController
@RequiredArgsConstructor
public class TokenTseController {

	private final TokenTseService tokenService;
	
	@GetMapping()
	public String gerarToken() {
		return tokenService.gerarToken();
	}
	
}
