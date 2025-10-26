package com.sw.tse.domain.expection;

public class HospedeNotFoundException extends RecursoNaoEncontradoException {
    private static final long serialVersionUID = 1L;
    
    public HospedeNotFoundException(Long idHospede) {
        super(String.format("Hóspede com ID %d não encontrado", idHospede));
    }
}
