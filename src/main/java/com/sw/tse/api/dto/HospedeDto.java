package com.sw.tse.api.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record HospedeDto (	
	@JsonProperty("id") Long id,
	@JsonProperty("idHospede") Long idHospede,
	@JsonProperty("idTipoHospede") String tipoHospede,
	@JsonProperty("clienteId") Long clienteId,
	@NotNull(message = "Campo 'principal' é obrigatório")
	@Pattern(regexp = "S|N", message = "Campo 'principal' deve ser 'S' ou 'N'")
	@JsonProperty("principal") String principal,
	@NotNull
	@JsonProperty("nome") String nome,
	
	@NotNull
	@NotBlank
	@JsonProperty("tipoDocumento") String tipoDocumento,
	@JsonProperty("cpf") String numeroDocumento,
	
	@NotNull
	@JsonProperty("dataNascimento") LocalDate dataNascimento,
	@JsonProperty("email") String email,
	@JsonProperty("ddi") String ddi,
	@JsonProperty("ddd") String ddd,
	@JsonProperty("telefone") String telefone,
	@NotNull(message = "Sexo é obrigatório")
	@JsonProperty("sexo") String sexo,
	@JsonProperty("codigoIbge") String codigoIbge,
	
	@JsonProperty("logradouro") String logradouro,
	@JsonProperty("numero") String numero,
	@JsonProperty("bairro") String bairro,
	@JsonProperty("complemento") String complemento,
	
	@JsonProperty("cep") String cep,
	@JsonProperty("checkIn") LocalDate dataCheckin,
	@JsonProperty("checkOut") LocalDate dataCheckOut
	
) {}
