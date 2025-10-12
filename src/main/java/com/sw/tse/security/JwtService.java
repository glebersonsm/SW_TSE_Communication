package com.sw.tse.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String secreteKey;
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.audience}")
    private String audience;
    @Value("${jwt.expiration.hours}")
    private long expirationHours;
    
    private static final String CLAIM_NAME = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name";
    private static final String CLAIM_USER_ID = "UserId";
    private static final String CLAIM_ROLE = "http://schemas.microsoft.com/ws/2008/06/identity/claims/role";
    private static final String CLAIM_PORTAL_ID = "UsuarioPortalIdentificador";
    private static final String CLAIM_ID_USUARIO_CLIENTE = "idUsuarioCliente";
    private static final String CLAIM_TOKEN_USUARIO_CLIENTE = "tokenUsuarioCliente";
    private static final String CLAIM_ID_PESSOA_CLIENTE = "idPessoaCliente";
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return gerarToken(claims, userDetails.getUsername());
    }
    
    public String generateToken(UserDetails userDetails, String userId, String usuarioPortalIdentificador) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_NAME, userDetails.getUsername());
        claims.put(CLAIM_USER_ID, userId);
        claims.put(CLAIM_ROLE, roles);
        claims.put(CLAIM_PORTAL_ID, usuarioPortalIdentificador);
        claims.put(CLAIM_ID_USUARIO_CLIENTE, JwtTokenUtil.ID_USUARIO_CLIENTE_FAKE);
        claims.put(CLAIM_TOKEN_USUARIO_CLIENTE, JwtTokenUtil.TOKEN_USUARIO_CLIENTE_FAKE);
        claims.put(CLAIM_ID_PESSOA_CLIENTE, JwtTokenUtil.ID_PESSOA_CLIENTE_FAKE);

        return gerarToken(claims, null);
    }
    
    public Claims validarToken(String token) throws JwtException {
        SecretKey key = getSignInKey();
        return Jwts.parser()
                .requireIssuer(issuer)
                .requireAudience(audience)
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    private String gerarToken(Map<String, Object> claims, String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationHours, ChronoUnit.HOURS))) 
                .signWith(getSignInKey())
                .compact();
    }
    
    private SecretKey getSignInKey() {
        byte[] keyBytes = this.secreteKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
