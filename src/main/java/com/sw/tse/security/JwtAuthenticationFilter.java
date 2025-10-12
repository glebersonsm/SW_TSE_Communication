package com.sw.tse.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import org.springframework.lang.NonNull;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final JwtService jwtService;
    private final AuthenticationEntryPoint authenticationEntryPoint; 
    
    private static final String CLAIM_NAME = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name";
    private static final String CLAIM_ROLE = "http://schemas.microsoft.com/ws/2008/06/identity/claims/role";
    private static final String CLAIM_ID_USUARIO_CLIENTE = "idUsuarioCliente";
    private static final String CLAIM_TOKEN_USUARIO_CLIENTE = "tokenUsuarioCliente";
    private static final String CLAIM_ID_PESSOA_CLIENTE = "idPessoaCliente";
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            final String jwt = authHeader.substring(7);
            Claims claims = jwtService.validarToken(jwt);
            
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = claims.get(CLAIM_NAME, String.class);

                @SuppressWarnings("unchecked")
                List<String> roles = claims.get(CLAIM_ROLE, List.class);
                if (roles == null) {
                    roles = Collections.emptyList();
                }

                List<GrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());

                UserDetails userDetails = new User(username, "", authorities);


                Long idUsuarioCliente = claims.get(CLAIM_ID_USUARIO_CLIENTE, Long.class);
                String tokenUsuarioCliente = claims.get(CLAIM_TOKEN_USUARIO_CLIENTE, String.class);
                Long idPessoaCliente = claims.get(CLAIM_ID_PESSOA_CLIENTE, Long.class);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                JwtAuthenticationDetails details = new JwtAuthenticationDetails(request, idUsuarioCliente, tokenUsuarioCliente, idPessoaCliente);
                authToken.setDetails(details);
                
                SecurityContextHolder.getContext().setAuthentication(authToken);
                filterChain.doFilter(request, response);
            }
        } catch (JwtException e) {
            BadCredentialsException authException = new BadCredentialsException(traduzirJwtException(e), e);
            this.authenticationEntryPoint.commence(request, response, authException);
        }
    }
    
    private String traduzirJwtException(JwtException e) {
        if (e instanceof ExpiredJwtException) {
            return "Seu token de acesso expirou. Por favor, autentique-se novamente.";
        }
        if (e instanceof SignatureException) {
            return "O token fornecido possui uma assinatura inválida.";
        }
        if (e instanceof IncorrectClaimException) {
            return "O token contém dados de autenticação inválidos (emissor ou audiência incorretos).";
        }
        return "O token de acesso fornecido é inválido ou está mal formatado.";
    }
}
