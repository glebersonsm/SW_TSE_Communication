package com.sw.tse.domain.service.impl.db;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sw.tse.api.dto.ContaFinanceiraClienteDto;
import com.sw.tse.client.OperadorSistemaApiClient;
import com.sw.tse.client.SegundaViaBoletoApiClient;
import com.sw.tse.core.util.ParametroFinanceiroHelper;
import com.sw.tse.domain.converter.ContaFinanceiraConverter;
import com.sw.tse.domain.expection.ApiTseException;
import com.sw.tse.domain.expection.TokenJwtInvalidoException;
import com.sw.tse.domain.model.db.ContaFinanceira;
import com.sw.tse.domain.model.db.ParametroFinanceiro;
import com.sw.tse.domain.model.dto.ContasPaginadasDto;
import com.sw.tse.domain.repository.ContaFinanceiraRepository;
import com.sw.tse.domain.service.interfaces.ContaFinanceiraService;
import com.sw.tse.domain.service.interfaces.TokenTseService;
import com.sw.tse.security.JwtTokenUtil;

import feign.FeignException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    
    @Autowired
    private com.sw.tse.domain.repository.ParametroFinanceiroRepository parametroFinanceiroRepository;
    
    @PersistenceContext
    private EntityManager entityManager;


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
        
        // Inicializar relacionamentos lazy necessários para cálculo de juros e multa
        inicializarRelacionamentosParaCalculo(contasFinanceiras);
        
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
            Long empresaId,
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
                status,
                empresaId
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
                empresaId,
                quantidadeRegistrosRetornar,
                offset
        );
        
        // Inicializar relacionamentos lazy necessários para cálculo de juros e multa
        inicializarRelacionamentosParaCalculo(contasFinanceiras);
        
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
    
    /**
     * Inicializa os relacionamentos lazy necessários para o cálculo de juros e multa.
     * Como as queries nativas não fazem fetch automático dos relacionamentos lazy,
     * precisamos fazer fetch manual usando JPQL com JOIN FETCH e substituir as contas na lista.
     * 
     * Também pré-carrega os parâmetros financeiros em batch para evitar N+1 queries.
     * 
     * @param contasFinanceiras Lista de contas financeiras a serem inicializadas
     */
    private void inicializarRelacionamentosParaCalculo(List<ContaFinanceira> contasFinanceiras) {
        if (contasFinanceiras.isEmpty()) {
            return;
        }
        
        log.info("Inicializando relacionamentos para {} contas financeiras", contasFinanceiras.size());
        
        // Extrair IDs das contas
        List<Long> idsContas = contasFinanceiras.stream()
                .map(ContaFinanceira::getId)
                .collect(Collectors.toList());
        
        // Buscar contas com JOIN FETCH para carregar relacionamentos
        String jpql = """
            SELECT DISTINCT cf 
            FROM ContaFinanceira cf
            LEFT JOIN FETCH cf.carteiraBoleto
            LEFT JOIN FETCH cf.empresa
            LEFT JOIN FETCH cf.meioPagamento
            WHERE cf.id IN :ids
            """;
        
        List<ContaFinanceira> contasComRelacionamentos = entityManager.createQuery(jpql, ContaFinanceira.class)
                .setParameter("ids", idsContas)
                .getResultList();
        
        log.info("Buscadas {} contas com relacionamentos carregados", contasComRelacionamentos.size());
        
        // Pré-carregar parâmetros financeiros em batch para evitar N+1 queries
        // Extrair IDs únicos de empresas que não têm carteiraBoleto (precisam de ParametroFinanceiro)
        List<Long> idsEmpresas = contasComRelacionamentos.stream()
                .filter(cf -> cf.getEmpresa() != null && cf.getCarteiraBoleto() == null)
                .map(cf -> cf.getEmpresa().getId())
                .distinct()
                .collect(Collectors.toList());
        
        if (!idsEmpresas.isEmpty()) {
            log.info("Pré-carregando {} parâmetros financeiros em batch", idsEmpresas.size());
            List<ParametroFinanceiro> parametros = parametroFinanceiroRepository.findByEmpresaIds(idsEmpresas);
            
            // Pré-popular cache do ParametroFinanceiroHelper para evitar N+1 queries
            for (ParametroFinanceiro parametro : parametros) {
                // Usar idTenant (que é o mesmo que idEmpresa) ou empresa.getId()
                Long idEmpresa = null;
                if (parametro.getEmpresa() != null) {
                    idEmpresa = parametro.getEmpresa().getId();
                } else if (parametro.getIdTenant() != null) {
                    idEmpresa = parametro.getIdTenant().longValue();
                }
                
                if (idEmpresa != null) {
                    ParametroFinanceiroHelper.preencherCache(idEmpresa, parametro);
                }
            }
            
            log.info("Parâmetros financeiros pré-carregados e cache populado: {}", parametros.size());
        }
        
        // Criar um mapa para acesso rápido
        Map<Long, ContaFinanceira> mapaContas = contasComRelacionamentos.stream()
                .collect(Collectors.toMap(ContaFinanceira::getId, conta -> conta, (a, b) -> a));
        
        // Substituir as contas na lista original pelas contas com relacionamentos carregados
        for (int i = 0; i < contasFinanceiras.size(); i++) {
            ContaFinanceira contaOriginal = contasFinanceiras.get(i);
            ContaFinanceira contaComRelacionamentos = mapaContas.get(contaOriginal.getId());
            if (contaComRelacionamentos != null) {
                contasFinanceiras.set(i, contaComRelacionamentos);
                
                // Verificar se meioPagamento foi carregado
                if (contaComRelacionamentos.getMeioPagamento() != null) {
                    log.debug("Conta ID {} - MeioPagamento carregado: {} (ID: {})", 
                            contaComRelacionamentos.getId(),
                            contaComRelacionamentos.getMeioPagamento().getDescricao(),
                            contaComRelacionamentos.getMeioPagamento().getIdMeioPagamento());
                } else {
                    log.debug("Conta ID {} - MeioPagamento é NULL", contaComRelacionamentos.getId());
                }
                
                // Log detalhado para debug - especialmente para contas vencidas
                String status = contaComRelacionamentos.calcularStatus();
                BigDecimal juros = contaComRelacionamentos.calcularJuros();
                BigDecimal multa = contaComRelacionamentos.calcularMulta();
                
                if ("VENCIDO".equals(status)) {
                    // Informações detalhadas sobre a configuração
                    String infoCarteira = "null";
                    String infoEmpresa = "null";
                    
                    if (contaComRelacionamentos.getCarteiraBoleto() != null) {
                        BigDecimal valorJurosMora = contaComRelacionamentos.getCarteiraBoleto().getValorJurosDeMora();
                        BigDecimal valorMulta = contaComRelacionamentos.getCarteiraBoleto().getValorMulta();
                        infoCarteira = String.format("ID %d, JurosMora: %s, Multa: %s", 
                                contaComRelacionamentos.getCarteiraBoleto().getIdCarteiraBoleto(),
                                valorJurosMora != null ? valorJurosMora.toString() : "null",
                                valorMulta != null ? valorMulta.toString() : "null");
                    }
                    
                    if (contaComRelacionamentos.getEmpresa() != null) {
                        infoEmpresa = String.format("ID %d", contaComRelacionamentos.getEmpresa().getId());
                    }
                    
                    log.info("Conta VENCIDA ID {} - CarteiraBoleto: {}, Empresa: {}, Juros: {}, Multa: {}, ValorAtualizado: {}", 
                            contaComRelacionamentos.getId(), 
                            infoCarteira,
                            infoEmpresa,
                            juros,
                            multa,
                            contaComRelacionamentos.calcularValorAtualizado());
                    
                    // Log adicional se não houver juros/multa calculados
                    if (juros.compareTo(BigDecimal.ZERO) == 0 && multa.compareTo(BigDecimal.ZERO) == 0) {
                        log.warn("ATENÇÃO: Conta VENCIDA ID {} não tem juros/multa calculados! CarteiraBoleto: {}, Empresa: {}", 
                                contaComRelacionamentos.getId(),
                                infoCarteira,
                                infoEmpresa);
                    }
                } else {
                    log.debug("Conta ID {} - Status: {}, CarteiraBoleto: {}, Empresa: {}", 
                            contaComRelacionamentos.getId(), 
                            status,
                            contaComRelacionamentos.getCarteiraBoleto() != null ? "carregada" : "null",
                            contaComRelacionamentos.getEmpresa() != null ? "carregada" : "null");
                }
            } else {
                log.warn("Conta ID {} não foi encontrada no fetch com relacionamentos", contaOriginal.getId());
            }
        }
    }
}
