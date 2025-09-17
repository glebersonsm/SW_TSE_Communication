package com.sw.tse.domain.service.interfaces;

import java.util.List;

import com.sw.tse.domain.model.api.request.FiltroRelatorioCustomizadoApiRequest;

public interface RelatorioCustomizadoService {
	public <T> List<T> buscarRelatorioGenerico(Long idRelatorio, List<FiltroRelatorioCustomizadoApiRequest> filtros, Class<T> tipoDoElemento);
}
