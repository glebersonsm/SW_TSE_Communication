package com.sw.tse.domain.model.api.request;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sw.tse.domain.model.api.enums.SexoEnum;
import com.sw.tse.domain.model.api.enums.TipoPessoaEnum;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PessoaApiRequest(
		
		@JsonProperty("IdPessoa") Long idPessoa,
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		@JsonProperty("DataCadastro") LocalDate dataCadastro,
	    @JsonProperty("IdTipoPessoa") TipoPessoaEnum idTipoPessoa, 
	    
	    @JsonProperty("RazaoSocial") String razaoSocial,
	    @JsonProperty("NomeFantasia") String nomeFantasia,
	    @JsonProperty("DataNascimento") LocalDate dataNascimento,
	    @JsonProperty("CpfCnpj") String cpfCnpj,
	    @JsonProperty("RgNumero") String rgNumero,
	    @JsonProperty("RgOrgaoExpedidor") String rgOrgaoExpedidor,
	    @JsonProperty("RgUf") String rgUf,
	    @JsonProperty("IdProfissao") Integer idProfissao,
	    @JsonProperty("IdSexo") SexoEnum idSexo, 
	
	    @JsonProperty("IdEstadoCivil") Integer idEstadoCivil,
	    @JsonProperty("IdRegimeCasamento") Integer idRegimeCasamento,
	    @JsonProperty("IdNacionalidade") Integer idNacionalidade,
	    @JsonProperty("IdIdioma") Integer idIdioma,
	    @JsonProperty("EnderecosPessoa") List<EnderecoPessoaApiRequest> enderecosPessoa,
	    @JsonProperty("EnderecosEmail") List<EnderecoEmailApiRequest> enderecosEmail,
	    @JsonProperty("ContatosTelefonicos") List<ContatoTelefonicoApiRequest> contatosTelefonicos
		
) {}
