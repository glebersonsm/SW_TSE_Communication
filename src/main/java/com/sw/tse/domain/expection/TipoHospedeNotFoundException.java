package com.sw.tse.domain.expection;

public class TipoHospedeNotFoundException extends RecursoNaoEncontradoException {
    private static final long serialVersionUID = 1L;
    
    public TipoHospedeNotFoundException(Long idTipoHospede) {
        super(String.format("TipoHospede com ID %d não encontrado", idTipoHospede));
    }
}

