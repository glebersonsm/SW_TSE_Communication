package com.sw.tse.domain.expection;

public class SexoInvalidoException extends DadoInvalidoException {
    private static final long serialVersionUID = 1L;
    
    public SexoInvalidoException(String sexoInformado) {
        super(String.format("Sexo '%s' é inválido. Use 'M' para Masculino ou 'F' para Feminino", sexoInformado));
    }
}

