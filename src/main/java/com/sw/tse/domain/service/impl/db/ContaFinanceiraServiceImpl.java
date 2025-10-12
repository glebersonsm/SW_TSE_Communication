package com.sw.tse.domain.service.impl.db;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sw.tse.api.dto.ContaFinanceiraClienteDto;
import com.sw.tse.domain.converter.ContaFinanceiraConverter;
import com.sw.tse.domain.expection.TokenJwtInvalidoException;
import com.sw.tse.domain.model.db.ContaFinanceira;
import com.sw.tse.domain.repository.ContaFinanceiraRepository;
import com.sw.tse.domain.service.interfaces.ContaFinanceiraService;
import com.sw.tse.security.JwtTokenUtil;

@Service
@ConditionalOnProperty(name = "database.enabled", havingValue = "true")
public class ContaFinanceiraServiceImpl implements ContaFinanceiraService {

    @Autowired
    private ContaFinanceiraRepository contaFinanceiraRepository;

    @Autowired
    private ContaFinanceiraConverter contaFinanceiraConverter;

    @Override
    public ContaFinanceira salvar(ContaFinanceira contaFinanceira) {
        return contaFinanceiraRepository.save(contaFinanceira);
    }

    @Override
    public Optional<ContaFinanceira> buscarPorId(Long id) {
        return contaFinanceiraRepository.findById(id);
    }

    @Override
    public List<ContaFinanceira> listarTodas() {
        return contaFinanceiraRepository.findAll();
    }

    @Override
    public List<ContaFinanceira> buscarPorContrato(Long idContrato) {
        return contaFinanceiraRepository.findByContratoId(idContrato);
    }

    @Override
    public List<ContaFinanceira> buscarPorPessoa(Long idPessoa) {
        return contaFinanceiraRepository.findByPessoaIdPessoa(idPessoa);
    }

    @Override
    public List<ContaFinanceira> buscarContasEmAtraso() {
        return contaFinanceiraRepository.findContasEmAtraso(LocalDateTime.now());
    }

    @Override
    public List<ContaFinanceira> buscarPorStatusPagamento(Boolean pago) {
        return contaFinanceiraRepository.findByPago(pago);
    }

    @Override
    public List<ContaFinanceira> buscarPorTipoHistorico(String tipoHistorico) {
        return contaFinanceiraRepository.findByTipoHistorico(tipoHistorico);
    }

    @Override
    public List<ContaFinanceira> buscarPorDestinoContaFinanceira(String destino) {
        return contaFinanceiraRepository.findByDestinoContaFinanceira(destino);
    }

    @Override
    public List<ContaFinanceira> buscarPorEmpresa(Long idEmpresa) {
        return contaFinanceiraRepository.findByEmpresaId(idEmpresa);
    }

    @Override
    public List<ContaFinanceira> buscarPorPeriodoVencimento(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return contaFinanceiraRepository.findByDataVencimentoBetween(dataInicio, dataFim);
    }

    @Override
    public List<ContaFinanceira> buscarPorPeriodoPagamento(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return contaFinanceiraRepository.findByDataPagamentoBetween(dataInicio, dataFim);
    }

    @Override
    public Long contarContasNaoPagas() {
        return contaFinanceiraRepository.countContasNaoPagas();
    }

    @Override
    public Double somarValoresContasNaoPagas() {
        return contaFinanceiraRepository.sumValoresContasNaoPagas();
    }

    @Override
    public ContaFinanceira atualizar(ContaFinanceira contaFinanceira) {
        return contaFinanceiraRepository.save(contaFinanceira);
    }

    @Override
    public void excluirPorId(Long id) {
        contaFinanceiraRepository.deleteById(id);
    }

    @Override
    public void excluir(ContaFinanceira contaFinanceira) {
        contaFinanceiraRepository.delete(contaFinanceira);
    }

    @Override
    public List<ContaFinanceira> buscarContasPorCliente(Long idCliente) {
        return contaFinanceiraRepository.findContasPorCliente(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContaFinanceiraClienteDto> buscarContasClienteDto() {
        Long idCliente = JwtTokenUtil.getIdPessoaCliente();
        
        if (idCliente == null) {
            throw new TokenJwtInvalidoException("ID do cliente não está disponível no token de autenticação");
        }
        
        List<ContaFinanceira> contasFinanceiras = contaFinanceiraRepository.findContasPorCliente(idCliente);
        
        return contasFinanceiras.stream()
                .map(contaFinanceiraConverter::toDto)
                .collect(Collectors.toList());
    }
}
