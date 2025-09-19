package com.sw.tse.domain.service.interfaces;

import java.util.Optional;

import com.sw.tse.api.dto.HospedeDto;
import com.sw.tse.domain.model.api.response.PessoaCpfApiResponse;

public interface PessoaService {
	Long salvar(HospedeDto hospedeDto);
	Optional<PessoaCpfApiResponse> buscarPorCpf(String cpf);
}
