package com.sw.tse.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil {
    
    public static final Long ID_USUARIO_CLIENTE_FAKE = 2478L;
    public static final String TOKEN_USUARIO_CLIENTE_FAKE = "token-fake-cliente-2478";
    public static final Long ID_PESSOA_CLIENTE_FAKE = 37418L;
    
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
    
    public static Long getIdPessoaCliente() {
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
