package com.sw.tse.domain.service.impl.db;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sw.tse.api.dto.DadosPessoaDto;
import com.sw.tse.api.dto.EnderecoDto;
import com.sw.tse.api.dto.EnderecoResponse;
import com.sw.tse.api.dto.HospedeDto;
import com.sw.tse.api.dto.PessoaComProprietarioResponse;
import com.sw.tse.api.dto.TelefoneResponse;
import com.sw.tse.core.util.StringUtil;
import com.sw.tse.domain.converter.PessoaConverter;
import com.sw.tse.domain.model.api.response.PessoaCpfApiResponse;
import com.sw.tse.domain.model.db.ContatoTelefonico;
import com.sw.tse.domain.model.db.Contrato;
import com.sw.tse.domain.model.db.EnderecoPessoa;
import com.sw.tse.domain.model.db.OperadorSistema;
import com.sw.tse.domain.model.db.Pessoa;
import com.sw.tse.domain.repository.ContratoRepository;
import com.sw.tse.domain.repository.PessoaRepository;
import com.sw.tse.domain.service.interfaces.OperadorSistemaService;
import com.sw.tse.domain.service.interfaces.PessoaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConditionalOnProperty(name = "database.enabled", havingValue = "true")
@RequiredArgsConstructor
@Service
public class PessoaDbServiceImpl implements PessoaService {
	
	private final PessoaConverter pessoaConverter;
	private final PessoaRepository pessoaRepository;
	private final ContratoRepository contratoRepository;
	
	private final OperadorSistemaService operadorSistemaService;
	
	

	@Transactional
	@Override
	public Long salvar(HospedeDto hospedeDto, Contrato contrato) {
		Pessoa pessoa = new Pessoa();
		
		if(StringUtils.hasText(hospedeDto.numeroDocumento())) {
			String numeroDocumento = StringUtil.removeMascaraCpf(hospedeDto.numeroDocumento());
			List<Pessoa> listaPessoa = new ArrayList<>();
			
			if(hospedeDto.tipoDocumento() == null || hospedeDto.tipoDocumento().equals("CPF")) {
				listaPessoa = pessoaRepository.findFirstByCpfCnpjOrderByIdPessoa(numeroDocumento);
			}
			
			if(!listaPessoa.isEmpty()) {
				pessoa = listaPessoa.get(0);
				
				// VALIDAR SE É PROPRIETÁRIO - SE FOR, NÃO ATUALIZAR DADOS
				if (contrato != null && pessoa.getIdPessoa() != null) {
					boolean isProprietario = false;
					
					// Verificar se é cessionário
					if (contrato.getPessoaCessionario() != null && 
						pessoa.getIdPessoa().equals(contrato.getPessoaCessionario().getIdPessoa())) {
						isProprietario = true;
						log.info("Pessoa {} é cessionário do contrato {} - dados não serão atualizados por segurança", 
							pessoa.getIdPessoa(), contrato.getId());
					}
					
					// Verificar se é cocessionário
					if (contrato.getPessaoCocessionario() != null && 
						pessoa.getIdPessoa().equals(contrato.getPessaoCocessionario().getIdPessoa())) {
						isProprietario = true;
						log.info("Pessoa {} é cocessionário do contrato {} - dados não serão atualizados por segurança", 
							pessoa.getIdPessoa(), contrato.getId());
					}
					
					// Se for proprietário, retornar sem atualizar
					if (isProprietario) {
						return pessoa.getIdPessoa();
					}
				}
			}
			
		}
		
		OperadorSistema responsavelCadastro = operadorSistemaService.operadorSistemaPadraoCadastro();
		
		pessoa = pessoaConverter.hospedeDtoToPessoa(hospedeDto, pessoa , responsavelCadastro);
		
		pessoaRepository.save(pessoa);
		
		return pessoa.getIdPessoa();
	}



	@Override
	public Optional<PessoaCpfApiResponse> buscarPorCpf(String cpf) {
		Pessoa pessoa = 
				pessoaRepository.findFirstByCpfCnpjOrderByIdPessoa(cpf).stream().findFirst().orElse(new Pessoa());
		
		if(pessoa.getIdPessoa() == null) {
			return Optional.empty();
		}
		
		return  Optional.of(pessoaConverter.toPessoaCpfApiResponse(pessoa));
	}

	@Override
	public Optional<PessoaCpfApiResponse> buscarPorId(Long idPessoa) {
		Pessoa pessoa = pessoaRepository.findById(idPessoa).orElse(null);
		
		if(pessoa == null) {
			return Optional.empty();
		}
		
		return Optional.of(pessoaConverter.toPessoaCpfApiResponse(pessoa));
	}

	@Override
	public Optional<PessoaComProprietarioResponse> buscarPorCpfCompleto(String cpf, Long idContrato) {
		// Remover máscara do CPF
		String cpfLimpo = StringUtil.removeMascaraCpf(cpf);
		
		// Buscar pessoa por CPF
		Pessoa pessoa = pessoaRepository.findFirstByCpfCnpjOrderByIdPessoa(cpfLimpo)
			.stream()
			.findFirst()
			.orElse(null);
		
		if (pessoa == null || pessoa.getIdPessoa() == null) {
			return Optional.empty();
		}
		
		// Verificar se a pessoa é proprietária de QUALQUER contrato no sistema
		// (cessionário OU cocessionário de algum contrato)
		boolean isProprietario = contratoRepository.pessoaEhProprietariaDeAlgumContrato(pessoa.getIdPessoa());
		
		if (isProprietario) {
			log.info("🔒 Pessoa {} é PROPRIETÁRIA de algum contrato no sistema - dados protegidos contra edição", pessoa.getIdPessoa());
		} else {
			log.info("✏️ Pessoa {} NÃO é proprietária de nenhum contrato - dados podem ser editados", pessoa.getIdPessoa());
		}
		
		// Montar response com dados completos
		// Converter sexo: MASCULINO (código 0) -> "M", FEMININO (código 1) -> "F"
		String sexoConvertido = null;
		if (pessoa.getSexo() != null) {
			sexoConvertido = pessoa.getSexo().getCodigo() == 0 ? "M" : 
							 pessoa.getSexo().getCodigo() == 1 ? "F" : null;
		}
		
		PessoaComProprietarioResponse.PessoaComProprietarioResponseBuilder builder = PessoaComProprietarioResponse.builder()
			.idPessoa(pessoa.getIdPessoa())
			.nome(pessoa.getNome())
			.sobrenome("") // Pessoa não tem sobrenome separado
			.cpf(pessoa.getCpfCnpj())
			.dataNascimento(pessoa.getDataNascimento() != null ? 
				pessoa.getDataNascimento().format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
			.sexo(sexoConvertido)
			.isProprietario(isProprietario);
		
		// Buscar email (primeiro da lista)
		if (pessoa.getEmails() != null && !pessoa.getEmails().isEmpty()) {
			builder.email(pessoa.getEmails().get(0).getEmail());
		}
		
		// Buscar telefone (primeiro da lista)
		if (pessoa.getTelefones() != null && !pessoa.getTelefones().isEmpty()) {
			ContatoTelefonico telefone = pessoa.getTelefones().get(0);
			builder.telefone(TelefoneResponse.builder()
				.ddi(telefone.getDdi())
				.ddd(telefone.getDdd())
				.numero(telefone.getNumero())
				.build());
		}
		
		// Buscar endereço de correspondência ou primeiro endereço disponível
		if (pessoa.getEnderecos() != null && !pessoa.getEnderecos().isEmpty()) {
			// Primeiro, tentar buscar endereço de correspondência
			EnderecoPessoa enderecoCorrespondencia = pessoa.getEnderecos().stream()
				.filter(e -> e.getParaCorrespondencia() != null && e.getParaCorrespondencia())
				.findFirst()
				.orElse(null);
			
			// Se não encontrar endereço de correspondência, buscar o primeiro endereço disponível
			if (enderecoCorrespondencia == null) {
				enderecoCorrespondencia = pessoa.getEnderecos().get(0);
				log.debug("Endereço de correspondência não encontrado para pessoa {}. Usando primeiro endereço disponível.", pessoa.getIdPessoa());
			}
			
			if (enderecoCorrespondencia != null) {
				// Se UF estiver vazio no endereço, buscar da cidade relacionada
				String uf = enderecoCorrespondencia.getUf();
				if ((uf == null || uf.isEmpty()) && enderecoCorrespondencia.getCidade() != null) {
					uf = enderecoCorrespondencia.getCidade().getUf();
				}
				
				builder.endereco(EnderecoResponse.builder()
					.logradouro(enderecoCorrespondencia.getLogradouro())
					.numero(enderecoCorrespondencia.getNumero())
					.complemento(enderecoCorrespondencia.getComplemento())
					.bairro(enderecoCorrespondencia.getBairro())
					.cep(enderecoCorrespondencia.getCep())
					.cidade(enderecoCorrespondencia.getCidade() != null ? 
						enderecoCorrespondencia.getCidade().getNome() : null)
					.uf(uf)
					.build());
				
				log.debug("Endereço adicionado ao response para pessoa {}: {}, {} - {}/{}", 
					pessoa.getIdPessoa(), 
					enderecoCorrespondencia.getLogradouro(),
					enderecoCorrespondencia.getNumero(),
					enderecoCorrespondencia.getCidade() != null ? enderecoCorrespondencia.getCidade().getNome() : "",
					uf);
			}
		} else {
			log.debug("Nenhum endereço encontrado para pessoa {}", pessoa.getIdPessoa());
		}
		
		return Optional.of(builder.build());
	}
	
	@Override
	public DadosPessoaDto obterDadosPessoaLogada(Long pessoaId) {
		log.info("Buscando dados da pessoa ID: {}", pessoaId);
		
		var pessoa = pessoaRepository.findById(pessoaId)
				.orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));
		
		DadosPessoaDto dadosPessoa = new DadosPessoaDto();
		dadosPessoa.setId(pessoa.getIdPessoa());
		dadosPessoa.setNome(pessoa.getNome() != null ? pessoa.getNome() : "");
		dadosPessoa.setCpf(pessoa.getCpfCnpj() != null ? pessoa.getCpfCnpj() : "");
		
		// Pegar primeiro email da lista (se existir)
		if (pessoa.getEmails() != null && !pessoa.getEmails().isEmpty()) {
			dadosPessoa.setEmail(pessoa.getEmails().get(0).getEmail() != null ? 
								pessoa.getEmails().get(0).getEmail() : "");
		} else {
			dadosPessoa.setEmail("");
		}
		
		// Pegar primeiro telefone da lista (se existir) e formatar
		if (pessoa.getTelefones() != null && !pessoa.getTelefones().isEmpty()) {
			var telefone = pessoa.getTelefones().get(0);
			var numeroCompleto = (telefone.getDdd() != null ? telefone.getDdd() : "") + 
								(telefone.getNumero() != null ? telefone.getNumero() : "");
			dadosPessoa.setTelefone(numeroCompleto);
		} else {
			dadosPessoa.setTelefone("");
		}
		
		// Mapear primeiro endereço se existir
		if (pessoa.getEnderecos() != null && !pessoa.getEnderecos().isEmpty()) {
			var enderecoPessoa = pessoa.getEnderecos().get(0);
			
			EnderecoDto endereco = new EnderecoDto();
			endereco.setLogradouro(enderecoPessoa.getLogradouro() != null ? enderecoPessoa.getLogradouro() : "");
			endereco.setNumero(enderecoPessoa.getNumero() != null ? enderecoPessoa.getNumero() : "");
			endereco.setComplemento(enderecoPessoa.getComplemento() != null ? enderecoPessoa.getComplemento() : "");
			endereco.setBairro(enderecoPessoa.getBairro() != null ? enderecoPessoa.getBairro() : "");
			endereco.setCidade(enderecoPessoa.getCidade() != null ? 
							  enderecoPessoa.getCidade().getNome() : "");
			endereco.setUf(enderecoPessoa.getCidade() != null ?
						  enderecoPessoa.getCidade().getUf() : "");
			endereco.setCep(enderecoPessoa.getCep() != null ? enderecoPessoa.getCep() : "");
			
			dadosPessoa.setEndereco(endereco);
		}
		
		log.info("Dados da pessoa ID: {} retornados com sucesso", pessoaId);
		return dadosPessoa;
	}

}
