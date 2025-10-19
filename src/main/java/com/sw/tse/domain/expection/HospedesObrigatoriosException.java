package com.sw.tse.domain.expection;

public class HospedesObrigatoriosException extends DadoObrigatorioException {
    private static final long serialVersionUID = 1L;
    
    public HospedesObrigatoriosException() {
        super("Lista de hóspedes não pode ser nula ou vazia (obrigatório ao menos 1 hóspede)");
    }
}

