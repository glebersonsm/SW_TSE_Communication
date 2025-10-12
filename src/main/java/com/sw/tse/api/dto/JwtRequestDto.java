package com.sw.tse.api.dto;

public record JwtRequestDto(
    String username,
    String password
) {}
