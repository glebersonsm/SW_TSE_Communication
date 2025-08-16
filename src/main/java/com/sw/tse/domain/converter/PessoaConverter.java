package com.sw.tse.domain.converter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.sw.tse.controller.model.HospedeDto;
import com.sw.tse.domain.model.api.enums.SexoEnum;
import com.sw.tse.domain.model.api.enums.TipoPessoaEnum;
import com.sw.tse.domain.model.api.request.ContatoTelefonicoDto;
import com.sw.tse.domain.model.api.request.EnderecoEmailDto;
import com.sw.tse.domain.model.api.request.EnderecoPessoaDto;
import com.sw.tse.domain.model.api.request.PessoaApiRequest;

@Component
public class PessoaConverter {

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
                null, // rgNumero (não presente no HospedeDto)
                null, // rgOrgaoExpedidor
                null, // rgUf
                null, // idProfissao
                sexo,
                null, // idEstadoCivil
                null, // idRegimeCasamento
                null, // idNacionalidade
                null, // idIdioma
                enderecos.isEmpty() ? null : enderecos,
                emails.isEmpty() ? null : emails,
                contatos.isEmpty() ? null : contatos
        );
    }

    private List<EnderecoPessoaDto> construirListaEnderecos(HospedeDto dto) {
        if (!StringUtils.hasText(dto.logradouro())) {
            return Collections.emptyList();
        }

        EnderecoPessoaDto endereco = new EnderecoPessoaDto(
        		null, // ID ENDERECO
                "Endereço Principal",  // DECRICAO DO ENDERECO
                1, // ID TIPO DE ENDERECO
                "Residencial", //DESCRICAO TIPO ENDERECO
                1, // ID TIPO LOGRADOURO
                "Rua", // TIPO LOGRADOURO
                dto.logradouro(), // LOGRADOURO
                dto.complemento(), // COMPLENTO
                dto.bairro(), // BAIRRO
                dto.numero(), // NUMERO
                30, //ID PAIS (DEFINIR PADRÃO)
                null, // ID CIDADE
                "Olímpia", // NOME CIDADE
                null, // ID UF
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

        String telefoneNumeros = dto.telefone().replaceAll("\\D", "");
        String ddi = telefoneNumeros.length() >= 12 ? telefoneNumeros.substring(0, 2) : "55";
        String ddd = telefoneNumeros.length() >= 10 ? telefoneNumeros.substring(2, 4) : "";
        String numero = telefoneNumeros.length() >= 8 ? telefoneNumeros.substring(4) : telefoneNumeros;

        ContatoTelefonicoDto contato = new ContatoTelefonicoDto(
                1, // TIPO CONTADO TELEFONE
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
	
}
