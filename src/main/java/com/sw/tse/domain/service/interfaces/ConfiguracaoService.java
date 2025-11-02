package com.sw.tse.domain.service.interfaces;

import java.util.List;

import com.sw.tse.api.model.EmpresaTseDto;
import com.sw.tse.api.model.TorreDto;

public interface ConfiguracaoService {
    
    List<EmpresaTseDto> listarEmpresas();
    
    List<TorreDto> listarTorresPorEmpresa(Long idEmpresa);
}

