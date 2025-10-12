package com.sw.tse.domain.service.interfaces;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.sw.tse.api.dto.ContaFinanceiraClienteDto;
import com.sw.tse.domain.model.db.ContaFinanceira;

public interface ContaFinanceiraService {

    ContaFinanceira salvar(ContaFinanceira contaFinanceira);

    Optional<ContaFinanceira> buscarPorId(Long id);

    List<ContaFinanceira> listarTodas();

    List<ContaFinanceira> buscarPorContrato(Long idContrato);

    List<ContaFinanceira> buscarPorPessoa(Long idPessoa);

    List<ContaFinanceira> buscarContasEmAtraso();

    List<ContaFinanceira> buscarPorStatusPagamento(Boolean pago);

    List<ContaFinanceira> buscarPorTipoHistorico(String tipoHistorico);

    List<ContaFinanceira> buscarPorDestinoContaFinanceira(String destino);

    List<ContaFinanceira> buscarPorEmpresa(Long idEmpresa);

    List<ContaFinanceira> buscarPorPeriodoVencimento(LocalDateTime dataInicio, LocalDateTime dataFim);

    List<ContaFinanceira> buscarPorPeriodoPagamento(LocalDateTime dataInicio, LocalDateTime dataFim);

    Long contarContasNaoPagas();

    Double somarValoresContasNaoPagas();

    ContaFinanceira atualizar(ContaFinanceira contaFinanceira);

    void excluirPorId(Long id);

    void excluir(ContaFinanceira contaFinanceira);

    List<ContaFinanceira> buscarContasPorCliente(Long idCliente);

    List<ContaFinanceiraClienteDto> buscarContasClienteDto(Long idCliente);
}
