package com.sw.tse.domain.model.api.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(value =  AccessLevel.PRIVATE)
@NoArgsConstructor(access =  AccessLevel.PACKAGE)
public class LoginResponse {
    
    private TokenApiResponse tokenResponse;
    private OperadorSistemaCriadoApiResponse operadorResponse;
    private boolean novoOperadorCriado;
    private String emailUsuario;
    
   
    public static LoginResponse fromToken(TokenApiResponse tokenResponse) {
    	LoginResponse loginResponse = new LoginResponse();
    	loginResponse.setTokenResponse(tokenResponse);
    	loginResponse.setNovoOperadorCriado(false);
    	return loginResponse;
    }
    
    public static LoginResponse fromNovoOperadorCliente(OperadorSistemaCriadoApiResponse operadorResponse, String email) {
    	LoginResponse loginResponse = new LoginResponse();
    	loginResponse.setNovoOperadorCriado(true);
    	loginResponse.setOperadorResponse(operadorResponse);
    	loginResponse.setEmailUsuario(email);
    	return loginResponse;
    }
    
    
}