package com.sw.tse.security;

import jakarta.servlet.http.HttpServletRequest;

public class JwtAuthenticationDetails {
    private final String remoteAddress;
    private final String sessionId;
    private final Long idUsuarioCliente;
    private final String tokenUsuarioCliente;
    private final Long idPessoaCliente;
    
    public JwtAuthenticationDetails(HttpServletRequest request, Long idUsuarioCliente, String tokenUsuarioCliente, Long idPessoaCliente) {
        this.remoteAddress = request.getRemoteAddr();
        this.sessionId = request.getSession(false) != null ? request.getSession(false).getId() : null;
        this.idUsuarioCliente = idUsuarioCliente;
        this.tokenUsuarioCliente = tokenUsuarioCliente;
        this.idPessoaCliente = idPessoaCliente;
    }
    
    public String getRemoteAddress() {
        return remoteAddress;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public Long getIdUsuarioCliente() {
        return idUsuarioCliente;
    }
    
    public String getTokenUsuarioCliente() {
        return tokenUsuarioCliente;
    }
    
    public Long getIdPessoaCliente() {
        return idPessoaCliente;
    }
}
