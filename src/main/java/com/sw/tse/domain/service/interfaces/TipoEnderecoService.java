package com.sw.tse.domain.service.interfaces;

import java.util.List;

import com.sw.tse.domain.model.api.response.TipoEnderecoApiResponse;

public interface TipoEnderecoService {
	public List<TipoEnderecoApiResponse> listarTiposEndereco();
}
