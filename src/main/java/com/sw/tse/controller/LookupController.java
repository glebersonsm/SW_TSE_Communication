package com.sw.tse.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.domain.model.api.response.TipoDocumentoPessoaDto;
import com.sw.tse.domain.service.interfaces.TipoDocumentoPessoaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/lookup")
@RequiredArgsConstructor
public class LookupController {

	private final TipoDocumentoPessoaService tipoDocumentoPessoaService;
	

	@GetMapping("TiposDocumentoPessoa")
	public List<TipoDocumentoPessoaDto> listarTiposDocumento(){
		return tipoDocumentoPessoaService.listarTiposDocumento();
	}
	
}
