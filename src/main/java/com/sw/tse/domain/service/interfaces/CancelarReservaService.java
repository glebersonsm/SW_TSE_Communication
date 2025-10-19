package com.sw.tse.domain.service.interfaces;

public interface CancelarReservaService {
    
    void cancelarReserva(Long idUtilizacaoContrato, String motivo, Long idPessoaCliente);
}

