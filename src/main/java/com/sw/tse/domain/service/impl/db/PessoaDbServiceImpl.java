package com.sw.tse.domain.service.impl.db;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.controller.model.HospedeDto;
import com.sw.tse.domain.service.interfaces.PessoaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConditionalOnProperty(name = "database.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Service
public class PessoaDbServiceImpl implements PessoaService {@Override
	public Long salvar(HospedeDto hospedeDto) {
		// TODO Auto-generated method stub
		return null;
	}

}
