package com.sw.tse.api.dto;

public record JwtResponseDto(
    String token,
    String type,
    Long idUsuarioCliente,
    String tokenUsuarioCliente,
    Long idPessoaCliente
) {}
