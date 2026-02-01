package com.sw.tse.domain.expection;

import lombok.Getter;

@Getter
public class TipoValidacaoIntegralizacaoInvalidoException extends RegraDeNegocioException {
    private static final long serialVersionUID = 1L;
    
    private final String tipoValidacaoRecebido;
    
    public TipoValidacaoIntegralizacaoInvalidoException(String tipoValidacaoRecebido) {
        super(String.format("Tipo de validação de integralização inválido: '%s'. Valores aceitos: 'FIXO' ou 'PERCENTUAL'", 
            tipoValidacaoRecebido));
        this.tipoValidacaoRecebido = tipoValidacaoRecebido;
    }
}
