package com.sw.tse.api.dto;

public record UsuarioClienteDto(
		String tokenCliente,
		Long idUsuarioCliente,
		Long idPesssoaCliente
) {}
