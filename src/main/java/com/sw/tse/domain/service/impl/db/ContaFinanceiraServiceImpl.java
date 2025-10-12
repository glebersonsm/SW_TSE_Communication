package com.sw.tse.domain.service.impl.db;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sw.tse.api.dto.ContaFinanceiraClienteDto;
import com.sw.tse.client.OperadorSistemaApiClient;
import com.sw.tse.client.SegundaViaBoletoApiClient;
import com.sw.tse.domain.converter.ContaFinanceiraConverter;
import com.sw.tse.domain.expection.ApiTseException;
import com.sw.tse.domain.expection.TokenJwtInvalidoException;
import com.sw.tse.domain.model.db.ContaFinanceira;
import com.sw.tse.domain.repository.ContaFinanceiraRepository;
import com.sw.tse.domain.service.interfaces.ContaFinanceiraService;
import com.sw.tse.domain.service.interfaces.TokenTseService;
import com.sw.tse.security.JwtTokenUtil;

import feign.FeignException;
import feign.Response;
import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnProperty(name = "database.enabled", havingValue = "true")
@Slf4j
public class ContaFinanceiraServiceImpl implements ContaFinanceiraService {

    @Autowired
    private ContaFinanceiraRepository contaFinanceiraRepository;

    @Autowired
    private ContaFinanceiraConverter contaFinanceiraConverter;

    @Autowired
    private SegundaViaBoletoApiClient segundaViaBoletoApiClient;

    @Autowired
    private OperadorSistemaApiClient operadorSistemaApiClient;

    @Autowired
    private TokenTseService tokenTseService;

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

    @Override
    @Transactional(readOnly = true)
    public byte[] gerarSegundaViaBoleto(Long idContaFinanceira) {
        log.info("Iniciando geração de segunda via de boleto para conta financeira ID: {}", idContaFinanceira);
        
        // Buscar conta financeira
        ContaFinanceira contaFinanceira = contaFinanceiraRepository.findById(idContaFinanceira)
                .orElseThrow(() -> new ApiTseException("Conta financeira não encontrada"));
        
        // Validar se pertence ao cliente autenticado
        Long idCliente = JwtTokenUtil.getIdPessoaCliente();
        if (idCliente == null) {
            throw new TokenJwtInvalidoException("ID do cliente não está disponível no token de autenticação");
        }
        
        if (!contaFinanceira.getPessoa().getIdPessoa().equals(idCliente)) {
            throw new ApiTseException("Conta financeira não pertence ao cliente autenticado");
        }
        

        if (contaFinanceira.getMeioPagamento() == null || 
            !"BOLETO".equals(contaFinanceira.getMeioPagamento().getCodMeioPagamento())) {
            throw new ApiTseException("Esta conta financeira não é um boleto");
        }
        

        String status = contaFinanceira.calcularStatus();
        if ("PAGO".equals(status)) {
            throw new ApiTseException("Não é possível gerar segunda via de boleto já pago");
        }
        
        log.info("Validações aprovadas. Status da conta: {}", status);
        
        try {

            String bearerToken = "Bearer " + tokenTseService.gerarToken();
            

            Long idEmpresa = contaFinanceira.getEmpresa().getId();
            log.info("Setando empresa de sessão na API para o ID: {}", idEmpresa);
            try {
                operadorSistemaApiClient.setEmpresaSessao(idEmpresa, bearerToken, java.util.Collections.emptyMap());
                log.info("Empresa de sessão definida com sucesso para ID: {}", idEmpresa);
            } catch (Exception e) {
                log.error("Erro ao definir empresa de sessão para ID: {}", idEmpresa, e);
                throw new ApiTseException("Erro ao configurar empresa de sessão na API TSE", e);
            }
            

            log.info("Solicitando geração de segunda via do boleto para conta ID: {}", idContaFinanceira);
            Response response = segundaViaBoletoApiClient.gerarSegundaViaBoleto(idContaFinanceira, bearerToken);
            

            if (response.status() < 200 || response.status() >= 300) {
                String errorBody = "";
                try {
                    if (response.body() != null) {
                        errorBody = new String(response.body().asInputStream().readAllBytes());
                    }
                } catch (Exception e) {
                    log.warn("Não foi possível ler o corpo da resposta de erro", e);
                }
                log.error("Erro ao gerar segunda via de boleto. Status: {}, Conta ID: {}, Corpo da resposta: {}", 
                         response.status(), idContaFinanceira, errorBody);
                throw new ApiTseException("Erro ao gerar segunda via de boleto na API TSE. Status: " + response.status());
            }
            

            byte[] pdfBytes = response.body().asInputStream().readAllBytes();
            
            log.info("Segunda via de boleto gerada com sucesso. Tamanho do PDF: {} bytes", pdfBytes.length);
            return pdfBytes;
            
        } catch (IOException e) {
            log.error("Erro ao processar resposta da API de segunda via de boleto", e);
            throw new ApiTseException("Erro ao processar PDF do boleto", e);
        } catch (FeignException e) {
            log.error("Erro de comunicação com a API TSE para segunda via de boleto. Status: {}, Corpo: {}", e.status(), e.contentUTF8(), e);
            throw new ApiTseException("Erro de comunicação com a API TSE", e);
        } catch (Exception e) {
            log.error("Erro inesperado ao gerar segunda via de boleto", e);
            throw new ApiTseException("Erro inesperado ao gerar segunda via de boleto", e);
        }
    }
}
