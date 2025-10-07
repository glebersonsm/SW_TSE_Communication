package com.sw.tse.domain.service.impl.db;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.api.dto.OperadorSistemaRequestDto;
import com.sw.tse.client.OperadorSistemaApiClient;
import com.sw.tse.core.config.CadastroOperadorSistemaPropertiesCustom;
import com.sw.tse.domain.converter.OperadorSistemaConverter;
import com.sw.tse.domain.expection.ApiTseException;
import com.sw.tse.domain.expection.OperadorSistemaNotFoundException;
import com.sw.tse.domain.expection.ValorPadraoNaoConfiguradoException;
import com.sw.tse.domain.model.api.enums.TipoValorRelatorioCustomizado;
import com.sw.tse.domain.model.api.request.FiltroRelatorioCustomizadoApiRequest;
import com.sw.tse.domain.model.api.request.OperadorSistemaApiRequest;
import com.sw.tse.domain.model.api.response.BuscaOperadorSistemPessoaResponse;
import com.sw.tse.domain.model.api.response.OperadorSistemaCriadoApiResponse;
import com.sw.tse.domain.model.api.response.OperadorSistemaListaApiResponse;
import com.sw.tse.domain.model.db.OperadorSistema;
import com.sw.tse.domain.repository.OperadorSistemaRepository;
import com.sw.tse.domain.service.impl.api.RelatorioCustomizadoApiService;
import com.sw.tse.domain.service.interfaces.OperadorSistemaService;
import com.sw.tse.domain.service.interfaces.TokenTseService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@ConditionalOnProperty(name = "database.enabled", havingValue = "true")
@RequiredArgsConstructor
@Service
@Slf4j
public class OperadorSistemaDbServiceImpl implements OperadorSistemaService {
	
	private final OperadorSistemaRepository operadorSistemaRepository;
	private final OperadorSistemaConverter operadorSistemaConverter;
	private final CadastroOperadorSistemaPropertiesCustom cadastroOperadorSistemaPropertiesCustom;
	
	private final TokenTseService tokenTseService;
	private final OperadorSistemaApiClient operadorSistemaApiClient;
	private final RelatorioCustomizadoApiService relatorioCustomizadoApiService;
	
	@Value("${api.tse.relatorios.operadorcliente}")
	private Long idRelatorioOperadorSistema;

	@Override
	public List<OperadorSistemaListaApiResponse> listarTodos() {
		List<OperadorSistema> listaOperadoresSistema = operadorSistemaRepository.findAll();
		return operadorSistemaConverter.toDtoList(listaOperadoresSistema);
	}

	@Override
	public OperadorSistemaListaApiResponse buscarPorId(Long idOperadorSistema) {
		OperadorSistema operadorSistema = operadorSistemaRepository.findById(idOperadorSistema)
				.orElseThrow(() -> new OperadorSistemaNotFoundException(String.format("Não existe operador sistema com id %d", idOperadorSistema)));
		return operadorSistemaConverter.toDto(operadorSistema);
	}
	
	
	@Override
	public OperadorSistema operadorSistemaPadraoCadastro() {
		if(cadastroOperadorSistemaPropertiesCustom.getOperador() == null || 
				cadastroOperadorSistemaPropertiesCustom.getOperador().equals(0L)) {
			throw new ValorPadraoNaoConfiguradoException("Usuário padrão para cadastro não configurado");
		}
		
		return operadorSistemaRepository.findById(cadastroOperadorSistemaPropertiesCustom.getOperador())
				.orElseThrow(() -> new OperadorSistemaNotFoundException(String.format("Não existe operador sistema com id %d", cadastroOperadorSistemaPropertiesCustom.getOperador())));
	}

	@Override
	public OperadorSistemaCriadoApiResponse criarOperadorSistema(OperadorSistemaRequestDto requestDto) {
		log.info("Iniciando processo de criação de operador de sistema via API (delegação da implementação DB)...");
		try {
			String bearerToken = "Bearer " + tokenTseService.gerarToken();
			

			if (requestDto.listaIdEmpresasPermitidas() == null || requestDto.listaIdEmpresasPermitidas().isEmpty()) {
				throw new IllegalArgumentException("É necessário fornecer ao menos uma empresa permitida.");
			}
			
			Long idEmpresaPrincipal = requestDto.listaIdEmpresasPermitidas().get(0);
			log.info("Setando empresa de sessão na API para o ID: {}", idEmpresaPrincipal);
			operadorSistemaApiClient.setEmpresaSessao(idEmpresaPrincipal, bearerToken, Collections.emptyMap());
			

			OperadorSistemaApiRequest apiRequest = new OperadorSistemaApiRequest(
				requestDto.login(),
				requestDto.tipoOperador(),
				requestDto.email(),
				requestDto.listaIdEmpresasPermitidas(),
				requestDto.idPessoaVincular()
			);
			
			log.info("Enviando requisição para criar operador com login: {}", apiRequest.login() );
			OperadorSistemaCriadoApiResponse response = operadorSistemaApiClient.criarOperadorSistema(bearerToken, apiRequest);
			
			log.info("Operador de sistema criado com sucesso via API. Novo ID: {}", response.idOperador());
			return response;
			
		} catch (ApiTseException e) {
			log.error("Falha de negócio ao criar operador: {}", e.getMessage());
			throw new ApiTseException(e.getMessage());
		} catch (FeignException e) {
            log.error("Erro de comunicação ao criar operador. Status: {}, Corpo: {}", e.status(), e.contentUTF8(), e);
            throw new ApiTseException("Erro de comunicação com a API de Operadores.", e);
        }
	}

	@Override
	public BuscaOperadorSistemPessoaResponse buscarPorIdPessoa(Long idPessoa) {

		if(idRelatorioOperadorSistema == null || idRelatorioOperadorSistema.equals(0L)) {
			throw new ApiTseException("Relatório customizado pra consultar operador sistema por id pessoa não parametrizado");
		}
		
		FiltroRelatorioCustomizadoApiRequest filtroIdPessoa = FiltroRelatorioCustomizadoApiRequest.builder()
				.nomeParametro("idpessoa")
				.valor(idPessoa.toString())
				.tipo(TipoValorRelatorioCustomizado.INTETEIRO)
				.criptografar(false)
				.build();
		
		List<FiltroRelatorioCustomizadoApiRequest> filtros = Arrays.asList(filtroIdPessoa);
		
		try {
			List<BuscaOperadorSistemPessoaResponse> listaOperadorSistemPessoa = 
					relatorioCustomizadoApiService.buscarRelatorioGenerico(idRelatorioOperadorSistema, filtros, BuscaOperadorSistemPessoaResponse.class);
			return listaOperadorSistemPessoa.stream().findFirst().orElse(new BuscaOperadorSistemPessoaResponse(0L, null, null, null, false));
			
		} catch (FeignException e) {
			log.error("erro ao chamar a api de operador de sistema cliente");
            throw new ApiTseException(String.format("Erro: %s ao obter operador sistema cliente pela api do TSE", e.contentUTF8()));
		}
	}

	@Override
	public BuscaOperadorSistemPessoaResponse buscarPorLogin(String login) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
