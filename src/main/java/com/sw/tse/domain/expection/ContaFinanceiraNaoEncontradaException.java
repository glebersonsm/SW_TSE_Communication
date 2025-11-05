package com.sw.tse.domain.expection;

import java.util.List;

import lombok.Getter;

@Getter
public class ContaFinanceiraNaoEncontradaException extends RecursoNaoEncontradoException {
    private static final long serialVersionUID = 1L;
    
    private final List<Long> idsContas;
    
    public ContaFinanceiraNaoEncontradaException(List<Long> idsContas) {
        super(String.format("Nenhuma conta financeira encontrada para os IDs: %s", idsContas));
        this.idsContas = idsContas;
    }
    
    public ContaFinanceiraNaoEncontradaException(String mensagem) {
        super(mensagem);
        this.idsContas = null;
    }
}

