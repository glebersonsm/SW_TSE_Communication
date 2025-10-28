package com.sw.tse.domain.service.impl.api;

import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.api.dto.HospedeDto;
import com.sw.tse.client.PessoaApiClient;
import com.sw.tse.domain.converter.PessoaConverter;
import com.sw.tse.domain.expection.ApiTseException;
import com.sw.tse.domain.model.api.request.PessoaApiRequest;
import com.sw.tse.domain.model.api.response.PessoaCpfApiResponse;
import com.sw.tse.domain.model.db.Contrato;
import com.sw.tse.domain.service.interfaces.PessoaService;
import com.sw.tse.domain.service.interfaces.TokenTseService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@ConditionalOnProperty(name = "database.enabled", havingValue = "false", matchIfMissing = true)
public class PessoaApiServiceImpl implements PessoaService {
	
	private final PessoaApiClient pessoaApiClient;
	private final TokenTseService tokenTseService;
	private final PessoaConverter pessoaConverter;
	
	@Override	
	public Long salvar(HospedeDto hospedeDto, Contrato contrato) {
		
		PessoaApiRequest request = pessoaConverter.toPessoaApiHospedeDto(hospedeDto);
		Long idPessoa = hospedeDto.idHospede() == null ? 0 : hospedeDto.idHospede();
		
		log.info("Iniciando processo de salvar pessoa com ID: {}", idPessoa);
		
		try {
			String bearerToken = "Bearer " + tokenTseService.gerarToken();
			
			if(request.cpfCnpj() != null) {
				 Optional<PessoaCpfApiResponse> optionalPessoaCpf = buscarPorCpf(request.cpfCnpj());
				 if(optionalPessoaCpf.isPresent()) {
					 // VALIDAR SE É PROPRIETÁRIO - SE FOR, NÃO ATUALIZAR DADOS
					 Long pessoaId = optionalPessoaCpf.get().idPessoa();
					 if (contrato != null && pessoaId != null) {
						 // Verificar se é cessionário ou cocessionário
						 if ((contrato.getPessoaCessionario() != null && 
							  pessoaId.equals(contrato.getPessoaCessionario().getIdPessoa())) ||
							 (contrato.getPessaoCocessionario() != null && 
							  pessoaId.equals(contrato.getPessaoCocessionario().getIdPessoa()))) {
							 log.info("Pessoa {} é proprietário do contrato {} - dados não serão atualizados por segurança", 
								 pessoaId, contrato.getId());
							 return pessoaId;
						 }
					 }
					 return pessoaId;
				 }
			}
		
			log.info("Enviando requisição para salvar pessoa");
			
			Long idPessoaSalva = Long.valueOf(pessoaApiClient.salvarPessoa(idPessoa, bearerToken, request));
		
			return idPessoaSalva;
		 } catch (FeignException e) {
	            log.error("Erro ao chamar a API de Pessoas. Status: {}, Corpo: {}", e.status(), e.contentUTF8(), e);
	            if (e.status() == 400) {
	                throw new ApiTseException("Dados inválidos enviados para a API de Pessoas: " + e.contentUTF8());
	            }
	            if (e.status() == 401 || e.status() == 403) {
	                 throw new ApiTseException("Falha de autenticação/autorização com a API do TSE.");
	            }
	            throw new ApiTseException("Erro de comunicação com a API de Pessoas.", e);
	    }
	}
	
	
	@Override
	public Optional<PessoaCpfApiResponse> buscarPorCpf(String cpf) {
		String bearerToken = "Bearer " + tokenTseService.gerarToken();
		try {			
			PessoaCpfApiResponse pessoaCpf = pessoaApiClient.buscarPorCpf(cpf, bearerToken);
			return Optional.of(pessoaCpf);
		} catch (FeignException e) {
			if(e.status() == 400) {
				return Optional.empty();
			}
			String errorContent = e.contentUTF8();
			String errorMessage = (errorContent != null && !errorContent.trim().isEmpty()) 
				? errorContent 
				: "Erro ao buscar pessoa por CPF (HTTP " + e.status() + ")";
			throw new ApiTseException(errorMessage);
			
		}
	}

	@Override
	public Optional<PessoaCpfApiResponse> buscarPorId(Long idPessoa) {
		String bearerToken = "Bearer " + tokenTseService.gerarToken();
		try {
			PessoaApiRequest pessoaRequest = pessoaApiClient.buscarPorId(idPessoa, bearerToken);
			
			// Extrair email principal
			String email = null;
			if (pessoaRequest.enderecosEmail() != null && !pessoaRequest.enderecosEmail().isEmpty()) {
				email = pessoaRequest.enderecosEmail().get(0).email();
			}
			
			// Converter PessoaApiRequest para PessoaCpfApiResponse
			PessoaCpfApiResponse pessoaCpf = new PessoaCpfApiResponse(
				pessoaRequest.idPessoa(),
				pessoaRequest.razaoSocial(),
				email
			);
			
			return Optional.of(pessoaCpf);
		} catch (FeignException e) {
			if(e.status() == 400 || e.status() == 404) {
				return Optional.empty();
			}
			String errorContent = e.contentUTF8();
			String errorMessage = (errorContent != null && !errorContent.trim().isEmpty()) 
				? errorContent 
				: "Erro ao buscar pessoa por ID (HTTP " + e.status() + ")";
			throw new ApiTseException(errorMessage);
		}
	}
	
}
