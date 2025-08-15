package com.sw.tse.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.controller.model.HospedeDto;
import com.sw.tse.domain.service.interfaces.PessoaService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1/pessoa")
@RestController
@RequiredArgsConstructor
public class PessoaController {

	private final PessoaService pessoaService;
	
	@PostMapping
	public Long salvar(@RequestBody HospedeDto request) {
		return pessoaService.salvar(request);
	}
}
