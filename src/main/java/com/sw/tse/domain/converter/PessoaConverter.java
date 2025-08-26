package com.sw.tse.domain.converter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.sw.tse.controller.model.CidadeDto;
import com.sw.tse.controller.model.HospedeDto;
import com.sw.tse.controller.model.TipoEnderecoDto;
import com.sw.tse.core.config.CadastroPessoaPropertiesCustom;
import com.sw.tse.core.util.StringUtil;
import com.sw.tse.domain.expection.ApiTseException;
import com.sw.tse.domain.model.api.enums.SexoEnum;
import com.sw.tse.domain.model.api.enums.TipoPessoaEnum;
import com.sw.tse.domain.model.api.enums.TipoTelefone;
import com.sw.tse.domain.model.api.request.ContatoTelefonicoDto;
import com.sw.tse.domain.model.api.request.EnderecoEmailDto;
import com.sw.tse.domain.model.api.request.EnderecoPessoaDto;
import com.sw.tse.domain.model.api.request.PessoaApiRequest;
import com.sw.tse.domain.service.interfaces.CidadeService;
import com.sw.tse.domain.service.interfaces.TipoEnderecoService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PessoaConverter {

	private final CidadeService cidadeService;
	private final TipoEnderecoService tipoEnderecoService;
	private final CadastroPessoaPropertiesCustom cadastroPessoaPropertiesCustom;
	
	
	public PessoaApiRequest toPessoaApiHospedeDto(HospedeDto hospedeDto) {
        if (hospedeDto == null) {
            return null;
        }
        
        Long idPessoa = hospedeDto.idHospede(); 
        LocalDate dataCadastro = LocalDate.now(); 
        TipoPessoaEnum tipoPessoa = TipoPessoaEnum.FISICA; 
        String razaoSocial = hospedeDto.nome();
        String nomeFantasia = hospedeDto.nome();
        LocalDate dataNascimento = hospedeDto.dataNascimento();
        String cpfCnpj = hospedeDto.cpf();
        SexoEnum sexo;
        if(hospedeDto.sexo().equals("M")) {
        	sexo = SexoEnum.MASCULINO;
        } else {
        	sexo = SexoEnum.FEMININO;
        }

        List<EnderecoPessoaDto> enderecos = construirListaEnderecos(hospedeDto);
        List<EnderecoEmailDto> emails = construirListaEmails(hospedeDto);
        List<ContatoTelefonicoDto> contatos = construirListaContatos(hospedeDto);


        return new PessoaApiRequest(
                idPessoa,
                dataCadastro,
                tipoPessoa,
                razaoSocial,
                nomeFantasia,
                dataNascimento,
                cpfCnpj,
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

    private List<EnderecoPessoaDto> construirListaEnderecos(HospedeDto dto) {
        if (!StringUtils.hasText(dto.logradouro())) {
            return Collections.emptyList();
        }
        
        TipoEnderecoDto tipoEnderecoPadrao = validarTipoEnderecoPadrao();
        
        String cep = StringUtil.removerMascaraCep(dto.cep());
        CidadeDto cidadeDto = cidadeService.buscarPorCep(cep);

        EnderecoPessoaDto endereco = new EnderecoPessoaDto(
        		tipoEnderecoPadrao.id(), // ID ENDERECO
                "Endereço Principal",  // DECRICAO DO ENDERECO
                cadastroPessoaPropertiesCustom.getTipoendereco() , // ID TIPO DE ENDERECO
                "Residencial", //DESCRICAO TIPO ENDERECO
                1, // ID TIPO LOGRADOURO
                "Rua", // TIPO LOGRADOURO
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

    private List<EnderecoEmailDto> construirListaEmails(HospedeDto dto) {
        if (!StringUtils.hasText(dto.email())) {
            return Collections.emptyList();
        }
        return List.of(new EnderecoEmailDto(null, dto.email(), "Email Principal"));
    }

    private List<ContatoTelefonicoDto> construirListaContatos(HospedeDto dto) {
        if (!StringUtils.hasText(dto.telefone())) {
            return Collections.emptyList();
        }

        TipoTelefone tipoPadrao = TipoTelefone.CELULAR;
        
        if(cadastroPessoaPropertiesCustom.getTipotefone() != null) {
        	tipoPadrao = cadastroPessoaPropertiesCustom.getTipotefone();
        }
        
        String telefoneNumeros = dto.telefone().replaceAll("\\D", "");
        String ddi = telefoneNumeros.length() >= 12 ? telefoneNumeros.substring(0, 2) : "55";
        String ddd = telefoneNumeros.length() >= 10 ? telefoneNumeros.substring(2, 4) : "";
        String numero = telefoneNumeros.length() >= 8 ? telefoneNumeros.substring(4) : telefoneNumeros;

        ContatoTelefonicoDto contato = new ContatoTelefonicoDto(
        		tipoPadrao, // TIPO CONTADO TELEFONE
                tryParseInt(ddi), // DDI
                tryParseInt(ddd), // DDD
                tryParseInt(numero), // NUMERO
                null, // RAMAL
                "Telefone Principal", // DESCRICAO
                false // CONTEM WHATS
        );
        return List.of(contato);
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
    
    private TipoEnderecoDto validarTipoEnderecoPadrao() {
    	if(cadastroPessoaPropertiesCustom.getTipoendereco() == null) {
    		throw new ApiTseException("Tipo endereço padrão não configurado");
    	}
    	
    	List<TipoEnderecoDto> listaTipoEndereco = tipoEnderecoService.listarTiposEndereco();
		
		
		if(!listaTipoEndereco.stream()
                .anyMatch(endereco -> cadastroPessoaPropertiesCustom.getTipoendereco().equals(endereco.id()))){
    		throw new ApiTseException("Tipo endereço padrão configurar nãoe existe no tse");
    	}
    	
    	return listaTipoEndereco.stream().findFirst().get();
    }
	
}
