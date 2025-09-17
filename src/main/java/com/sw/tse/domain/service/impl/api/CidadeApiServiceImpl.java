package com.sw.tse.domain.service.impl.api;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sw.tse.client.BuscaCepBrasilApiClient;
import com.sw.tse.client.RelatorioCustomizadoApiClient;
import com.sw.tse.core.util.StringUtil;
import com.sw.tse.domain.expection.ApiTseException;
import com.sw.tse.domain.expection.BrasilApiException;
import com.sw.tse.domain.model.api.enums.TipoValorRelatorioCustomizado;
import com.sw.tse.domain.model.api.request.FiltroRelatorioCustomizadoApiRequest;
import com.sw.tse.domain.model.api.response.BuscaCepBrasilApiResponse;
import com.sw.tse.domain.model.api.response.CidadeApiResponse;
import com.sw.tse.domain.service.interfaces.CidadeService;
import com.sw.tse.domain.service.interfaces.TokenTseService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@ConditionalOnProperty(name = "database.enabled", havingValue = "false", matchIfMissing = true)
public class CidadeApiServiceImpl implements CidadeService{

	
	private final BuscaCepBrasilApiClient brasilApiBuscaCep;
	private final RelatorioCustomizadoApiService relatorioCustomizado;
	
	 @Value("${api.tse.relatorios.cidade}")
	 private Long idRelatorioCidade;

	@Override
	public CidadeApiResponse buscarPorCep(String cep) {
		
		BuscaCepBrasilApiResponse buscaCep = buscarCepApiBrasil(cep);
		
		CidadeApiResponse cidadeDto = buscarCidadeTse(buscaCep);
		
		return cidadeDto;
	}

	private CidadeApiResponse buscarCidadeTse(BuscaCepBrasilApiResponse buscaCep) {

		if(idRelatorioCidade == null || idRelatorioCidade.equals(0L)) {
			throw new ApiTseException("Relatório customizado pra cidade não parametrizao");
		}
		
		FiltroRelatorioCustomizadoApiRequest nomeCidade = FiltroRelatorioCustomizadoApiRequest.builder()
				.nomeParametro("nome")
				.valor(buscaCep.cidade())
				.tipo(TipoValorRelatorioCustomizado.STRING)
				.criptografar(false)
				.build();
		
		FiltroRelatorioCustomizadoApiRequest uf = FiltroRelatorioCustomizadoApiRequest.builder()
				.nomeParametro("uf")
				.valor(buscaCep.uf())
				.tipo(TipoValorRelatorioCustomizado.STRING)
				.criptografar(false)
				.build();
		
		List<FiltroRelatorioCustomizadoApiRequest> filtros = Arrays.asList(nomeCidade,uf);
		
		try {
			List<CidadeApiResponse> cidadeDto = relatorioCustomizado.buscarRelatorioGenerico(idRelatorioCidade, filtros, CidadeApiResponse.class);
			return cidadeDto.stream().findFirst().orElseThrow(
					() -> new ApiTseException(String.format("Não encontrado a cidade %s para o estado %s", buscaCep.cidade(), buscaCep.uf())));
		} catch (FeignException e) {
			log.error("Erro ao chmar a api de busca cep");
            throw new ApiTseException(String.format("Erro: %s ao obter cidade pela api do TSE", e.contentUTF8()));
		}
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
