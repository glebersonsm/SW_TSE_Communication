package com.sw.tse.domain.expection;

import lombok.Getter;

@Getter
public class CapacidadeUhExcedidaException extends RegraDeNegocioException {
    private static final long serialVersionUID = 1L;
    
    private final Integer capacidadeMaxima;
    private final Integer quantidadeHospedes;
    
    public CapacidadeUhExcedidaException(Integer capacidadeMaxima, Integer quantidadeHospedes) {
        super(String.format("Capacidade da unidade hoteleira excedida. " +
                "Capacidade máxima: %d hóspedes, quantidade informada: %d hóspedes", 
                capacidadeMaxima, quantidadeHospedes));
        this.capacidadeMaxima = capacidadeMaxima;
        this.quantidadeHospedes = quantidadeHospedes;
    }
}
