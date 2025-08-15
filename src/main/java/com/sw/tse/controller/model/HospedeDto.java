package com.sw.tse.controller.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

public record HospedeDto (	
	@NotNull
	@JsonProperty("id") Long id,
	@JsonProperty("idHospede") Long idHospede,
	@JsonProperty("idTipoHospede") String tipoHospede,
	@JsonProperty("clienteId") Long clienteId,
	@JsonProperty("principal") String principal,
	@NotNull
	@JsonProperty("nome") String nome,
	@JsonProperty("cpf") String cpf,
	@NotNull
	@JsonProperty("dataNascimento") LocalDate dataNascimento,
	@JsonProperty("email") String email,
	@JsonProperty("telefone") String telefone,
	@JsonProperty("sexo") String sexo,
	@JsonProperty("codigoIbge") String codigoIbge,
	

	@JsonProperty("logradouro") String logradouro,
	@JsonProperty("numero") String numero,
	@JsonProperty("bairro") String bairro,
	@JsonProperty("complemento") String complemento,
	
	@JsonProperty("cep") String cep,
	@NotNull
	@JsonProperty("checkIn") LocalDate dataCheckin,
	@NotNull
	@JsonProperty("checkOut") LocalDate dataCheckOut
	
) {}
