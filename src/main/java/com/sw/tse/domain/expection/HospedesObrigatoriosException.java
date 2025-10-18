package com.sw.tse.domain.expection;

public class HospedesObrigatoriosException extends RuntimeException {
    
    public HospedesObrigatoriosException() {
        super("Lista de hóspedes não pode ser nula ou vazia (obrigatório ao menos 1 hóspede)");
    }
}

