package com.sw.tse.domain.expection;

public class ReservaCanceladaNaoEditavelException extends RegraDeNegocioException {
    private static final long serialVersionUID = 1L;
    
    public ReservaCanceladaNaoEditavelException(Long idUtilizacaoContrato) {
        super(String.format("A reserva %d não pode ser editada pois está cancelada", idUtilizacaoContrato));
    }
}
