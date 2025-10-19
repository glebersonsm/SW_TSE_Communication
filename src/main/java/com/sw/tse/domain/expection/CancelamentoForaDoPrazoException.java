package com.sw.tse.domain.expection;

public class CancelamentoForaDoPrazoException extends RegraDeNegocioException {
    
    private static final long serialVersionUID = 1L;
    
    public CancelamentoForaDoPrazoException(String tipoUtilizacao, int diasMinimos, int diasRestantes) {
        super(String.format(
            "Cancelamento de %s requer no minimo %d dias antes do check-in. Restam apenas %d dias", 
            tipoUtilizacao, diasMinimos, diasRestantes
        ));
    }
}

