package com.sw.tse.domain.expection;

public class UsuarioResponsavelNullException extends RuntimeException {
    
    public UsuarioResponsavelNullException() {
        super("Usuário responsável não pode ser nulo");
    }
}
