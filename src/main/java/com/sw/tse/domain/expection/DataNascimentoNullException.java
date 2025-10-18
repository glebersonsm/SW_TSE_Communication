package com.sw.tse.domain.expection;

public class DataNascimentoNullException extends RuntimeException {
    
    public DataNascimentoNullException() {
        super("Data de nascimento não pode ser nula");
    }
}
