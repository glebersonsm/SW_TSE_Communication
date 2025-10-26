package com.sw.tse.domain.expection;

import java.time.LocalDate;

public class ReservaCheckinPassadoException extends RegraDeNegocioException {
    private static final long serialVersionUID = 1L;
    
    public ReservaCheckinPassadoException(Long idUtilizacaoContrato, LocalDate dataCheckin) {
        super(String.format("A reserva %d não pode ser editada pois o check-in (%s) já passou ou é hoje", 
            idUtilizacaoContrato, dataCheckin));
    }
}
