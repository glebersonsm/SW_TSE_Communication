package com.sw.tse.domain.service.interfaces;

import java.util.List;

import com.sw.tse.domain.model.api.dto.ContratoClienteApiResponse;

public interface ContratoClienteService {

    List<ContratoClienteApiResponse> buscarContratosCliente();   
    List<ContratoClienteApiResponse> buscarContratosPorIdUsuario(Long idUsuario);
    
}