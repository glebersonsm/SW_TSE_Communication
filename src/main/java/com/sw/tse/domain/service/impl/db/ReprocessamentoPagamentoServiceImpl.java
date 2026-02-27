package com.sw.tse.domain.service.impl.db;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sw.tse.api.dto.ProcessamentoPagamentoResponseDto;
import com.sw.tse.api.dto.ProcessarPagamentoAprovadoTseDto;
import com.sw.tse.core.util.DiasAtrasoHelper;
import com.sw.tse.domain.expection.PagamentoTseBusinessException;
import com.sw.tse.domain.model.db.ContaFinanceira;
import com.sw.tse.domain.model.db.ContaMovimentacaoBancaria;
import com.sw.tse.domain.model.db.MovimentacaoBancaria;
import com.sw.tse.domain.model.db.MovimentacaoBancariaContaFinanceira;
import com.sw.tse.domain.model.db.Negociacao;
import com.sw.tse.domain.model.db.NegociacaoContaFinanceira;
import com.sw.tse.domain.model.db.OperadorSistema;
import com.sw.tse.domain.model.db.TransacaoDebitoCredito;
import com.sw.tse.domain.repository.ContaFinanceiraRepository;
import com.sw.tse.domain.repository.ContaMovimentacaoBancariaRepository;
import com.sw.tse.domain.repository.MovimentacaoBancariaContaFinanceiraRepository;
import com.sw.tse.domain.repository.MovimentacaoBancariaRepository;
import com.sw.tse.domain.repository.NegociacaoContaFinanceiraRepository;
import com.sw.tse.domain.repository.NegociacaoRepository;
import com.sw.tse.domain.repository.OperadorSistemaRepository;
import com.sw.tse.domain.repository.TransacaoDebitoCreditoRepository;
import com.sw.tse.domain.service.interfaces.OperadorSistemaService;
import com.sw.tse.domain.service.interfaces.ReprocessamentoPagamentoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.tse.api.dto.ContaFinanceiraParaPagamentoDto;
import com.sw.tse.domain.model.db.BandeiraCartao;
import com.sw.tse.domain.model.db.Empresa;
import com.sw.tse.domain.model.db.MeioPagamento;
import com.sw.tse.domain.model.db.TipoOrigemContaFinanceira;
import com.sw.tse.domain.repository.BandeiraCartaoRepository;
import com.sw.tse.domain.repository.MeioPagamentoRepository;
import java.util.HashMap;
import java.util.Map;
import com.sw.tse.security.JwtTokenUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(name = "database.enabled", havingValue = "true")
public class ReprocessamentoPagamentoServiceImpl implements ReprocessamentoPagamentoService {

    // ==================== CONSTANTES ====================
    /** Texto que identifica cancelamentos realizados pelo nosso fluxo do Portal. */
    private static final String PREFIXO_CANCELAMENTO_PORTAL = "Paga via Portal";

    @Value("${sw.tse.portal.pagamento-pix.id-meio-pagamento}")
    private Long idMeioPagamentoPortalPix;

    @Value("${sw.tse.portal.pagamento-cartao.id-meio-pagamento}")
    private Long idMeioPagamentoPortalCartao;

    // ==================== REPOSITORIES ====================
    @Autowired
    private ContaFinanceiraRepository contaFinanceiraRepository;
    @Autowired
    private NegociacaoRepository negociacaoRepository;
    @Autowired
    private NegociacaoContaFinanceiraRepository negociacaoContaFinanceiraRepository;
    @Autowired
    private MovimentacaoBancariaRepository movimentacaoBancariaRepository;
    @Autowired
    private MovimentacaoBancariaContaFinanceiraRepository movimentacaoVinculoRepository;
    @Autowired
    private ContaMovimentacaoBancariaRepository contaMovimentacaoBancariaRepository;
    @Autowired
    private TransacaoDebitoCreditoRepository transacaoDebitoCreditoRepository;
    @Autowired
    private OperadorSistemaRepository operadorSistemaRepository;
    @Autowired
    private OperadorSistemaService operadorSistemaService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MeioPagamentoRepository meioPagamentoRepository;
    @Autowired
    private BandeiraCartaoRepository bandeiraCartaoRepository;

    // ==================== ENUM DE ESTADO ====================
    private enum EstadoSincronizacao {
        BLOQUEADA_EXTERNO,
        INCONSISTENTE_SEM_NEGOCIACAO,
        INCONSISTENTE_SEM_MOVIMENTACAO,
        COMPLETA,
        PENDENTE
    }

    // ==================== MÉTODO PRINCIPAL ====================

    /**
     * Ponto de entrada do reprocessamento idempotente.
     * 
     * @Transactional garante atomicidade. O PESSIMISTIC_WRITE está no repository.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ProcessamentoPagamentoResponseDto verificarESincronizar(ProcessarPagamentoAprovadoTseDto dto) {

        boolean isPix = "PIX".equalsIgnoreCase(dto.getMeioPagamento());

        List<Long> idsContas = dto.getContasFinanceiras().stream()
                .map(cf -> cf.getIdContaFinanceiraTse())
                .collect(Collectors.toList());

        // ===== FASE 1: LOCK PRIMEIRO — DECISÃO DEPOIS =====
        // Lock pessimista adquirido na PRIMEIRA e ÚNICA leitura das contas.
        // Isso elimina a janela de corrida entre requests concorrentes.
        List<ContaFinanceira> contas = contaFinanceiraRepository.findByIdsComLock(idsContas);

        if (contas.isEmpty()) {
            throw new PagamentoTseBusinessException("Nenhuma conta financeira encontrada para os IDs: " + idsContas);
        }

        // Resolver operador responsável
        OperadorSistema responsavel = resolverResponsavel(dto);

        // Data de referência — sempre usa a data original da transação (nunca now())
        LocalDateTime dataReferencia = dto.getDataPagamento() != null
                ? dto.getDataPagamento().truncatedTo(ChronoUnit.SECONDS)
                : LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        log.info("[Reprocessamento] isPix={}, qtdContas={}, dataReferencia={}, idTransacao={}",
                isPix, contas.size(), dataReferencia, dto.getIdTransacao());

        if (isPix) {
            return processarPix(contas, dto, responsavel, dataReferencia);
        } else {
            return processarCartao(contas, dto, responsavel, dataReferencia);
        }
    }

    // ==================== PIX ====================

    private ProcessamentoPagamentoResponseDto processarPix(
            List<ContaFinanceira> contas,
            ProcessarPagamentoAprovadoTseDto dto,
            OperadorSistema responsavel,
            LocalDateTime dataReferencia) {

        if (contas.size() == 1) {
            return processarPixContaUnica(contas.get(0), dto, responsavel, dataReferencia);
        } else {
            return processarPixMultiplasContas(contas, dto, responsavel, dataReferencia);
        }
    }

    /**
     * Cenário 1: PIX, 1 conta.
     * Valida baixa pelo nosso fluxo e garante Negociação + MovimentacaoBancaria.
     */
    private ProcessamentoPagamentoResponseDto processarPixContaUnica(
            ContaFinanceira conta,
            ProcessarPagamentoAprovadoTseDto dto,
            OperadorSistema responsavel,
            LocalDateTime dataReferencia) {

        // Validar que a conta foi baixada (pelo nosso fluxo ou está paga)
        if (conta.getDataCancelamento() != null) {
            validarCancelamentoNossoFluxo(conta, isPix(dto));
            log.info("[PIX 1] Conta {} cancelada pelo nosso fluxo — validando registros posteriores.", conta.getId());
        } else if (!Boolean.TRUE.equals(conta.getPago())) {
            throw new PagamentoTseBusinessException(
                    String.format(
                            "ID: %d — Conta não está baixada. Não é possível sincronizar sem que o pagamento tenha sido confirmado.",
                            conta.getId()));
        }

        EstadoSincronizacao estado = classificarEstadoConta(conta);

        switch (estado) {
            case COMPLETA -> {
                log.info("[PIX 1] Conta {} já está COMPLETA. Marcando SINCRONIZADA.", conta.getId());
                return sucesso("Transação já estava sincronizada", null);
            }
            case INCONSISTENTE_SEM_NEGOCIACAO -> {
                log.info("[PIX 1] Conta {} INCONSISTENTE_SEM_NEGOCIACAO — criando Negociação + Movimentação.",
                        conta.getId());
                Negociacao negociacao = garantirNegociacao(conta, responsavel, dataReferencia, 3); // tipo 3 = Alterada
                garantirMovimentacaoBancaria(conta, dto, responsavel, dataReferencia);
                return sucesso("Negociação e movimentação criadas no reprocessamento", negociacao.getId());
            }
            case INCONSISTENTE_SEM_MOVIMENTACAO -> {
                log.info("[PIX 1] Conta {} INCONSISTENTE_SEM_MOVIMENTACAO — criando somente Movimentação.",
                        conta.getId());
                garantirMovimentacaoBancaria(conta, dto, responsavel, dataReferencia);
                return sucesso("Movimentação bancária criada no reprocessamento", null);
            }
            default -> throw new PagamentoTseBusinessException(
                    "Estado inesperado no reprocessamento da conta " + conta.getId() + ": " + estado);
        }
    }

    /**
     * Cenário 2: PIX, N contas.
     * Verifica a existência de conta consolidada (via txId) e decide o sub-fluxo.
     */
    private ProcessamentoPagamentoResponseDto processarPixMultiplasContas(
            List<ContaFinanceira> contas,
            ProcessarPagamentoAprovadoTseDto dto,
            OperadorSistema responsavel,
            LocalDateTime dataReferencia) {

        List<Long> idsContas = contas.stream().map(ContaFinanceira::getId).collect(Collectors.toList());

        // Buscar conta consolidada pelo txId dentro das contas vinculadas
        Optional<ContaFinanceira> contaConsolidadaOpt = (dto.getIdTransacao() != null)
                ? contaFinanceiraRepository.findByTxIdAndIdsIn(dto.getIdTransacao(), idsContas)
                : Optional.empty();

        if (contaConsolidadaOpt.isPresent()) {
            // Cenário 2.2: já existe conta baixada com nosso txId
            return processarPixCenario22(contaConsolidadaOpt.get(), contas, dto, responsavel, dataReferencia);
        } else {
            // Cenário 2.1: nenhuma conta baixada — executa fluxo completo
            return processarPixCenario21(contas, dto, responsavel, dataReferencia);
        }
    }

    /**
     * Cenário 2.1: PIX N contas — SEM conta consolidada baixada.
     * Executa fluxo completo: cancela originais, cria consolidada, Negociação e
     * Movimentação.
     */
    private ProcessamentoPagamentoResponseDto processarPixCenario21(
            List<ContaFinanceira> contas,
            ProcessarPagamentoAprovadoTseDto dto,
            OperadorSistema responsavel,
            LocalDateTime dataReferencia) {

        log.info("[PIX 2.1] Executando consolidação automática para {} contas.", contas.size());

        // 1. Mapear DTOs para acesso aos juros/multas enviados
        Map<Long, ContaFinanceiraParaPagamentoDto> mapaDtos = dto.getContasFinanceiras().stream()
                .collect(Collectors.toMap(ContaFinanceiraParaPagamentoDto::getIdContaFinanceiraTse, c -> c));

        // 2. Determinar conta consolidada
        ContaFinanceira contaNova = criarContaConsolidada(contas, mapaDtos, null, dto, responsavel, null, true);
        contaFinanceiraRepository.save(contaNova);
        log.info("[PIX 2.1] Conta consolidada criada - ID: {}, Valor: {}", contaNova.getId(),
                contaNova.getValorReceber());

        // 3. Negociação e registros financeiros
        Negociacao negociacao = criarNegociacao(contas.get(0).getEmpresa(), responsavel, dataReferencia);
        negociacaoRepository.save(negociacao);

        // Vincular a nova (tipo 1 = Nova)
        vincularNegociacao(negociacao, contaNova, 1, responsavel, dataReferencia, null);

        // 4. Cancelar originais e vincular (tipo 3 = Alterada/Cancelada)
        for (ContaFinanceira original : contas) {
            String jsonOriginal = serializarContaOriginal(original);

            if (original.getDataCancelamento() == null) {
                original.cancelar(responsavel, "Cancelada - Paga via Portal com PIX");
                contaFinanceiraRepository.save(original);
            }

            vincularNegociacao(negociacao, original, 3, responsavel, dataReferencia, jsonOriginal);
            log.info("[PIX 2.1] Conta original {} cancelada e vinculada à negociação.", original.getId());
        }

        // 5. Garantir Movimentação Bancária (PIX já é baixado na criação via factory)
        garantirMovimentacaoBancaria(contaNova, dto, responsavel, dataReferencia);

        return sucesso("Consolidação PIX reprocessada com sucesso", negociacao.getId());
    }

    /**
     * Cenário 2.2: PIX N contas — COM conta consolidada já baixada.
     * Valida/cria Negociação e MovimentacaoBancaria para a conta consolidada.
     * Cancela as contas originais que ainda estejam ativas.
     */
    private ProcessamentoPagamentoResponseDto processarPixCenario22(
            ContaFinanceira contaConsolidada,
            List<ContaFinanceira> todasContas,
            ProcessarPagamentoAprovadoTseDto dto,
            OperadorSistema responsavel,
            LocalDateTime dataReferencia) {

        log.info("[PIX 2.2] Conta consolidada encontrada: ID={}. Verificando consistência.", contaConsolidada.getId());

        EstadoSincronizacao estado = classificarEstadoConta(contaConsolidada);

        Negociacao negociacao;
        switch (estado) {
            case COMPLETA -> {
                log.info("[PIX 2.2] Conta consolidada já COMPLETA.");
                negociacao = null;
            }
            case INCONSISTENTE_SEM_NEGOCIACAO -> {
                negociacao = garantirNegociacao(contaConsolidada, responsavel, dataReferencia, 1); // tipo 1 = Nova
                garantirMovimentacaoBancaria(contaConsolidada, dto, responsavel, dataReferencia);
            }
            case INCONSISTENTE_SEM_MOVIMENTACAO -> {
                negociacao = null;
                garantirMovimentacaoBancaria(contaConsolidada, dto, responsavel, dataReferencia);
            }
            default -> throw new PagamentoTseBusinessException(
                    "Estado inesperado para conta consolidada " + contaConsolidada.getId() + ": " + estado);
        }

        // Cancelar contas originais ainda ativas (idempotente)
        for (ContaFinanceira original : todasContas) {
            if (original.getId().equals(contaConsolidada.getId()))
                continue;

            if (original.getDataCancelamento() != null) {
                // Já cancelada — verificar se foi por nós
                validarCancelamentoNossoFluxo(original, true);
                log.info("[PIX 2.2] Conta original {} já cancelada pelo nosso fluxo — pulando.", original.getId());
            } else if (!Boolean.TRUE.equals(original.getPago())) {
                original.cancelar(responsavel, "Cancelada - Paga via Portal com PIX");
                contaFinanceiraRepository.save(original);
                log.info("[PIX 2.2] Conta original {} cancelada.", original.getId());
            }
        }

        Long idNegociacao = negociacao != null ? negociacao.getId() : null;
        return sucesso("Reprocessamento PIX N contas (Cenário 2.2) concluído", idNegociacao);
    }

    // ==================== CARTÃO ====================

    /**
     * Cenário 3: Cartão.
     * Valida TransacaoDebitoCredito + garante Negociação.
     */
    private ProcessamentoPagamentoResponseDto processarCartao(
            List<ContaFinanceira> contas,
            ProcessarPagamentoAprovadoTseDto dto,
            OperadorSistema responsavel,
            LocalDateTime dataReferencia) {

        log.info("[CARTÃO] Iniciando processamento para {} contas.", contas.size());

        // 1. Localizar TransacaoDebitoCredito (obrigatória para todos os sub-cenários
        // de cartão no reprocessamento)
        // Se já existe uma conta vinculada ao idTransacaoCartaoCreditoDebito, usamos
        // ela como base.
        TransacaoDebitoCredito transacao = localizarTransacao(contas, dto);

        // 2. Classificar Cenário: Conta Única (Alteração) vs Múltiplas Contas
        // (Consolidação)
        // No cartão, mesmo que venha 1 conta no DTO, o sistema pode ter consolidado no
        // processamento original.
        // Verificamos se há uma conta baixada vinculada à transação.
        Optional<ContaFinanceira> contaBaixadaOpt = contaFinanceiraRepository
                .findByIdTransacaoCartaoCreditoDebito(transacao.getId());

        if (contaBaixadaOpt.isPresent()) {
            ContaFinanceira contaBaixada = contaBaixadaOpt.get();
            log.info("[CARTÃO] Conta baixada encontrada para a transação: ID={}", contaBaixada.getId());
            return sincronizarContaCartaoExistente(contaBaixada, contas, dto, responsavel, dataReferencia, transacao);
        } else {
            // Se não há conta baixada, mas temos múltiplas contas originais ou 1 conta
            // ativa
            log.info("[CARTÃO] Nenhuma conta baixada encontrada. Executando consolidação automática.");
            return consolidarCartaoAutomatico(contas, dto, responsavel, dataReferencia, transacao);
        }
    }

    private TransacaoDebitoCredito localizarTransacao(List<ContaFinanceira> contas,
            ProcessarPagamentoAprovadoTseDto dto) {
        // Tenta achar pelo ID no DTO primeiro
        if (dto.getPaymentId() != null || dto.getIdTransacao() != null) {
            // O DTO idTransacao no cartão costuma ser o MerchantOrderId
            Optional<TransacaoDebitoCredito> t = transacaoDebitoCreditoRepository
                    .findFirstByMerchantOrderIdOrderByDataCadastroDesc(dto.getIdTransacao());
            if (t.isPresent())
                return t.get();
        }

        // Tenta achar pelas contas
        for (ContaFinanceira c : contas) {
            if (c.getIdTransacaoCartaoCreditoDebito() != null) {
                return transacaoDebitoCreditoRepository.findById(c.getIdTransacaoCartaoCreditoDebito())
                        .orElseThrow(() -> new PagamentoTseBusinessException(
                                "Transação vinculada à conta não encontrada no TSE"));
            }
        }

        throw new PagamentoTseBusinessException(
                "Transação de débito/crédito não encontrada para reprocessamento de cartão.");
    }

    private ProcessamentoPagamentoResponseDto sincronizarContaCartaoExistente(
            ContaFinanceira contaBaixada,
            List<ContaFinanceira> todasContas,
            ProcessarPagamentoAprovadoTseDto dto,
            OperadorSistema responsavel,
            LocalDateTime dataReferencia,
            TransacaoDebitoCredito transacao) {

        EstadoSincronizacao estado = classificarEstadoConta(contaBaixada);
        Negociacao negociacao = null;

        switch (estado) {
            case COMPLETA -> log.info("[CARTÃO] Conta {} já está COMPLETA.", contaBaixada.getId());
            case INCONSISTENTE_SEM_NEGOCIACAO -> {
                int tipoNegoc = (todasContas.size() == 1 && todasContas.get(0).getId().equals(contaBaixada.getId())) ? 3
                        : 1;
                negociacao = garantirNegociacao(contaBaixada, responsavel, dataReferencia, tipoNegoc);
            }
            case INCONSISTENTE_SEM_MOVIMENTACAO -> {
                // Para cartão, Movimentação Bancária costuma vir de integração
                // externa/conciliação.
                // Não criamos automaticamente no reprocessamento para evitar erros de
                // conciliação futura.
                log.warn("[CARTÃO] Conta {} sem Movimentação Bancária — requer verificação manual de extrato.",
                        contaBaixada.getId());
            }
            default -> throw new PagamentoTseBusinessException("Estado inesperado: " + estado);
        }

        // Garantir que as originais (se houver) estejam canceladas
        for (ContaFinanceira original : todasContas) {
            if (original.getId().equals(contaBaixada.getId()))
                continue;
            if (original.getDataCancelamento() == null) {
                original.cancelar(responsavel, "Cancelada - Paga via Portal com Cartão");
                contaFinanceiraRepository.save(original);
            }
        }

        return sucesso("Sincronização de cartão concluída", negociacao != null ? negociacao.getId() : null);
    }

    private ProcessamentoPagamentoResponseDto consolidarCartaoAutomatico(
            List<ContaFinanceira> contas,
            ProcessarPagamentoAprovadoTseDto dto,
            OperadorSistema responsavel,
            LocalDateTime dataReferencia,
            TransacaoDebitoCredito transacao) {

        log.info("[CARTÃO] Consolidando {} contas.", contas.size());

        Map<Long, ContaFinanceiraParaPagamentoDto> mapaDtos = dto.getContasFinanceiras().stream()
                .collect(Collectors.toMap(ContaFinanceiraParaPagamentoDto::getIdContaFinanceiraTse, c -> c));

        // 1. Criar conta consolidada
        // Para cartão, precisamos da BandeiraCartao (buscamos da transação se possível)
        BandeiraCartao bandeira = null;
        if (transacao.getIdBandeiraCartao() != null) {
            bandeira = bandeiraCartaoRepository.findById(transacao.getIdBandeiraCartao().intValue()).orElse(null);
        }

        @SuppressWarnings("null")
        ContaFinanceira contaNova = criarContaConsolidada(contas, mapaDtos, transacao, dto, responsavel, bandeira,
                false);
        contaFinanceiraRepository.save(contaNova);

        // 2. Negociação
        Negociacao negociacao = criarNegociacao(contas.get(0).getEmpresa(), responsavel, dataReferencia);
        negociacaoRepository.save(negociacao);

        vincularNegociacao(negociacao, contaNova, 1, responsavel, dataReferencia, null);

        // 3. Cancelar e vincular originais
        for (ContaFinanceira original : contas) {
            String json = serializarContaOriginal(original);
            if (original.getDataCancelamento() == null) {
                original.cancelar(responsavel, "Cancelada - Paga via Portal com Cartão");
                contaFinanceiraRepository.save(original);
            }
            vincularNegociacao(negociacao, original, 3, responsavel, dataReferencia, json);
        }

        return sucesso("Consolidação de cartão realizada com sucesso no reprocessamento", negociacao.getId());
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Classifica o estado de uma conta individual.
     * Baseado na existência de NegociacaoContaFinanceira e
     * MovimentacaoBancariaContaFinanceira.
     */
    private EstadoSincronizacao classificarEstadoConta(ContaFinanceira conta) {
        List<NegociacaoContaFinanceira> ncfs = negociacaoContaFinanceiraRepository
                .findByContaFinanceiraId(conta.getId());
        List<MovimentacaoBancariaContaFinanceira> vinculosMovim = movimentacaoVinculoRepository
                .findByContaFinanceiraId(conta.getId());

        boolean temNegociacao = !ncfs.isEmpty();
        boolean temMovimentacao = !vinculosMovim.isEmpty();

        if (temNegociacao && temMovimentacao)
            return EstadoSincronizacao.COMPLETA;
        if (!temNegociacao)
            return EstadoSincronizacao.INCONSISTENTE_SEM_NEGOCIACAO;
        return EstadoSincronizacao.INCONSISTENTE_SEM_MOVIMENTACAO;
    }

    /**
     * Verifica se uma conta cancelada foi cancelada pelo nosso fluxo.
     * Se não foi, lança BLOQUEADA_EXTERNO.
     */
    private void validarCancelamentoNossoFluxo(ContaFinanceira conta, boolean isPix) {
        String historico = conta.getHistoricoCancelamento();
        boolean nossoFluxo = historico != null && historico.contains(PREFIXO_CANCELAMENTO_PORTAL);
        if (!nossoFluxo) {
            throw new PagamentoTseBusinessException(
                    String.format("ID: %d — Conta cancelada por fluxo externo (histórico: '%s'). " +
                            "É necessária atuação manual de um operador.", conta.getId(), historico));
        }
    }

    /**
     * Garante que existe uma NegociacaoContaFinanceira para esta conta.
     * Idempotente: não cria se já existir.
     */
    private Negociacao garantirNegociacao(ContaFinanceira conta, OperadorSistema responsavel,
            LocalDateTime dataReferencia, int tipoNegociacao) {

        // Guard: já existe?
        List<NegociacaoContaFinanceira> existentes = negociacaoContaFinanceiraRepository
                .findByContaFinanceiraId(conta.getId());
        if (!existentes.isEmpty()) {
            log.info("[Negociação] Conta {} já possui {} NCF(s) — não criando.", conta.getId(), existentes.size());
            return existentes.get(0).getNegociacao();
        }

        Negociacao negociacao = new Negociacao();
        negociacao.setEmpresa(conta.getEmpresa());
        negociacao.setResponsavelCadastro(responsavel);
        negociacao.setDataCadastro(dataReferencia);
        negociacao.setStatusNegociacao(1);
        negociacaoRepository.save(negociacao);

        NegociacaoContaFinanceira ncf = new NegociacaoContaFinanceira();
        ncf.setNegociacao(negociacao);
        ncf.setContaFinanceira(conta);
        ncf.setTipoNegociacao(tipoNegociacao);
        ncf.setEmpresa(conta.getEmpresa());
        ncf.setResponsavelCadastro(responsavel);
        ncf.setDataCadastro(dataReferencia);
        negociacaoContaFinanceiraRepository.save(ncf);

        log.info("[Negociação] Criada NegociacaoID={} para ContaID={}, tipo={}", negociacao.getId(), conta.getId(),
                tipoNegociacao);
        return negociacao;
    }

    /**
     * Garante que existe uma MovimentacaoBancaria para esta conta (PIX).
     * Idempotente: não cria se já existir.
     * Datas retroativas: usa dataReferencia (data original da transação), não
     * now().
     * Regra de feriados: aplica DiasAtrasoHelper.obterProximoDiaUtil sobre a data
     * original.
     */
    private void garantirMovimentacaoBancaria(ContaFinanceira conta,
            ProcessarPagamentoAprovadoTseDto dto,
            OperadorSistema responsavel,
            LocalDateTime dataReferencia) {

        // Guard: já existe?
        List<MovimentacaoBancariaContaFinanceira> existentes = movimentacaoVinculoRepository
                .findByContaFinanceiraId(conta.getId());
        if (!existentes.isEmpty()) {
            log.info("[MovimBancaria] Conta {} já possui vínculo de movimentação — não criando.", conta.getId());
            return;
        }

        // Conta de movimentação bancária
        ContaMovimentacaoBancaria contaMovim = conta.getContaMovimentacaoBancaria();
        if (contaMovim == null && dto.getIdContaMovimentacaoBancaria() != null) {
            contaMovim = contaMovimentacaoBancariaRepository
                    .findById(dto.getIdContaMovimentacaoBancaria()).orElse(null);
        }

        if (contaMovim == null) {
            log.warn("[MovimBancaria] Sem conta de movimentação para conta {}. Movimentação não criada.",
                    conta.getId());
            return;
        }

        // Data da movimentação: próximo dia útil a partir da data ORIGINAL da transação
        // Feriados são respeitados via FeriadosContext (populado pelo C# no request)
        LocalDate dataMovim = DiasAtrasoHelper.obterProximoDiaUtil(dataReferencia.toLocalDate());

        OperadorSistema operadorPadrao = operadorSistemaService.operadorSistemaPadraoCadastro();

        MovimentacaoBancaria movimentacao = new MovimentacaoBancaria();
        movimentacao.setEmpresa(conta.getEmpresa());
        movimentacao.setContaMovimentacaoBancaria(contaMovim);
        movimentacao.setData(dataMovim.atStartOfDay());
        movimentacao.setHistorico("Recebimento PIX (Reprocessamento) - Conta " + conta.getId()
                + " - NSU: " + dto.getNsu());
        movimentacao.setValor(dto.getValorTotal());
        movimentacao.setDebitoCreditoMovimentacaoBancaria(0); // 0 = Crédito
        movimentacao.setResponsavelCadastro(operadorPadrao);
        movimentacaoBancariaRepository.save(movimentacao);

        MovimentacaoBancariaContaFinanceira vinculo = new MovimentacaoBancariaContaFinanceira();
        vinculo.setMovimentacaoBancaria(movimentacao);
        vinculo.setContaFinanceira(conta);
        vinculo.setValor(dto.getValorTotal());
        vinculo.setResponsavelCadastro(operadorPadrao);
        vinculo.setEmpresa(conta.getEmpresa());
        movimentacaoVinculoRepository.save(vinculo);

        log.info("[MovimBancaria] Criada ID={}, Data={}, Conta={}", movimentacao.getId(), dataMovim, conta.getId());
    }

    /**
     * Resolve o OperadorSistema responsável pelo reprocessamento.
     */
    private OperadorSistema resolverResponsavel(ProcessarPagamentoAprovadoTseDto dto) {
        Long idUsuario = JwtTokenUtil.getIdUsuarioCliente();
        if (idUsuario == null || idUsuario == 0)
            idUsuario = dto.getIdUsuarioLogado();

        if (idUsuario != null && idUsuario > 0) {
            return operadorSistemaRepository.findById(idUsuario)
                    .orElseGet(() -> operadorSistemaService.operadorSistemaPadraoCadastro());
        }
        return operadorSistemaService.operadorSistemaPadraoCadastro();
    }

    private boolean isPix(ProcessarPagamentoAprovadoTseDto dto) {
        return "PIX".equalsIgnoreCase(dto.getMeioPagamento());
    }

    // ==================== MÉTODOS DE CONSOLIDAÇÃO ====================

    private Negociacao criarNegociacao(Empresa empresa, OperadorSistema responsavel, LocalDateTime data) {
        Negociacao negociacao = new Negociacao();
        negociacao.setEmpresa(empresa);
        negociacao.setResponsavelCadastro(responsavel);
        negociacao.setStatusNegociacao(1); // Ativo
        negociacao.setDataCadastro(data);
        return negociacao;
    }

    private void vincularNegociacao(Negociacao negociacao, ContaFinanceira conta, int tipo,
            OperadorSistema responsavel, LocalDateTime data, String json) {
        NegociacaoContaFinanceira ncf = new NegociacaoContaFinanceira();
        ncf.setNegociacao(negociacao);
        ncf.setContaFinanceira(conta);
        ncf.setTipoNegociacao(tipo);
        ncf.setEmpresa(negociacao.getEmpresa());
        ncf.setResponsavelCadastro(responsavel);
        ncf.setDataCadastro(data);
        ncf.setContaFinanceiraJson(json);
        negociacaoContaFinanceiraRepository.save(ncf);
    }

    private String serializarContaOriginal(ContaFinanceira conta) {
        try {
            Map<String, Object> dados = new HashMap<>();
            dados.put("IdContaFinanceira", conta.getId());
            dados.put("DataVencimento", conta.getDataVencimento());
            dados.put("ValorReceber", conta.getValorReceber());
            dados.put("ValorParcela", conta.getValorParcela());
            dados.put("ValorAcrescimo", conta.getValorAcrescimo());
            dados.put("ValorJuros", conta.getValorJuros());
            dados.put("ValorMulta", conta.getValorMulta());
            dados.put("ValorAcrescimoAcumuladoCorrecaoMonetaria", conta.getValorAcrescimoAcumuladoCorrecaoMonetaria());
            return objectMapper.writeValueAsString(dados);
        } catch (Exception e) {
            log.error("Erro ao serializar conta {}", conta.getId(), e);
            return "{}";
        }
    }

    private ContaFinanceira criarContaConsolidada(
            List<ContaFinanceira> contasOriginais,
            Map<Long, ContaFinanceiraParaPagamentoDto> mapaDtos,
            TransacaoDebitoCredito transacao,
            ProcessarPagamentoAprovadoTseDto dto,
            OperadorSistema responsavel,
            BandeiraCartao bandeiraCartao,
            boolean isPix) {

        ContaFinanceira base = contasOriginais.get(0);
        TipoOrigemContaFinanceira origem = determinarOrigemConta(contasOriginais, base);
        ValoresTotais valores = calcularValoresTotais(contasOriginais);

        // Somar juros/multa dos DTOs se existirem, senão usa o calculado pela entidade
        BigDecimal totalJuros = contasOriginais.stream()
                .map(co -> {
                    ContaFinanceiraParaPagamentoDto cd = mapaDtos.get(co.getId());
                    return (cd != null && cd.getValorJuros() != null) ? cd.getValorJuros() : co.calcularJuros();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalMulta = contasOriginais.stream()
                .map(co -> {
                    ContaFinanceiraParaPagamentoDto cd = mapaDtos.get(co.getId());
                    return (cd != null && cd.getValorMulta() != null) ? cd.getValorMulta() : co.calcularMulta();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        MeioPagamento meioPagamento = meioPagamentoRepository
                .findById(isPix ? idMeioPagamentoPortalPix : idMeioPagamentoPortalCartao)
                .orElseThrow(() -> new PagamentoTseBusinessException("Meio de pagamento não configurado"));

        ContaMovimentacaoBancaria contaMovim = determinarContaMovimentacao(dto, base, bandeiraCartao, isPix);
        OperadorSistema operadorPadrao = operadorSistemaService.operadorSistemaPadraoCadastro();

        return ContaFinanceira.criarContaConsolidadaPagamentoPortal(
                base,
                dto.getValorTotal(),
                valores.totalAcrescimo,
                totalJuros,
                totalMulta,
                valores.totalCorrecaoMonetaria,
                transacao != null ? transacao.getId() : null,
                dto.getCodigoAutorizacao(),
                dto.getAdquirente(),
                dto.getNsu(),
                dto.getIdTransacao(),
                meioPagamento,
                origem,
                responsavel,
                bandeiraCartao,
                dto.getDataPagamento() != null ? dto.getDataPagamento() : LocalDateTime.now(),
                contaMovim,
                isPix,
                dto.getPixCopiaECola(),
                dto.getDataGeracaoPix(),
                operadorPadrao);
    }

    private TipoOrigemContaFinanceira determinarOrigemConta(List<ContaFinanceira> contas, ContaFinanceira base) {
        return contas.stream()
                .filter(c -> c.getOrigemConta() != null)
                .findFirst()
                .map(ContaFinanceira::getOrigemConta)
                .orElse(base.getOrigemConta());
    }

    private ValoresTotais calcularValoresTotais(List<ContaFinanceira> contas) {
        BigDecimal totalAcrescimo = BigDecimal.ZERO;
        BigDecimal totalCorrecao = BigDecimal.ZERO;
        for (ContaFinanceira c : contas) {
            if (c.getValorAcrescimo() != null)
                totalAcrescimo = totalAcrescimo.add(c.getValorAcrescimo());
            if (c.getValorAcrescimoAcumuladoCorrecaoMonetaria() != null)
                totalCorrecao = totalCorrecao.add(c.getValorAcrescimoAcumuladoCorrecaoMonetaria());
        }
        return new ValoresTotais(totalAcrescimo, totalCorrecao);
    }

    private ContaMovimentacaoBancaria determinarContaMovimentacao(
            ProcessarPagamentoAprovadoTseDto dto,
            ContaFinanceira base,
            BandeiraCartao bandeira,
            boolean isPix) {

        if (isPix && dto.getIdContaMovimentacaoBancaria() != null) {
            return contaMovimentacaoBancariaRepository.findById(dto.getIdContaMovimentacaoBancaria())
                    .orElse(base.getContaMovimentacaoBancaria());
        }
        if (!isPix && bandeira != null && bandeira.getIdContaMovBancaria() != null) {
            return contaMovimentacaoBancariaRepository.findById(bandeira.getIdContaMovBancaria().longValue())
                    .orElse(base.getContaMovimentacaoBancaria());
        }
        return base.getContaMovimentacaoBancaria();
    }

    private static class ValoresTotais {
        final BigDecimal totalAcrescimo;
        final BigDecimal totalCorrecaoMonetaria;

        ValoresTotais(BigDecimal totalAcrescimo, BigDecimal totalCorrecaoMonetaria) {
            this.totalAcrescimo = totalAcrescimo;
            this.totalCorrecaoMonetaria = totalCorrecaoMonetaria;
        }
    }

    private ProcessamentoPagamentoResponseDto sucesso(String mensagem, Long idNegociacao) {
        return ProcessamentoPagamentoResponseDto.builder()
                .idNegociacao(idNegociacao)
                .mensagem(mensagem)
                .status("SUCESSO")
                .build();
    }
}
