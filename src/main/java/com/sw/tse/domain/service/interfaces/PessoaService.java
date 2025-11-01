package com.sw.tse.domain.service.interfaces;

import java.util.Optional;

import com.sw.tse.api.dto.HospedeDto;
import com.sw.tse.domain.model.api.response.PessoaCpfApiResponse;
import com.sw.tse.domain.model.db.Contrato;

public interface PessoaService {
	
	/**
	 * Salva ou atualiza uma pessoa baseado no CPF informado no DTO
	 * Se a pessoa já existe (encontrada por CPF) e for proprietário do contrato (cessionário/cocessionário),
	 * os dados NÃO serão atualizados por segurança
	 * 
	 * @param hospedeDto Dados do hóspede/pessoa
	 * @param contrato Contrato para verificar se é proprietário
	 * @return ID da pessoa salva ou existente
	 */
	Long salvar(HospedeDto hospedeDto, Contrato contrato);
	
	/**
	 * @deprecated Usar salvar(HospedeDto, Contrato) para proteção de dados de proprietário
	 */
	@Deprecated
	default Long salvar(HospedeDto hospedeDto) {
		return salvar(hospedeDto, null);
	}
	
	Optional<PessoaCpfApiResponse> buscarPorCpf(String cpf);
	
	Optional<PessoaCpfApiResponse> buscarPorId(Long idPessoa);
	
	/**
	 * Busca pessoa por CPF com informações completas e verifica se é proprietário do contrato
	 * 
	 * @param cpf CPF da pessoa
	 * @param idContrato ID do contrato para verificar se é proprietário
	 * @return Dados completos da pessoa com flag isProprietario
	 */
	Optional<com.sw.tse.api.dto.PessoaComProprietarioResponse> buscarPorCpfCompleto(String cpf, Long idContrato);
}
