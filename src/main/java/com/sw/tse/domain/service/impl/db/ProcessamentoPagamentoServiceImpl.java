package com.sw.tse.domain.service.impl.db;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.tse.api.dto.ContaFinanceiraParaPagamentoDto;
import com.sw.tse.api.dto.ProcessarPagamentoAprovadoTseDto;
import com.sw.tse.domain.expection.ContaFinanceiraNaoEncontradaException;
import com.sw.tse.domain.expection.OperadorSistemaNotFoundException;
import com.sw.tse.domain.expection.PagamentoCartaoException;
import com.sw.tse.domain.expection.TokenJwtInvalidoException;
import com.sw.tse.domain.model.db.BandeiraCartao;
import com.sw.tse.domain.model.db.ContaFinanceira;
import com.sw.tse.domain.model.db.MeioPagamento;
import com.sw.tse.domain.model.db.Negociacao;
import com.sw.tse.domain.model.db.NegociacaoContaFinanceira;
import com.sw.tse.domain.model.db.OperadorSistema;
import com.sw.tse.domain.model.db.TipoOrigemContaFinanceira;
import com.sw.tse.domain.model.db.TransacaoDebitoCredito;
import com.sw.tse.domain.repository.BandeiraCartaoRepository;
import com.sw.tse.domain.repository.ContaFinanceiraRepository;
import com.sw.tse.domain.repository.MeioPagamentoRepository;
import com.sw.tse.domain.repository.NegociacaoContaFinanceiraRepository;
import com.sw.tse.domain.repository.NegociacaoRepository;
import com.sw.tse.domain.repository.OperadorSistemaRepository;
import com.sw.tse.domain.repository.TipoOrigemContaFinanceiraRepository;
import com.sw.tse.domain.repository.TransacaoDebitoCreditoRepository;
import com.sw.tse.domain.service.interfaces.PagamentoCartaoService;
import com.sw.tse.security.JwtTokenUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnProperty(name = "database.enabled", havingValue = "true")
@Slf4j
public class ProcessamentoPagamentoServiceImpl implements PagamentoCartaoService {
    
    @Value("${sw.tse.portal.pagamento-cartao.id-meio-pagamento}")
    private Long idMeioPagamentoPortalCartao;
    
    @Value("${sw.tse.portal.pagamento-pix.id-meio-pagamento}")
    private Long idMeioPagamentoPortalPix;
    
    @Autowired
    private ContaFinanceiraRepository contaFinanceiraRepository;
    
    @Autowired
    private TransacaoDebitoCreditoRepository transacaoRepository;
    
    @Autowired
    private NegociacaoRepository negociacaoRepository;
    
    @Autowired
    private NegociacaoContaFinanceiraRepository negociacaoContaFinanceiraRepository;
    
    @Autowired
    private OperadorSistemaRepository operadorSistemaRepository;
    
    @Autowired
    private MeioPagamentoRepository meioPagamentoRepository;
    
    @Autowired
    private TipoOrigemContaFinanceiraRepository tipoOrigemRepository;
    
    @Autowired
    private BandeiraCartaoRepository bandeiraCartaoRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    @Transactional
    public void processarPagamentoAprovado(ProcessarPagamentoAprovadoTseDto dto) {
        log.info("Processando pagamento aprovado para empresa {} com {} contas financeiras",
                dto.getIdEmpresaTse(), dto.getContasFinanceiras().size());
        
        try {
            // 1. Buscar usuário responsável do JWT (igual aos outros endpoints)
            Long idUsuarioCliente = JwtTokenUtil.getIdUsuarioCliente();
            
            if (idUsuarioCliente == null || idUsuarioCliente == 0) {
                log.warn("ID do usuário não encontrado no JWT, usando ID enviado no DTO: {}", dto.getIdUsuarioLogado());
                idUsuarioCliente = dto.getIdUsuarioLogado();
            }
            
            if (idUsuarioCliente == null || idUsuarioCliente == 0) {
                throw new TokenJwtInvalidoException("Não foi possível obter ID do usuário logado do token JWT");
            }
            
            log.info("Usuário responsável: ID {}", idUsuarioCliente);
            
            OperadorSistema responsavel = operadorSistemaRepository.findById(idUsuarioCliente)
                    .orElseThrow(() -> new OperadorSistemaNotFoundException("Operador sistema não encontrado para ID: " + JwtTokenUtil.getIdUsuarioCliente()));
            
            // 2. Buscar contas financeiras originais
            List<Long> idsContas = dto.getContasFinanceiras().stream()
                    .map(ContaFinanceiraParaPagamentoDto::getIdContaFinanceiraTse)
                    .collect(Collectors.toList());
            
            List<ContaFinanceira> contasOriginais = contaFinanceiraRepository.findAllById(idsContas);
            
            if (contasOriginais.isEmpty()) {
                throw new ContaFinanceiraNaoEncontradaException(idsContas);
            }
            
            log.info("Encontradas {} contas originais para processar", contasOriginais.size());
            log.info("IdBandeira recebido no DTO: {}", dto.getIdBandeira());
            log.info("Meio de pagamento recebido: {}", dto.getMeioPagamento());
            
            // Verificar se é pagamento PIX (não precisa de bandeira nem transação de cartão)
            boolean isPix = "PIX".equalsIgnoreCase(dto.getMeioPagamento());
            
            BandeiraCartao bandeiraCartao = null;
            TransacaoDebitoCredito transacao = null;
            
            if (!isPix) {
                // 3. Buscar BandeiraCartao (para taxa e configurações) - OBRIGATÓRIO para CARTAO
                bandeiraCartao = buscarBandeiraCartao(
                        contasOriginais.get(0).getEmpresa().getId(),
                        dto.getIdBandeira(),
                        dto.getAdquirente());
                
                if (bandeiraCartao != null) {
                    log.info("BandeiraCartao encontrada: ID {}, Taxa: {}", 
                            bandeiraCartao.getIdBandeiraCartao(), bandeiraCartao.getTaxaOperacao());
                } else {
                    throw new PagamentoCartaoException(
                            String.format("Configuração de bandeira não encontrada. Tenant: %d, IdBandeira: %d, Gateway: %s. " +
                                    "Cadastre uma configuração ativa em 'bandeiracartao' com operacao='CREDAV'",
                                    contasOriginais.get(0).getEmpresa().getId(), dto.getIdBandeira(), dto.getAdquirente()));
                }
                
                // 4. Criar TransacaoDebitoCredito (apenas para CARTAO)
                transacao = criarTransacaoDebitoCredito(
                        dto, contasOriginais.get(0), responsavel, bandeiraCartao);
                transacaoRepository.save(transacao);
                log.info("TransacaoDebitoCredito criada com ID: {}, Status: InProgress", transacao.getId());
                
                // 4.1. Atualizar transação com resultado do pagamento aprovado
                atualizarTransacaoComResultado(transacao, dto);
                transacaoRepository.save(transacao);
                log.info("TransacaoDebitoCredito atualizada - Status: {}, NSU: {}", transacao.getStatus(), transacao.getNsu());
            } else {
                log.info("Pagamento PIX - Não será criada TransacaoDebitoCredito");
            }
            
            // 5. Criar conta consolidada (passa null para transacao se for PIX)
            ContaFinanceira contaNova = criarContaConsolidada(contasOriginais, transacao, dto, responsavel, bandeiraCartao, isPix);
            contaFinanceiraRepository.save(contaNova);
            log.info("Conta consolidada criada com ID: {}, Valor: {}", contaNova.getId(), contaNova.getValorReceber());
            
            // 6. Criar data de cadastro sem nanosegundos (regra TSE)
            // Mesma data para Negociacao e todas as NegociacaoContaFinanceira
            LocalDateTime dataCadastroNegociacao = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            
            // 7. Criar Negociação
            Negociacao negociacao = criarNegociacao(contasOriginais.get(0).getEmpresa(), responsavel, dataCadastroNegociacao);
            negociacaoRepository.save(negociacao);
            log.info("Negociação criada com ID: {}, DataCadastro: {}", negociacao.getId(), negociacao.getDataCadastro());
            
            // 8. Vincular conta NOVA na negociação (tipo 1 = Nova Conta)
            NegociacaoContaFinanceira ncfNova = new NegociacaoContaFinanceira();
            ncfNova.setNegociacao(negociacao);
            ncfNova.setContaFinanceira(contaNova);
            ncfNova.setTipoNegociacao(1);
            ncfNova.setEmpresa(negociacao.getEmpresa());
            ncfNova.setResponsavelCadastro(responsavel);
            ncfNova.setContaFinanceiraJson(null); // Nova conta não tem JSON
            ncfNova.setDataCadastro(dataCadastroNegociacao); // Mesma data da negociação
            negociacaoContaFinanceiraRepository.save(ncfNova);
            
            // 9. Vincular contas ORIGINAIS na negociação (tipo 2 = Cancelada) e CANCELAR
            for (ContaFinanceira contaOriginal : contasOriginais) {
                // Salvar JSON da conta ANTES de cancelar
                String jsonContaOriginal = serializarContaOriginal(contaOriginal);
                
                // Vincular na negociação
                NegociacaoContaFinanceira ncfOriginal = new NegociacaoContaFinanceira();
                ncfOriginal.setNegociacao(negociacao);
                ncfOriginal.setContaFinanceira(contaOriginal);
                ncfOriginal.setTipoNegociacao(2); // Cancelada
                ncfOriginal.setEmpresa(negociacao.getEmpresa());
                ncfOriginal.setResponsavelCadastro(responsavel);
                ncfOriginal.setContaFinanceiraJson(jsonContaOriginal);
                ncfOriginal.setDataCadastro(dataCadastroNegociacao); // Mesma data da negociação
                negociacaoContaFinanceiraRepository.save(ncfOriginal);
                
                // Cancelar conta usando método do Aggregate Root
                contaOriginal.cancelar(responsavel, "Cancelada - Paga via Portal com Cartão");
                contaFinanceiraRepository.save(contaOriginal);
                
                log.info("Conta {} vinculada à negociação e cancelada (JSON salvo)", contaOriginal.getId());
            }
            
            log.info("Pagamento processado com sucesso. TransacaoId: {}, NSU: {}, NegociacaoId: {}, Contas vinculadas: {}",
                    dto.getIdTransacao(), dto.getNsu(), negociacao.getId(), contasOriginais.size() + 1);
            
        } catch (TokenJwtInvalidoException | OperadorSistemaNotFoundException | ContaFinanceiraNaoEncontradaException e) {
            // Re-lançar exceptions específicas sem wrapper
            throw e;
        } catch (Exception e) {
            log.error("Erro ao processar pagamento aprovado", e);
            throw new PagamentoCartaoException("Erro ao processar pagamento aprovado: " + e.getMessage(), e);
        }
    }
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    private BandeiraCartao buscarBandeiraCartao(Long idTenant, Integer idBandeira, String gatewaySysId) {
        if (idBandeira == null || idBandeira == 0) {
            throw new PagamentoCartaoException(
                    "ID da bandeira do cartão é obrigatório para processar pagamento");
        }
        
        // Extrair apenas "REDE" ou "GETNET" do SysId (ex: "GATEWAY_PAGAMENTO_REDE" -> "REDE")
        String nomeGateway = extrairNomeGateway(gatewaySysId);
        
        log.info("Buscando BandeiraCartao. Tenant: {}, IdBandeira: {}, Gateway original: '{}', Nome extraído: '{}'",
                idTenant, idBandeira, gatewaySysId, nomeGateway);
        
        // Buscar todos os registros que atendem aos critérios (exceto nome)
        // O campo nomeEstabelecimento é criptografado, então precisamos comparar em memória
        java.util.List<BandeiraCartao> candidatos = bandeiraCartaoRepository.findBandeirasAtivasParaPagamento(
                idTenant, 
                idBandeira);
        
        log.info("Encontrados {} candidatos de BandeiraCartao para filtrar por nome do gateway", candidatos.size());
        
        // Filtrar em memória pelo nome do gateway (campo descriptografado)
        BandeiraCartao bandeira = candidatos.stream()
                .filter(bc -> {
                    String nomeEstab = bc.getNomeEstabelecimento();
                    if (nomeEstab == null) return false;
                    String nomeEstabUpper = nomeEstab.toUpperCase();
                    boolean match = nomeEstabUpper.contains(nomeGateway.toUpperCase());
                    log.debug("Comparando: '{}' contém '{}'? {}", nomeEstabUpper, nomeGateway, match);
                    return match;
                })
                .findFirst()
                .orElse(null);
        
        if (bandeira == null) {
            log.warn("BandeiraCartao não encontrada para tenant: {}, idBandeira: {}, nome gateway: '{}'. " +
                    "Verifique se existe registro ativo em bandeiracartao com operacao='CREDAV' e nomeestabelecimento contendo '{}'",
                    idTenant, idBandeira, nomeGateway, nomeGateway);
        } else {
            log.info("BandeiraCartao encontrada: ID {}, NomeEstabelecimento contém '{}', TaxaOperacao: {}",
                    bandeira.getIdBandeiraCartao(), nomeGateway, bandeira.getTaxaOperacao());
        }
        
        return bandeira;
    }
    
    /**
     * Extrai o nome do gateway do SysId
     * Ex: "GATEWAY_PAGAMENTO_REDE" -> "REDE"
     *     "GATEWAY_PAGAMENTO_GETNET" -> "GETNET"
     */
    private String extrairNomeGateway(String gatewaySysId) {
        if (gatewaySysId == null) return "";
        
        String upper = gatewaySysId.toUpperCase();
        if (upper.contains("GETNET")) return "GETNET";
        if (upper.contains("REDE")) return "REDE";
        
        return gatewaySysId; // Fallback: retorna o original
    }
    
    private TransacaoDebitoCredito criarTransacaoDebitoCredito(
            ProcessarPagamentoAprovadoTseDto dto,
            ContaFinanceira contaBase,
            OperadorSistema responsavel,
            BandeiraCartao bandeiraCartao) {
        
        TransacaoDebitoCredito transacao = new TransacaoDebitoCredito();
        
        // Dados básicos da transação
        transacao.setEmpresa(contaBase.getEmpresa());
        transacao.setResponsavelCadastro(responsavel);
        transacao.setContrato(contaBase.getContrato());
        
        // MerchantOrderID = IdPedido da transacaoPagamento
        transacao.setMerchantOrderId(dto.getIdTransacao());
        
        // PaymentID = SOMENTE para GetNet (ID retornado pela operadora)
        String gatewaySysId = dto.getAdquirente() != null ? dto.getAdquirente().toUpperCase() : "";
        if (gatewaySysId.contains("GETNET") && dto.getPaymentId() != null) {
            transacao.setPaymentId(dto.getPaymentId());
        }
        
        // Bandeira e bandeiras aceitas
        transacao.setIdBandeirasAceitas(bandeiraCartao.getIdBandeirasAceitas());
        transacao.setIdBandeiraCartao(bandeiraCartao.getIdBandeiraCartao());
        
        // Dados do cartão completos (serão criptografados pelo @Convert)
        // Vem do frontend (cartão digitado ou vinculado) → Backend Portal → TSE
        transacao.setNumeroCartao(dto.getNumeroCartao());
        transacao.setCodSegurancaCartao(dto.getCodigoSegurancaCartao());
        transacao.setMesValidadeCartao(dto.getMesValidadeCartao());
        transacao.setAnoValidadeCartao(dto.getAnoValidadeCartao());
        transacao.setNomeImpressoCartao(dto.getNomeImpressoCartao());
        
        // Número do cartão mascarado já vem formatado do backend
        transacao.setNumeroCartaoMascarado(dto.getNumeroCartaoMascarado());
        transacao.setNomePessoa(dto.getNomeImpressoCartao());
        
        // Valores e vencimento
        transacao.setDataVencimento(LocalDateTime.now().plusDays(30));
        transacao.setValorReceber(dto.getValorTotal());
        transacao.setQtdParcela(dto.getNumeroParcelas());
        
        // Status inicial = InProgress (padrão ApiUtilsHotBeach)
        transacao.setBloqueadoParaProcessamento(true);
        transacao.setStatus("InProgress");
        transacao.setGatewayPagamento(dto.getAdquirente());
        transacao.setAutorizado(false);
        
        // Campos de retorno da operadora (já vêm preenchidos)
        transacao.setNsu(dto.getNsu());
        transacao.setTid(dto.getTid());
        transacao.setCodigoAutorizacao(dto.getCodigoAutorizacao());
        
        transacao.setEstornado(false);
        
        return transacao;
    }
    
    /**
     * Atualiza a transação com o resultado do pagamento (chamado após processamento)
     * Similar ao atualizarFromPagamentoCartaoResultado do ApiUtilsHotBeach
     */
    private void atualizarTransacaoComResultado(
            TransacaoDebitoCredito transacao,
            ProcessarPagamentoAprovadoTseDto dto) {
        
        // Atualizar com dados reais da operadora (GetNet ou eRede)
        transacao.setAutorizado(true);
        transacao.setStatus(dto.getStatus()); // Status da operadora (APROVADA, NEGADA, ERRO)
        transacao.setStatusGenerico("AUTORIZADO");
        transacao.setMensagemRetorno(dto.getMensagemRetorno()); // Mensagem retorno da operadora
        transacao.setCodigoRetorno(dto.getCodigoRetorno()); // Código retorno da operadora
        transacao.setBloqueadoParaProcessamento(false);
        
        // Atualizar IDs da transação
        transacao.setNsu(dto.getNsu());
        transacao.setTid(dto.getTid());
        transacao.setCodigoAutorizacao(dto.getCodigoAutorizacao());
    }
    
    private ContaFinanceira criarContaConsolidada(
            List<ContaFinanceira> contasOriginais,
            TransacaoDebitoCredito transacao,
            ProcessarPagamentoAprovadoTseDto dto,
            OperadorSistema responsavel,
            BandeiraCartao bandeiraCartao,
            boolean isPix) {
        
        // Usar a primeira conta como base
        ContaFinanceira base = contasOriginais.get(0);
        
        // Buscar meio de pagamento pelo ID configurado
        MeioPagamento meioPagamentoCartao = meioPagamentoRepository.findById(idMeioPagamentoPortalCartao)
                .orElseThrow(() -> new PagamentoCartaoException(
                        "Meio de pagamento não encontrado para ID: " + idMeioPagamentoPortalCartao));
        
        // Buscar origem da conta - Se alguma conta for de SALDO, usar SALDO
        TipoOrigemContaFinanceira origemConta = determinarOrigemConta(contasOriginais, base);
        
        // Calcular valores totais das contas canceladas
        ValoresTotais valores = calcularValoresTotais(contasOriginais);
        
        // Calcular número da parcela (buscar último número de parcela do contrato/origem)
        Integer numeroParcela = calcularNumeroParcela(base.getContrato().getId(), origemConta.getIdTipoOrigemContaFinanceira());
        
        // Calcular juros e multa ANTES de cancelar as contas
        // Usa os métodos calcularJuros() e calcularMulta() da própria entidade
        BigDecimal valorJurosCalculado = contasOriginais.stream()
                .map(ContaFinanceira::calcularJuros)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal valorMultaCalculado = contasOriginais.stream()
                .map(ContaFinanceira::calcularMulta)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        log.info("Valores de juros e multa CALCULADOS das contas vencidas - Juros: {}, Multa: {}", 
                valorJurosCalculado, valorMultaCalculado);
        
        // Buscar meio de pagamento apropriado
        MeioPagamento meioPagamento;
        if (isPix) {
            // Para PIX, usar o meio de pagamento configurado
            meioPagamento = meioPagamentoRepository.findById(idMeioPagamentoPortalPix)
                    .orElseThrow(() -> new PagamentoCartaoException(
                            "Meio de pagamento PIX não encontrado para ID: " + idMeioPagamentoPortalPix));
            
            log.info("Usando meio de pagamento PIX - ID: {}, Código: {}", 
                    idMeioPagamentoPortalPix, meioPagamento.getCodMeioPagamento());
        } else {
            // Para cartão, usar o configurado (MANTÉM FLUXO ORIGINAL)
            meioPagamento = meioPagamentoRepository.findById(idMeioPagamentoPortalCartao)
                    .orElseThrow(() -> new PagamentoCartaoException(
                            "Meio de pagamento CARTÃO não encontrado para ID: " + idMeioPagamentoPortalCartao));
        }
        
        // Usar método factory do Aggregate Root
        ContaFinanceira contaNova = ContaFinanceira.criarContaConsolidadaPagamentoPortal(
                base,
                dto.getValorTotal(),
                valores.totalAcrescimo,
                valorJurosCalculado, // Soma dos juros calculados em tela
                valorMultaCalculado, // Soma das multas calculadas em tela
                valores.totalDescontos, // Soma dos descontos das parcelas canceladas
                isPix ? null : transacao.getId(), // Para PIX, não há transação de cartão
                dto.getCodigoAutorizacao(),
                isPix ? "PIX" : dto.getAdquirente(),
                dto.getNsu(),
                dto.getIdTransacao(), // ID do pedido do portal para guidMerchantOrderId
                meioPagamento,
                origemConta,
                responsavel,
                isPix ? null : bandeiraCartao, // Para PIX, não há bandeira de cartão
                LocalDateTime.now() // Data da autorização (momento atual)
        );
        
        // Configurar campos adicionais conforme especificação
        configurarCamposAdicionaisContaNova(contaNova, contasOriginais, bandeiraCartao, numeroParcela, dto, isPix);
        
        return contaNova;
    }
    
    private Integer calcularNumeroParcela(Long idContrato, Integer idOrigemConta) {
        Integer ultimoNroParcela = contaFinanceiraRepository.obterUltimoNroParcelaContratoOrigem(idContrato, idOrigemConta);
        if (ultimoNroParcela == null) {
            ultimoNroParcela = 0;
        }
        return ultimoNroParcela + 1;
    }
    
    private void configurarCamposAdicionaisContaNova(
            ContaFinanceira contaNova,
            List<ContaFinanceira> contasOriginais,
            BandeiraCartao bandeiraCartao,
            Integer numeroParcela,
            ProcessarPagamentoAprovadoTseDto dto,
            boolean isPix) {
        
        // Pegar idUnidadeNegocio de uma das parcelas canceladas
        Long idUnidadeNegocio = contasOriginais.stream()
                .map(ContaFinanceira::getIdUnidadeNegocio)
                .filter(id -> id != null)
                .findFirst()
                .orElse(null);
        
        // Valor receber e valor parcela = soma das contas canceladas
        BigDecimal somaValorReceber = contasOriginais.stream()
                .map(ContaFinanceira::getValorReceber)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal somaValorParcela = contasOriginais.stream()
                .map(ContaFinanceira::getValorParcela)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Valor desconto, acréscimo e desconto manual = somar das parcelas canceladas
        BigDecimal somaValorDesconto = contasOriginais.stream()
                .map(ContaFinanceira::getValorDesconto)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal somaValorAcrescimo = contasOriginais.stream()
                .map(ContaFinanceira::getValorAcrescimo)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal somaValorDescontoManual = contasOriginais.stream()
                .map(ContaFinanceira::getValorDescontoManual)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Taxa do cartão (da tabela bandeiracartao) - apenas para CARTAO
        BigDecimal taxaCartao = BigDecimal.ZERO;
        BigDecimal descontoTaxaCartao = BigDecimal.ZERO;
        Long idBandeirasAceitas = null;
        
        if (!isPix && bandeiraCartao != null) {
            taxaCartao = bandeiraCartao.getTaxaOperacao() != null 
                    ? BigDecimal.valueOf(bandeiraCartao.getTaxaOperacao()) 
                    : BigDecimal.ZERO;
            
            // Desconto taxa cartão = valorPago * taxaCartao / 100
            descontoTaxaCartao = dto.getValorTotal()
                    .multiply(taxaCartao)
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            
            // ID bandeira aceitas (converter Integer para Long)
            idBandeirasAceitas = bandeiraCartao.getIdBandeirasAceitas() != null 
                    ? bandeiraCartao.getIdBandeirasAceitas().longValue() 
                    : null;
        }
        
        // Calcular juros e multa das contas originais (antes de cancelar)
        BigDecimal valorJuros = contasOriginais.stream()
                .map(ContaFinanceira::calcularJuros)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal valorMulta = contasOriginais.stream()
                .map(ContaFinanceira::calcularMulta)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        log.info("DEBUG - Configurando campos adicionais - Juros CALCULADOS: {}, Multa CALCULADA: {}", valorJuros, valorMulta);
        
        // Logar valores individuais das contas para debug
        contasOriginais.forEach(conta -> 
            log.info("DEBUG - Conta ID: {}, Status: {}, Juros Calculado: {}, Multa Calculada: {}", 
                conta.getId(), conta.calcularStatus(), conta.calcularJuros(), conta.calcularMulta())
        );
        
        // Usar método público da ContaFinanceira para configurar todos os campos
        contaNova.configurarCamposNegociacaoPortal(
                idUnidadeNegocio,
                numeroParcela,
                taxaCartao,
                descontoTaxaCartao,
                somaValorReceber,
                somaValorParcela,
                somaValorDesconto,
                somaValorAcrescimo,
                somaValorDescontoManual,
                valorJuros, // Valor calculado em tela no portal
                valorMulta, // Valor calculado em tela no portal
                idBandeirasAceitas
        );
        
        log.info("DEBUG - Após configurar - Conta nova Juros: {}, Multa: {}", 
                contaNova.getValorJuros(), contaNova.getValorMulta());
    }
    
    private ValoresTotais calcularValoresTotais(List<ContaFinanceira> contasOriginais) {
        BigDecimal totalAcrescimo = BigDecimal.ZERO;
        BigDecimal totalDescontos = BigDecimal.ZERO; // Soma dos descontos para correção monetária
        
        // Somar todos os valores das contas originais
        for (ContaFinanceira conta : contasOriginais) {
            if (conta.getValorAcrescimo() != null) {
                totalAcrescimo = totalAcrescimo.add(conta.getValorAcrescimo());
            }
            
            // Somar descontos das parcelas canceladas
            if (conta.getValorDesconto() != null) {
                totalDescontos = totalDescontos.add(conta.getValorDesconto());
            }
            if (conta.getValorDescontoManual() != null) {
                totalDescontos = totalDescontos.add(conta.getValorDescontoManual());
            }
        }
        
        log.info("Valores calculados - Acréscimo: {}, Soma Descontos (para correção monetária): {}", 
                totalAcrescimo, totalDescontos);
        
        return new ValoresTotais(totalAcrescimo, totalDescontos);
    }
    
    private Negociacao criarNegociacao(
            com.sw.tse.domain.model.db.Empresa empresa, 
            OperadorSistema responsavel,
            LocalDateTime dataCadastro) {
        Negociacao negociacao = new Negociacao();
        negociacao.setEmpresa(empresa);
        negociacao.setResponsavelCadastro(responsavel);
        negociacao.setStatusNegociacao(1); // Ativo
        negociacao.setOrigemNegociacao(null); // Null conforme solicitado
        negociacao.setDataCadastro(dataCadastro); // Data sem nanosegundos
        return negociacao;
    }
    
    private String serializarContaOriginal(ContaFinanceira conta) {
        try {
            // Criar um Map com os dados importantes da conta
            Map<String, Object> dadosConta = new HashMap<>();
            dadosConta.put("IdContaFinanceira", conta.getId());
            dadosConta.put("DataVencimento", conta.getDataVencimento());
            dadosConta.put("ValorReceber", conta.getValorReceber());
            dadosConta.put("ValorParcela", conta.getValorParcela());
            dadosConta.put("ValorAcrescimo", conta.getValorAcrescimo());
            dadosConta.put("ValorJuros", conta.getValorJuros());
            dadosConta.put("ValorMulta", conta.getValorMulta());
            dadosConta.put("ValorAcrescimoAcumuladoCorrecaoMonetaria", conta.getValorAcrescimoAcumuladoCorrecaoMonetaria());
            dadosConta.put("DataVencimentoOriginal", conta.getDataVencimentoOriginal());
            dadosConta.put("NumeroDocumento", conta.getNumeroDocumento());
            dadosConta.put("Historico", conta.getHistorico());
            
            return objectMapper.writeValueAsString(dadosConta);
        } catch (Exception e) {
            log.error("Erro ao serializar conta financeira", e);
            return "{}";
        }
    }
    
    /**
     * Determina qual origem usar para a conta consolidada.
     * Regra: Usa sempre a origem da primeira conta (ordenada por data de cadastro)
     */
    private TipoOrigemContaFinanceira determinarOrigemConta(
            List<ContaFinanceira> contasOriginais, 
            ContaFinanceira base) {
        
        // Usar origem da primeira conta da lista (assumindo que vem ordenada por data de cadastro)
        if (!contasOriginais.isEmpty() && contasOriginais.get(0).getOrigemConta() != null) {
            TipoOrigemContaFinanceira origem = contasOriginais.get(0).getOrigemConta();
            log.info("Usando origem da primeira conta: {} (ID: {})", 
                    origem.getSysId(), origem.getIdTipoOrigemContaFinanceira());
            return origem;
        }
        
        // Fallback: usar origem da conta base
        log.warn("Nenhuma origem encontrada nas contas originais, usando origem da conta base");
        return base.getOrigemConta();
    }
    
    // ==================== CLASSES INTERNAS ====================
    
    private static class ValoresTotais {
        final BigDecimal totalAcrescimo;
        final BigDecimal totalDescontos; // Soma dos descontos (vai para valoracrescimoacumuladocorrecaomonetaria)
        
        ValoresTotais(BigDecimal totalAcrescimo, BigDecimal totalDescontos) {
            this.totalAcrescimo = totalAcrescimo;
            this.totalDescontos = totalDescontos;
        }
    }
}

