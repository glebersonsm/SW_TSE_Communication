package com.sw.tse.domain.service.impl.db;

import java.time.LocalDate;
import java.util.List;
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
import com.sw.tse.domain.model.dto.ContasPaginadasDto;
import com.sw.tse.domain.repository.ContaFinanceiraRepository;
import com.sw.tse.domain.service.interfaces.ContaFinanceiraService;
import com.sw.tse.domain.service.interfaces.TokenTseService;
import com.sw.tse.security.JwtTokenUtil;

import feign.FeignException;
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
    @Transactional(readOnly = true)
    public List<ContaFinanceiraClienteDto> buscarContasClienteDto(LocalDate vencimentoInicial, LocalDate vencimentoFinal, String status) {
        Long idCliente = JwtTokenUtil.getIdPessoaCliente();
        
        if (idCliente == null) {
            throw new TokenJwtInvalidoException("ID do cliente não está disponível no token de autenticação");
        }
        
        List<ContaFinanceira> contasFinanceiras = contaFinanceiraRepository.findContasPorClienteComFiltros(
                idCliente, 
                vencimentoInicial, 
                vencimentoFinal, 
                status
        );
        
        return contasFinanceiras.stream()
                .map(contaFinanceiraConverter::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ContasPaginadasDto buscarContasClienteDtoComPaginacao(
            LocalDate vencimentoInicial, 
            LocalDate vencimentoFinal, 
            String status,
            Integer numeroDaPagina,
            Integer quantidadeRegistrosRetornar) {
        
        Long idCliente = JwtTokenUtil.getIdPessoaCliente();
        
        if (idCliente == null) {
            throw new TokenJwtInvalidoException("ID do cliente não está disponível no token de autenticação");
        }
        
        if (numeroDaPagina == null || numeroDaPagina < 1) {
            numeroDaPagina = 1;
        }
        
        if (quantidadeRegistrosRetornar == null || quantidadeRegistrosRetornar < 1) {
            quantidadeRegistrosRetornar = 30;
        }
        
        Long totalRegistros = contaFinanceiraRepository.countContasPorClienteComFiltros(
                idCliente, 
                vencimentoInicial, 
                vencimentoFinal, 
                status
        );
        
        int totalPages = (int) Math.ceil((double) totalRegistros / quantidadeRegistrosRetornar);
        
        if (totalPages == 0) {
            totalPages = 1;
        }
        
        if (numeroDaPagina > totalPages) {
            numeroDaPagina = totalPages;
        }
        
        int offset = (numeroDaPagina - 1) * quantidadeRegistrosRetornar;
        
        List<ContaFinanceira> contasFinanceiras = contaFinanceiraRepository.findContasPorClienteComFiltrosPaginado(
                idCliente, 
                vencimentoInicial, 
                vencimentoFinal, 
                status,
                quantidadeRegistrosRetornar,
                offset
        );
        
        List<ContaFinanceiraClienteDto> contasDto = contasFinanceiras.stream()
                .map(contaFinanceiraConverter::toDto)
                .collect(Collectors.toList());
        
        return new ContasPaginadasDto(contasDto, numeroDaPagina, totalPages);
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
            // Gerar token
            String bearerToken = "Bearer " + tokenTseService.gerarToken();
            
            // Setar empresa de sessão
            Long idEmpresa = contaFinanceira.getEmpresa().getId();
            log.info("Setando empresa de sessão na API para o ID: {}", idEmpresa);
            try {
                operadorSistemaApiClient.setEmpresaSessao(idEmpresa, bearerToken, java.util.Collections.emptyMap());
                log.info("Empresa de sessão definida com sucesso para ID: {}", idEmpresa);
            } catch (Exception e) {
                log.error("Erro ao definir empresa de sessão para ID: {}", idEmpresa, e);
                throw new ApiTseException("Erro ao configurar empresa de sessão na API TSE", e);
            }
            
            // Gerar segunda via - Feign automaticamente lança ApiTseException com mensagem da API se houver erro
            log.info("Solicitando geração de segunda via do boleto para conta ID: {}", idContaFinanceira);
            byte[] pdfBytes = segundaViaBoletoApiClient.gerarSegundaViaBoleto(idContaFinanceira, bearerToken);
            
            log.info("Segunda via de boleto gerada com sucesso. Tamanho do PDF: {} bytes", pdfBytes.length);
            return pdfBytes;
            
        } catch (ApiTseException e) {
            // ApiTseException já tem a mensagem da API TSE
            log.error("Erro ao gerar segunda via de boleto para conta ID {}: {}", idContaFinanceira, e.getMessage());
            throw e;
        } catch (FeignException e) {
            log.error("Erro de comunicação com a API TSE para segunda via de boleto. Status: {}", e.status(), e);
            throw new ApiTseException("Erro de comunicação com a API TSE: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao gerar segunda via de boleto", e);
            throw new ApiTseException("Erro inesperado ao gerar segunda via de boleto: " + e.getMessage());
        }
    }
}
