package com.sw.tse.domain.converter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.sw.tse.api.dto.HospedeDto;
import com.sw.tse.core.config.CadastroPessoaPropertiesCustom;
import com.sw.tse.core.util.StringUtil;
import com.sw.tse.domain.expection.ValorPadraoNaoConfiguradoException;
import com.sw.tse.domain.model.api.enums.SexoEnum;
import com.sw.tse.domain.model.api.enums.TipoPessoaEnum;
import com.sw.tse.domain.model.api.enums.TipoTelefone;
import com.sw.tse.domain.model.api.request.ContatoTelefonicoApiRequest;
import com.sw.tse.domain.model.api.request.EnderecoEmailApiRequest;
import com.sw.tse.domain.model.api.request.EnderecoPessoaApiRequest;
import com.sw.tse.domain.model.api.request.PessoaApiRequest;
import com.sw.tse.domain.model.api.response.CidadeApiResponse;
import com.sw.tse.domain.model.api.response.PessoaCpfApiResponse;
import com.sw.tse.domain.model.api.response.TipoEnderecoApiResponse;
import com.sw.tse.domain.model.api.response.TipoLogradouroApiResponse;
import com.sw.tse.domain.model.db.Cidade;
import com.sw.tse.domain.model.db.OperadorSistema;
import com.sw.tse.domain.model.db.Pessoa;
import com.sw.tse.domain.model.db.TipoEnderecoPessoa;
import com.sw.tse.domain.model.db.TipoLogradouro;
import com.sw.tse.domain.service.interfaces.CidadeService;
import com.sw.tse.domain.service.interfaces.TipoEnderecoService;
import com.sw.tse.domain.service.interfaces.TipoLogradouroService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PessoaConverter {
	
	private final CidadeService cidadeService;
	private final TipoEnderecoService tipoEnderecoService;
	private final TipoLogradouroService tipoLogradouroService;
	private final CadastroPessoaPropertiesCustom cadastroPessoaPropertiesCustom;
	private final TipoLogradouroConverter tipoLogradouroConverter;
	private final TipoEnderecoConverter tipoEnderecoConverter;
	private final CidadeConverter cidadeConverter;
	
	public PessoaApiRequest toPessoaApiHospedeDto(HospedeDto hospedeDto) {
        if (hospedeDto == null) {
            return null;
        }
        
        Long idPessoa = hospedeDto.idHospede(); 
        LocalDate dataCadastro = LocalDate.now(); 
        TipoPessoaEnum tipoPessoa = TipoPessoaEnum.FISICA; 
        String razaoSocial = hospedeDto.nome().toUpperCase();
        LocalDate dataNascimento = hospedeDto.dataNascimento();
        String documento = "";
        if(hospedeDto.tipoDocumento() == null || hospedeDto.tipoDocumento().equals("CPF")) {
        	documento = StringUtils.hasText(hospedeDto.numeroDocumento()) ? StringUtil.removeMascaraCpf(hospedeDto.numeroDocumento()) : "";
        }
        SexoEnum sexo;
        if(hospedeDto.sexo().equals("M")) {
        	sexo = SexoEnum.MASCULINO;
        } else {
        	sexo = SexoEnum.FEMININO;
        }

        List<EnderecoPessoaApiRequest> enderecos = construirListaEnderecos(hospedeDto);
        List<EnderecoEmailApiRequest> emails = construirListaEmails(hospedeDto);
        List<ContatoTelefonicoApiRequest> contatos = construirListaContatos(hospedeDto);


        return new PessoaApiRequest(
                idPessoa,
                dataCadastro,
                tipoPessoa,
                razaoSocial,
                null,
                dataNascimento,
                documento,
                null, // RG NUMERO
                null, // RG ORGÃO EXPEDITOR
                null, // RG UH
                null, // ID PROFISSÃO
                sexo,
                null, // ID ESTADO CIVIL
                null, // ID REGIME CASAMENTO
                null, // ID NASCIONALIDADE
                null, // ID IDIOMA
                enderecos.isEmpty() ? null : enderecos,
                emails.isEmpty() ? null : emails,
                contatos.isEmpty() ? null : contatos
        );
    }

    private List<EnderecoPessoaApiRequest> construirListaEnderecos(HospedeDto dto) {
        if (!StringUtils.hasText(dto.logradouro())) {
            return Collections.emptyList();
        }
        
        TipoEnderecoApiResponse tipoEnderecoPadrao = validarTipoEnderecoPadrao();
        TipoLogradouroApiResponse tipoLogradouroPadrao = validarTipoLogradouroPadrao();
        
        String cep = StringUtil.removerMascaraCep(dto.cep());
        CidadeApiResponse cidadeDto = cidadeService.buscarPorCep(cep);

        EnderecoPessoaApiRequest endereco = new EnderecoPessoaApiRequest(
        		tipoEnderecoPadrao.id(), // ID ENDERECO
                "Endereço Principal",  // DECRICAO DO ENDERECO
                cadastroPessoaPropertiesCustom.getTipoendereco() , // ID TIPO DE ENDERECO
                "Residencial", //DESCRICAO TIPO ENDERECO
                tipoLogradouroPadrao.id().intValue(), // ID TIPO LOGRADOURO
                tipoLogradouroPadrao.descricao(), // TIPO LOGRADOURO
                dto.logradouro(), // LOGRADOURO
                dto.complemento(), // COMPLENTO
                dto.bairro(), // BAIRRO
                dto.numero(), // NUMERO
                cidadeDto.getIdPais().intValue(), //ID PAIS (DEFINIR PADRÃO)
                cidadeDto.getIdCidade().intValue(), // ID CIDADE
                cidadeDto.getNome(), // NOME CIDADE
                cidadeDto.getIdEstado().intValue(), // ID UF
                tryParseInt(dto.cep()), // CEP
                true  // USAR CORRESPONDECIA PADRÃO TRUE
        );
       
        return List.of(endereco);
    }

    private List<EnderecoEmailApiRequest> construirListaEmails(HospedeDto dto) {
        if (!StringUtils.hasText(dto.email())) {
            return Collections.emptyList();
        }
        return List.of(new EnderecoEmailApiRequest(null, dto.email(), "Email Principal"));
    }

    private List<ContatoTelefonicoApiRequest> construirListaContatos(HospedeDto dto) {
        if (!StringUtils.hasText(dto.telefone())) {
            return Collections.emptyList();
        }

        TipoTelefone tipoPadrao = TipoTelefone.CELULAR;
        
        if(cadastroPessoaPropertiesCustom.getTipotefone() != null) {
        	tipoPadrao = cadastroPessoaPropertiesCustom.getTipotefone();
        }
        
        
        

        if(StringUtils.hasText(dto.telefone())) {
        
        	String ddi = dto.ddi() != null ? dto.ddi().trim() : null;
            String ddd = dto.ddd() != null ? dto.ddi().trim() : null;
            String numeroTelefone = StringUtil.removerMascaraTelefone(dto.telefone());
            
	        ContatoTelefonicoApiRequest contato = new ContatoTelefonicoApiRequest(
	        		tipoPadrao, // TIPO CONTADO TELEFONE
	                tryParseInt(ddi), // DDI
	                tryParseInt(ddd), // DDD
	                tryParseInt(numeroTelefone), // NUMERO
	                null, // RAMAL
	                "Telefone Principal", // DESCRICAO
	                true // CONTEM WHATS
	        );
	        return List.of(contato);
        }
        
        return null;
    }

    private Integer tryParseInt(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.replaceAll("\\D", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private TipoEnderecoApiResponse validarTipoEnderecoPadrao() {
    	if(cadastroPessoaPropertiesCustom.getTipoendereco() == null) {
    		throw new ValorPadraoNaoConfiguradoException("Tipo endereço padrão não configurado");
    	}
    	
    	List<TipoEnderecoApiResponse> listaTipoEndereco = tipoEnderecoService.listarTiposEndereco();
		
		
		if(!listaTipoEndereco.stream()
                .anyMatch(tipoEndereco -> cadastroPessoaPropertiesCustom.getTipoendereco().equals(tipoEndereco.id()))){
    		throw new ValorPadraoNaoConfiguradoException("Tipo endereço padrão configurar não existe no TSE");
    	}
    	
    	return listaTipoEndereco.stream().findFirst().get();
    }
    
    private TipoLogradouroApiResponse validarTipoLogradouroPadrao() {
    	if(cadastroPessoaPropertiesCustom.getTipoLogradouro() == null) {
    		throw new ValorPadraoNaoConfiguradoException("Tipo logradouro padrão não configurado");
    	}
    	
    	List<TipoLogradouroApiResponse> listaTipoLogradouro = tipoLogradouroService.listarTiposLogradouro();
    	
    	if(!listaTipoLogradouro.stream()
    			.anyMatch(tipoLogradouro -> cadastroPessoaPropertiesCustom.getTipoLogradouro().equals(tipoLogradouro.id()))) {
    		throw new ValorPadraoNaoConfiguradoException("Tipo logradouro padrão configurado não existe no TSE");
    	}
    	
    	return listaTipoLogradouro.stream().findFirst().get();
    }
    
	public Pessoa hospedeDtoToPessoa(HospedeDto hospedeDto, Pessoa pessoa, OperadorSistema responsavelCadastro) {
		
		if(pessoa.getIdPessoa() == null) {
			pessoa = novaPessoa(hospedeDto, pessoa, responsavelCadastro);
		}
		String cep = StringUtil.removerMascaraCep(hospedeDto.cep());
        CidadeApiResponse cidadeDto = cidadeService.buscarPorCep(cep);
        Cidade cidade = cidadeConverter.toEntity(cidadeDto);
	
        TipoEnderecoPessoa tipoEndereco = tipoEnderecoConverter.toEntity(validarTipoEnderecoPadrao());
        TipoLogradouro tipoLogradouro = tipoLogradouroConverter.toEntity(validarTipoLogradouroPadrao());
        
       
        if (!verificarSeEnderecoExiste(pessoa, cep)) {
			pessoa.adicionarEndereco("Endereço padrão", hospedeDto.logradouro(), hospedeDto.numero(), hospedeDto.complemento(), hospedeDto.bairro(), hospedeDto.cep(),
					cidade, true, tipoEndereco, tipoLogradouro, responsavelCadastro);
		}
		
        if(!verificarSeTelefoneExiste(pessoa, hospedeDto.ddd(), hospedeDto.telefone())){
        	pessoa.adicionarContatoTelefonico("Telefone padrão", TipoTelefone.CELULAR.getCodigo(), hospedeDto.ddi(), hospedeDto.ddd(), hospedeDto.telefone(), null,
        			true, null, responsavelCadastro);
        }
        
        if(!vericarSeEmailExiste(pessoa, hospedeDto.email())) {
        	pessoa.adicionarEmail("Email padrão", hospedeDto.email(), null, responsavelCadastro);
        }
        
		return pessoa;
	}

	private Pessoa novaPessoa(HospedeDto hospedeDto, Pessoa pessoa, OperadorSistema responsavelCadastro) {
		pessoa.setNome(hospedeDto.nome().toUpperCase());
		if(hospedeDto.tipoDocumento() == null || hospedeDto.tipoDocumento().equals("CPF")) {
			String numeroDocumento = StringUtil.removeMascaraCpf(hospedeDto.numeroDocumento());
			pessoa.setCpfCnpj(numeroDocumento);
		} else {
			pessoa.setNumeroDocumento(hospedeDto.numeroDocumento());
		}
		pessoa.setDataNascimento(hospedeDto.dataNascimento());
		SexoEnum sexo = hospedeDto.sexo().equals("M") ? SexoEnum.MASCULINO : SexoEnum.FEMININO;
		pessoa.setSexo(sexo);
		pessoa.setOperadorCadastro(responsavelCadastro);
		
		return pessoa;
	}
	
	
	public boolean verificarSeEnderecoExiste(Pessoa pessoa, String cepParaBuscar) {
	    if (cepParaBuscar == null || cepParaBuscar.isBlank()) {
	        return false;
	    }
	    
	  	return pessoa.getEnderecos().stream()
	        .anyMatch(endereco -> {

	            String cepDoEndereco = endereco.getCep();
	            if (cepDoEndereco == null || cepDoEndereco.isBlank()) {
	                return false; 
	            }
	            
	            String cepLimpoDoEndereco = StringUtil.removerMascaraCep(cepDoEndereco);
	            return cepLimpoDoEndereco.equals(cepParaBuscar);
	        });
	}
	
	public boolean verificarSeTelefoneExiste(Pessoa pessoa, String ddd, String numero) {	
		if(!StringUtils.hasText(ddd) && !StringUtils.hasText(numero)) {
			return false;
		}
		
		return pessoa.getTelefones().stream()
        .filter(Objects::nonNull) 
        .anyMatch(telefoneDaLista -> {
            
            boolean dddIgual = Objects.equals(telefoneDaLista.getDdd(), ddd);
            boolean numeroIgual = Objects.equals(telefoneDaLista.getNumero(), numero);
            
            return dddIgual && numeroIgual;
        });

	}
	
	public boolean vericarSeEmailExiste(Pessoa pessoa, String email) {	
		if(!StringUtils.hasText(email)) {
			return false;
		}
		
		return pessoa.getEmails().stream()
				.anyMatch(enderecoEmail -> {
					String emailSalvo = enderecoEmail.getEmail();
					if(emailSalvo == null || emailSalvo.isBlank()) {
						return false;
					}
					return email.equals(emailSalvo);
				});
	}
	
	
	public PessoaCpfApiResponse toPessoaCpfApiResponse(Pessoa pessoa) {
		if(pessoa.getIdPessoa() == null) {
			return null;
		}
		
		Long idPessoa = pessoa.getIdPessoa();
		String nome = pessoa.getNome();
		
		String email = pessoa.getEmails().stream().findFirst().orElse(null).toString();
		
		return  new PessoaCpfApiResponse(idPessoa, nome, email);
		
		
	}
}

