package com.sw.tse.domain.service.interfaces;

import java.util.List;

import com.sw.tse.domain.model.api.response.TipoDocumentoPessoaDto;


public interface TipoDocumentoPessoaService {

    List<TipoDocumentoPessoaDto> listarTiposDocumento();
    
}
