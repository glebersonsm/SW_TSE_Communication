package com.sw.tse.domain.service.impl.db;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.core.util.CpfUtil;
import com.sw.tse.core.util.StringUtil;
import com.sw.tse.domain.converter.ContratoConverter;
import com.sw.tse.domain.expection.LoginInvalidoTseException;
import com.sw.tse.domain.expection.PessoaSemContratoTseException;
import com.sw.tse.domain.model.api.response.ContratoPessoaApiResponse;
import com.sw.tse.domain.model.db.Contrato;
import com.sw.tse.domain.repository.ContratoRepository;
import com.sw.tse.domain.service.interfaces.ContratoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@ConditionalOnProperty(name = "database.enabled", havingValue = "true")
@Service
@Slf4j
@RequiredArgsConstructor
public class ContratoDbServiceImpl implements ContratoService {
	
	private final ContratoRepository contratoRepository;
	private final ContratoConverter contratoConverter;

	@Override
	public List<ContratoPessoaApiResponse> buscarContratoPorCPF(String cpf) {
		if(!CpfUtil.isValid(cpf)) {
			throw new LoginInvalidoTseException("CPF inválido");
		}
		
		String cpfLimpo = StringUtil.removeMascaraCpf(cpf);
		
		List<Contrato> listaContrato = contratoRepository.findByPessoaCpf(cpfLimpo);
		
		if(listaContrato.isEmpty()) {
			log.info("Pessoa com o cpf {} não tem contratos vinculados", cpfLimpo);
			throw new PessoaSemContratoTseException(String.format("Pessoa com o cpf %s não tem contratos vinculados", cpfLimpo));
		}
		
		return contratoConverter.toDtoList(listaContrato);
	}

	
	
}
