package com.sw.tse.domain.expection;

public class PeriodoModeloCotaNullException extends RuntimeException {
    
    public PeriodoModeloCotaNullException() {
        super("Período modelo cota não pode ser nulo");
    }
}
