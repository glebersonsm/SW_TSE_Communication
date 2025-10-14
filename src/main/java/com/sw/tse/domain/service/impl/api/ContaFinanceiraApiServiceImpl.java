package com.sw.tse.domain.service.impl.api;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sw.tse.api.dto.ContaFinanceiraClienteDto;
import com.sw.tse.client.SegundaViaBoletoApiClient;
import com.sw.tse.domain.expection.ApiTseException;
import com.sw.tse.domain.expection.TokenJwtInvalidoException;
import com.sw.tse.domain.model.db.ContaFinanceira;
import com.sw.tse.domain.service.interfaces.ContaFinanceiraService;
import com.sw.tse.domain.service.interfaces.TokenTseService;
import com.sw.tse.security.JwtTokenUtil;

import feign.FeignException;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(name = "database.enabled", havingValue = "false", matchIfMissing = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class ContaFinanceiraApiServiceImpl implements ContaFinanceiraService {

    private final SegundaViaBoletoApiClient segundaViaBoletoApiClient;
    private final TokenTseService tokenTseService;

    @Override
    @Transactional(readOnly = true)
    public byte[] gerarSegundaViaBoleto(Long idContaFinanceira) {
        log.info("Iniciando geração de segunda via de boleto via API para conta financeira ID: {}", idContaFinanceira);
        

        Long idCliente = JwtTokenUtil.getIdPessoaCliente();
        if (idCliente == null) {
            throw new TokenJwtInvalidoException("ID do cliente não está disponível no token de autenticação");
        }
        
        log.info("Cliente autenticado com ID: {}", idCliente);
        
        try {
            // Gerar token interno
            String bearerToken = "Bearer " + tokenTseService.gerarToken();
            
            // Setar empresa na sessão da API (necessário para segunda via)
            // Como não temos acesso à conta financeira via DB, vamos tentar sem setar empresa primeiro
            // Se falhar, o erro será tratado abaixo
            log.info("Tentando gerar segunda via sem setar empresa de sessão");
            
            log.info("Solicitando geração de segunda via do boleto para conta ID: {}", idContaFinanceira);
            Response response = segundaViaBoletoApiClient.gerarSegundaViaBoleto(idContaFinanceira, bearerToken);
            
            // Verificar status da resposta
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
            
            log.info("Segunda via de boleto gerada com sucesso via API. Tamanho do PDF: {} bytes", pdfBytes.length);
            return pdfBytes;
            
        } catch (IOException e) {
            log.error("Erro ao processar resposta da API de segunda via de boleto", e);
            throw new ApiTseException("Erro ao processar PDF do boleto", e);
        } catch (FeignException e) {
            log.error("Erro de comunicação com a API TSE para segunda via de boleto. Status: {}, Corpo: {}", e.status(), e.contentUTF8(), e);
            throw new ApiTseException("Erro de comunicação com a API TSE", e);
        } catch (Exception e) {
            log.error("Erro inesperado ao gerar segunda via de boleto via API", e);
            throw new ApiTseException("Erro inesperado ao gerar segunda via de boleto", e);
        }
    }

    // Métodos não implementados para API - retornam exceção
    @Override
    public ContaFinanceira salvar(ContaFinanceira contaFinanceira) {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }

    @Override
    public Optional<ContaFinanceira> buscarPorId(Long id) {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }

    @Override
    public List<ContaFinanceira> listarTodas() {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }

    @Override
    public List<ContaFinanceira> buscarPorContrato(Long idContrato) {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }

    @Override
    public List<ContaFinanceira> buscarPorPessoa(Long idPessoa) {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }

    @Override
    public List<ContaFinanceira> buscarContasEmAtraso() {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }

    @Override
    public List<ContaFinanceira> buscarPorStatusPagamento(Boolean pago) {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }

    @Override
    public List<ContaFinanceira> buscarPorTipoHistorico(String tipoHistorico) {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }

    @Override
    public List<ContaFinanceira> buscarPorDestinoContaFinanceira(String destino) {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }

    @Override
    public List<ContaFinanceira> buscarPorEmpresa(Long idEmpresa) {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }

    @Override
    public List<ContaFinanceira> buscarPorPeriodoVencimento(LocalDateTime dataInicio, LocalDateTime dataFim) {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }

    @Override
    public List<ContaFinanceira> buscarPorPeriodoPagamento(LocalDateTime dataInicio, LocalDateTime dataFim) {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }

    @Override
    public Long contarContasNaoPagas() {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }

    @Override
    public Double somarValoresContasNaoPagas() {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }

    @Override
    public ContaFinanceira atualizar(ContaFinanceira contaFinanceira) {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }

    @Override
    public void excluirPorId(Long id) {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }

    @Override
    public void excluir(ContaFinanceira contaFinanceira) {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }

    @Override
    public List<ContaFinanceira> buscarContasPorCliente(Long idCliente) {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }

    @Override
    public List<ContaFinanceiraClienteDto> buscarContasClienteDto() {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }
}
