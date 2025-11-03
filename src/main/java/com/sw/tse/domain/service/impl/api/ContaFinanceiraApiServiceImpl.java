package com.sw.tse.domain.service.impl.api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sw.tse.api.dto.ContaFinanceiraClienteDto;
import com.sw.tse.client.SegundaViaBoletoApiClient;
import com.sw.tse.domain.expection.ApiTseException;
import com.sw.tse.domain.expection.TokenJwtInvalidoException;
import com.sw.tse.domain.model.dto.ContasPaginadasDto;
import com.sw.tse.domain.service.interfaces.ContaFinanceiraService;
import com.sw.tse.domain.service.interfaces.TokenTseService;
import com.sw.tse.security.JwtTokenUtil;

import feign.FeignException;
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
                    
                    // Como não temos acesso à conta financeira via DB, vamos tentar sem setar empresa primeiro
                    // Se falhar, o erro será tratado pelo ErrorDecoder
                    log.info("Tentando gerar segunda via sem setar empresa de sessão");
                    
                    // Gerar segunda via - Feign automaticamente lança ApiTseException com mensagem da API se houver erro
                    log.info("Solicitando geração de segunda via do boleto para conta ID: {}", idContaFinanceira);
                    byte[] pdfBytes = segundaViaBoletoApiClient.gerarSegundaViaBoleto(idContaFinanceira, bearerToken);
                    
                    log.info("Segunda via de boleto gerada com sucesso via API. Tamanho do PDF: {} bytes", pdfBytes.length);
                    return pdfBytes;
                    
                } catch (ApiTseException e) {
                    // ApiTseException já tem a mensagem da API TSE
                    log.error("Erro ao gerar segunda via de boleto para conta ID {}: {}", idContaFinanceira, e.getMessage());
                    throw e;
                } catch (FeignException e) {
                    log.error("Erro de comunicação com a API TSE para segunda via de boleto. Status: {}", e.status(), e);
                    throw new ApiTseException("Erro de comunicação com a API TSE: " + e.getMessage());
                } catch (Exception e) {
                    log.error("Erro inesperado ao gerar segunda via de boleto via API", e);
                    throw new ApiTseException("Erro inesperado ao gerar segunda via de boleto: " + e.getMessage());
                }
    }

    @Override
    public List<ContaFinanceiraClienteDto> buscarContasClienteDto(LocalDate vencimentoInicial, LocalDate vencimentoFinal, String status) {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }
    
    @Override
    public ContasPaginadasDto buscarContasClienteDtoComPaginacao(
            LocalDate vencimentoInicial, 
            LocalDate vencimentoFinal, 
            String status,
            Long empresaId,
            Integer numeroDaPagina,
            Integer quantidadeRegistrosRetornar) {
        throw new UnsupportedOperationException("Operação não suportada na implementação API");
    }
}
