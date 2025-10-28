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
    private PessoaCpfApiResponse pessoa;
    private String cpf;
    
   
    public static LoginResponse fromToken(TokenApiResponse tokenResponse, PessoaCpfApiResponse pessoa, String cpf) {
    	LoginResponse loginResponse = new LoginResponse();
    	loginResponse.setTokenResponse(tokenResponse);
    	loginResponse.setNovoOperadorCriado(false);
    	loginResponse.setPessoa(pessoa);
    	loginResponse.setCpf(cpf);
    	return loginResponse;
    }
    
    public static LoginResponse fromNovoOperadorCliente(OperadorSistemaCriadoApiResponse operadorResponse, String email, PessoaCpfApiResponse pessoa, String cpf) {
    	LoginResponse loginResponse = new LoginResponse();
    	loginResponse.setNovoOperadorCriado(true);
    	loginResponse.setOperadorResponse(operadorResponse);
    	loginResponse.setEmailUsuario(email);
    	loginResponse.setPessoa(pessoa);
    	loginResponse.setCpf(cpf);
    	return loginResponse;
    }
    
    public LoginUnificadoResponse toLoginUnificado() {
        if (novoOperadorCriado) {
            // Novo operador criado
            return new LoginUnificadoResponse(
                null, // accessToken não disponível quando cria novo operador
                operadorResponse.idOperador(),
                pessoa.idPessoa(),
                operadorResponse.nomeOperador(),
                operadorResponse.login(),
                pessoa.email(),
                cpf,
                true,
                emailUsuario
            );
        } else {
            // Login com sucesso
            return new LoginUnificadoResponse(
                tokenResponse.accessToken(),
                tokenResponse.idUsuario(), // userId é o mesmo que idOperador
                pessoa.idPessoa(),
                pessoa.nome(),
                cpf,
                pessoa.email(),
                cpf,
                false,
                null
            );
        }
    }
    
}