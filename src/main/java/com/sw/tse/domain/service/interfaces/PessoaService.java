package com.sw.tse.domain.service.interfaces;

import java.util.Optional;

import com.sw.tse.api.dto.DadosPessoaDto;
import com.sw.tse.api.dto.HospedeDto;
import com.sw.tse.api.dto.PessoaComProprietarioResponse;
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
	 * Busca pessoa por CPF com informações completas e verifica se é proprietária de ALGUM contrato
	 * 
	 * A verificação de proprietário é GLOBAL - se a pessoa for cessionária ou cocessionária
	 * de QUALQUER contrato no sistema, a flag isProprietario será true.
	 * 
	 * @param cpf CPF da pessoa
	 * @param idContrato ID do contrato (parâmetro mantido para compatibilidade, mas não usado na verificação)
	 * @return Dados completos da pessoa com flag isProprietario
	 */
	Optional<PessoaComProprietarioResponse> buscarPorCpfCompleto(String cpf, Long idContrato);
	
	/**
	 * Retorna os dados da pessoa logada incluindo endereço, email e telefone
	 * 
	 * @param pessoaId ID da pessoa
	 * @return Dados da pessoa com endereço
	 */
	DadosPessoaDto obterDadosPessoaLogada(Long pessoaId);
}
