package com.sw.tse.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil {
    
    public static final Long ID_USUARIO_CLIENTE_FAKE = 1894L;
    public static final String TOKEN_USUARIO_CLIENTE_FAKE = "token-fake-cliente-2478";
    public static final Long ID_PESSOA_CLIENTE_FAKE = 7357L;
    
    public static Long getIdUsuarioCliente() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) auth;
            if (authToken.getDetails() instanceof JwtAuthenticationDetails) {
                JwtAuthenticationDetails details = (JwtAuthenticationDetails) authToken.getDetails();
                return details.getIdUsuarioCliente();
            }
        }
        return null;
    }
    
    public static String getTokenUsuarioCliente() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) auth;
            if (authToken.getDetails() instanceof JwtAuthenticationDetails) {
                JwtAuthenticationDetails details = (JwtAuthenticationDetails) authToken.getDetails();
                return details.getTokenUsuarioCliente();
            }
        }
        return null;
    }
    
    /**
     * Retorna o ID da pessoa cliente do token.
     * Quando o token contém idPessoaCliente = -1 (usuário demo 99999999999),
     * retorna ID_PESSOA_CLIENTE_FAKE para permitir testar o cálculo de juros
     * com feriados usando contas reais do banco.
     */
    public static Long getIdPessoaCliente() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) auth;
            if (authToken.getDetails() instanceof JwtAuthenticationDetails) {
                JwtAuthenticationDetails details = (JwtAuthenticationDetails) authToken.getDetails();
                Long idPessoa = details.getIdPessoaCliente();
                if (idPessoa != null && idPessoa == -1L) {
                    return ID_PESSOA_CLIENTE_FAKE;
                }
                return idPessoa;
            }
        }
        return null;
    }
    
    /**
     * Indica se a requisição está em modo simulação (usuário demo 99999999999).
     * No modo simulação, a memória de cálculo de juros e multas é retornada nas parcelas.
     */
    public static boolean isModoSimulacao() {
        Long idPessoaRaw = getIdPessoaClienteRaw();
        return idPessoaRaw != null && idPessoaRaw == -1L;
    }
    
    private static Long getIdPessoaClienteRaw() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) auth;
            if (authToken.getDetails() instanceof JwtAuthenticationDetails) {
                JwtAuthenticationDetails details = (JwtAuthenticationDetails) authToken.getDetails();
                return details.getIdPessoaCliente();
            }
        }
        return null;
    }
}
