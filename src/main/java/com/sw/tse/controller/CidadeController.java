package com.sw.tse.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.domain.model.api.response.CidadeDto;
import com.sw.tse.domain.service.interfaces.CidadeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cidade")
@RequiredArgsConstructor
public class CidadeController {

	private final CidadeService cidadeService;
	
	@GetMapping("/{cep}")
	public CidadeDto buscarPorCep(@PathVariable String cep) {
		CidadeDto cidadeDto = cidadeService.buscarPorCep(cep);
		return cidadeDto;
	}
}
