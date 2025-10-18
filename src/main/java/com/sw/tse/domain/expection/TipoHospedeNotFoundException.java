package com.sw.tse.domain.expection;

public class TipoHospedeNotFoundException extends RuntimeException {
    
    public TipoHospedeNotFoundException(Long idTipoHospede) {
        super(String.format("TipoHospede com ID %d não encontrado", idTipoHospede));
    }
}

