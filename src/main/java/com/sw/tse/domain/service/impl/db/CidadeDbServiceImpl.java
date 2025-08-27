package com.sw.tse.domain.service.impl.db;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sw.tse.client.BuscaCepBrasilApiClient;
import com.sw.tse.core.util.StringUtil;
import com.sw.tse.domain.expection.ApiTseException;
import com.sw.tse.domain.expection.BrasilApiException;
import com.sw.tse.domain.expection.CidadeNotFoundException;
import com.sw.tse.domain.model.api.response.BuscaCepBrasilApiResponse;
import com.sw.tse.domain.model.api.response.CidadeDto;
import com.sw.tse.domain.model.db.Cidade;
import com.sw.tse.domain.repository.CidadeRepository;
import com.sw.tse.domain.service.interfaces.CidadeService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConditionalOnProperty(name = "database.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Service
public class CidadeDbServiceImpl implements CidadeService {
	
	private final BuscaCepBrasilApiClient brasilApiBuscaCep;
	private final CidadeRepository cidadeRepository;

	@Override
	public CidadeDto buscarPorCep(String cep) {
		BuscaCepBrasilApiResponse buscaCep = buscarCepApiBrasil(cep);
		
		CidadeDto cidadeDto = buscarCidadeTse(buscaCep);
		return cidadeDto;
	}
	
	public CidadeDto buscarCidadeTse(BuscaCepBrasilApiResponse buscaCep){
		Cidade cidade = cidadeRepository.findByNomeAndUfOrdenado(buscaCep.cidade(), buscaCep.uf())
				.stream().findFirst().orElseThrow(() -> new CidadeNotFoundException(String.format("Não encontrado a cidade %s para o estado %s", buscaCep.cidade(), buscaCep.uf())));
		

		return CidadeDto.builder()
				.idCidade(cidade.getId())
				.nome(cidade.getNome())
				.codigoIbege(cidade.getCodigoIbge())
				.uf(cidade.getUf())
				.build();
	}
	
	
	private BuscaCepBrasilApiResponse buscarCepApiBrasil(String cep) {
		
		if(!StringUtils.hasText(cep)) {
			log.info("Cep informado é inválido ou null");
			throw new BrasilApiException(String.format("O CEP %s é inválido", cep));
		}
		
		cep = StringUtil.removerMascaraCep(cep);
		
		if(cep.length() != 8) {
			throw new ApiTseException(String.format("O Cep % não possui exatamente 8 caracteres", cep));
		}
		
		try {
			BuscaCepBrasilApiResponse buscaCep = brasilApiBuscaCep.buscarPorCep(cep);
			return buscaCep;
		 } catch (FeignException e) {
	            log.error("Erro ao chmar a api de busca cep");
	            if (e.status() == 400 || e.status() == 404) {
	                throw new BrasilApiException(String.format("O CEP %s é inválido", cep));
	            }

	            throw new BrasilApiException("Erro de comunicação de busca cep", e);
	    }
	}

}
