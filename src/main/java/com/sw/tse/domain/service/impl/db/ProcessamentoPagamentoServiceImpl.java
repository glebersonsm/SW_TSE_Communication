package com.sw.tse.domain.service.impl.db;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.tse.api.dto.ContaFinanceiraParaPagamentoDto;
import com.sw.tse.api.dto.ProcessamentoPagamentoResponseDto;
import com.sw.tse.api.dto.ProcessarPagamentoAprovadoTseDto;
import com.sw.tse.domain.expection.ContaFinanceiraNaoEncontradaException;
import com.sw.tse.domain.expection.OperadorSistemaNotFoundException;
import com.sw.tse.domain.expection.BandeiraCartaoNaoEncontradaException;
import com.sw.tse.domain.expection.PagamentoCartaoException;
import com.sw.tse.domain.expection.PagamentoTseBusinessException;
import com.sw.tse.domain.expection.RegraDeNegocioException;
import com.sw.tse.domain.model.db.BandeiraCartao;
import com.sw.tse.domain.model.db.BandeirasAceitas;
import com.sw.tse.domain.model.db.ContaFinanceira;
import com.sw.tse.domain.model.db.ContaMovimentacaoBancaria;
import com.sw.tse.domain.model.db.Empresa;
import com.sw.tse.domain.model.db.MeioPagamento;
import com.sw.tse.domain.model.db.MovimentacaoBancaria;
import com.sw.tse.domain.model.db.MovimentacaoBancariaContaFinanceira;
import com.sw.tse.domain.model.db.Negociacao;
import com.sw.tse.domain.model.db.NegociacaoContaFinanceira;
import com.sw.tse.domain.model.db.OperadorSistema;
import com.sw.tse.domain.model.db.TipoOrigemContaFinanceira;
import com.sw.tse.domain.model.db.TransacaoDebitoCredito;
import com.sw.tse.domain.repository.BandeiraCartaoRepository;
import com.sw.tse.domain.repository.BandeirasAceitasRepository;
import com.sw.tse.domain.repository.ContaFinanceiraRepository;
import com.sw.tse.domain.repository.ContaMovimentacaoBancariaRepository;
import com.sw.tse.domain.repository.MeioPagamentoRepository;
import com.sw.tse.domain.repository.MovimentacaoBancariaContaFinanceiraRepository;
import com.sw.tse.domain.repository.MovimentacaoBancariaRepository;
import com.sw.tse.domain.repository.NegociacaoContaFinanceiraRepository;
import com.sw.tse.domain.repository.NegociacaoRepository;
import com.sw.tse.domain.repository.OperadorSistemaRepository;
import com.sw.tse.domain.repository.TipoOrigemContaFinanceiraRepository;
import com.sw.tse.domain.repository.TransacaoDebitoCreditoRepository;
import com.sw.tse.domain.service.interfaces.OperadorSistemaService;
import com.sw.tse.domain.service.interfaces.PagamentoCartaoService;
import com.sw.tse.core.util.DiasAtrasoHelper;
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
        private BandeirasAceitasRepository bandeirasAceitasRepository;

        @Autowired
        private ContaMovimentacaoBancariaRepository contaMovimentacaoBancariaRepository;

        @Autowired
        private MovimentacaoBancariaRepository movimentacaoBancariaRepository;

        @Autowired
        private MovimentacaoBancariaContaFinanceiraRepository movimentacaoBancariaContaFinanceiraRepository;

        @PersistenceContext
        private EntityManager entityManager;

        @Autowired
        private OperadorSistemaService operadorSistemaService;

        @Autowired
        private ObjectMapper objectMapper;

        @Override
        @Transactional
        public ProcessamentoPagamentoResponseDto processarPagamentoAprovado(ProcessarPagamentoAprovadoTseDto dto) {
                log.info("Processando pagamento aprovado para empresa {} com {} contas financeiras. IdTransacao: {}",
                                dto.getIdEmpresaTse(), dto.getContasFinanceiras().size(), dto.getIdTransacao());

                try {
                        // 0. Idempotência: Verificar se já existe transação ou negociação para este
                        // MerchantOrderId (idTransacao)
                        if (dto.getIdTransacao() != null && !dto.getIdTransacao().startsWith("TEMP_")) {
                                List<TransacaoDebitoCredito> transacoesExistentes = transacaoRepository
                                                .findByMerchantOrderId(dto.getIdTransacao());
                                if (!transacoesExistentes.isEmpty()) {
                                        log.warn("Transação já processada anteriormente para IdTransacao: {}. NSU: {}",
                                                        dto.getIdTransacao(), transacoesExistentes.get(0).getNsu());

                                        // Buscar negociação vinculada (se houver) para retornar o ID
                                        // Como a transação existe, o sistema pode ter caído antes de marcar como
                                        // sucesso no Portal.
                                        // Vamos retornar sucesso para o Portal completar o ciclo.
                                        return ProcessamentoPagamentoResponseDto.builder()
                                                        .mensagem("Pagamento já processado anteriormente (Idempotência)")
                                                        .status("SUCESSO_EXISTENTE")
                                                        .build();
                                }
                        }

                        // 1. Buscar usuário responsável...
                        Long idUsuario = JwtTokenUtil.getIdUsuarioCliente();
                        if (idUsuario == null || idUsuario == 0) {
                                idUsuario = dto.getIdUsuarioLogado();
                        }
                        final Long idUsuarioParaLog = idUsuario;

                        OperadorSistema responsavel;
                        if (idUsuario != null && idUsuario > 0) {
                                responsavel = operadorSistemaRepository.findById(idUsuario)
                                                .orElseThrow(() -> new OperadorSistemaNotFoundException(
                                                                "Operador sistema não encontrado para ID: "
                                                                                + idUsuarioParaLog));
                                log.info("Usuário responsável: ID {} (JWT/DTO)", idUsuario);
                        } else if (dto.getIdPessoaTse() != null && dto.getIdPessoaTse() > 0) {
                                var resp = operadorSistemaService.buscarPorIdPessoa(dto.getIdPessoaTse());
                                if (resp != null && resp.idOperador() != null && resp.idOperador() > 0) {
                                        responsavel = operadorSistemaRepository.findById(resp.idOperador())
                                                        .orElseThrow(() -> new OperadorSistemaNotFoundException(
                                                                        "Operador não encontrado para idPessoa: "
                                                                                        + dto.getIdPessoaTse()));
                                        log.info("Usuário responsável: ID {} (idPessoaTse {} - job)",
                                                        responsavel.getId(),
                                                        dto.getIdPessoaTse());
                                } else {
                                        responsavel = operadorSistemaService.operadorSistemaPadraoCadastro();
                                        log.info("Usuário responsável: operador padrão (idPessoaTse sem operador)");
                                }
                        } else {
                                responsavel = operadorSistemaService.operadorSistemaPadraoCadastro();
                                log.info("Usuário responsável: operador padrão (sem JWT/DTO/idPessoaTse)");
                        }

                        // 2. Buscar contas financeiras originais
                        List<Long> idsContas = dto.getContasFinanceiras().stream()
                                        .map(ContaFinanceiraParaPagamentoDto::getIdContaFinanceiraTse)
                                        .collect(Collectors.toList());

                        List<ContaFinanceira> contasOriginais = contaFinanceiraRepository.findAllById(idsContas);

                        if (contasOriginais.isEmpty()) {
                                throw new ContaFinanceiraNaoEncontradaException(idsContas);
                        }

                        log.info("Encontradas {} contas originais para processar", contasOriginais.size());

                        // Validação de status das contas (Bloqueio de processamento automático)
                        for (ContaFinanceira conta : contasOriginais) {
                                if (conta.getDataCancelamento() != null) {
                                        throw new PagamentoTseBusinessException(String.format(
                                                        "ID: %d - Conta cancelada no sistema de origem (TSE). É necessária atuação manual de um operador.",
                                                        conta.getId()));
                                }
                                if (conta.isPagoCalculado()) {
                                        throw new PagamentoTseBusinessException(String.format(
                                                        "ID: %d - Conta já se encontra baixada, paga ou em processo de quitação no sistema de origem (TSE).",
                                                        conta.getId()));
                                }
                                if (Boolean.TRUE.equals(conta.getRecorrenciaAutorizada())) {
                                        throw new PagamentoTseBusinessException(String.format(
                                                        "ID: %d - Conta possui recorrência autorizada configurada. Não é permitido o processamento via portal sem intervenção.",
                                                        conta.getId()));
                                }
                        }

                        // Mapear DTOs para acesso rápido por ID
                        Map<Long, ContaFinanceiraParaPagamentoDto> mapaDtos = dto.getContasFinanceiras().stream()
                                        .collect(Collectors.toMap(
                                                        ContaFinanceiraParaPagamentoDto::getIdContaFinanceiraTse,
                                                        c -> c));

                        log.info("IdBandeira recebido no DTO: {}", dto.getIdBandeira());
                        log.info("Meio de pagamento recebido: {}", dto.getMeioPagamento());

                        // Verificar se é pagamento PIX (não precisa de bandeira nem transação de
                        // cartão)
                        boolean isPix = "PIX".equalsIgnoreCase(dto.getMeioPagamento());

                        BandeiraCartao bandeiraCartao = null;
                        TransacaoDebitoCredito transacao = null;

                        if (!isPix) {
                                // 3. Buscar BandeiraCartao (para taxa e configurações) - OBRIGATÓRIO para
                                // CARTAO
                                bandeiraCartao = buscarBandeiraCartao(
                                                contasOriginais.get(0).getEmpresa().getId(),
                                                dto.getIdBandeira(),
                                                dto.getAdquirente());

                                if (bandeiraCartao != null) {
                                        log.info("BandeiraCartao encontrada: ID {}, Taxa: {}",
                                                        bandeiraCartao.getIdBandeiraCartao(),
                                                        bandeiraCartao.getTaxaOperacao());
                                } else {
                                        // REGRA: Usar OBRIGATORIAMENTE a conta do GatewayPagamentoConfiguracao (vem do
                                        // DTO)
                                        if (dto.getIdContaMovimentacaoBancaria() == null) {
                                                String nomeGatewayMsg = extrairNomeGateway(dto.getAdquirente());
                                                throw new BandeiraCartaoNaoEncontradaException(
                                                                String.format(
                                                                                "Configuração de bandeira de cartão não encontrada no TSE para esta empresa. "
                                                                                                + "Cadastre um registro em Bandeiras de Cartão no TSE com: "
                                                                                                + "Empresa=%d, Bandeira ID=%d, Operação='CREDAV', Status=Ativo, "
                                                                                                + "e Nome do Estabelecimento contendo '%s'. "
                                                                                                + "Consulte as empresas que funcionam para verificar o padrão de configuração.",
                                                                                contasOriginais.get(0).getEmpresa()
                                                                                                .getId(),
                                                                                dto.getIdBandeira(),
                                                                                nomeGatewayMsg));
                                        }

                                        log.info("Usando conta de movimentação do GatewayPagamentoConfiguracao: ID {}",
                                                        dto.getIdContaMovimentacaoBancaria());

                                        // Criar automaticamente BandeiraCartao padrão para não bloquear o fluxo
                                        log.warn(
                                                        "BandeiraCartao não encontrada. Criando configuração padrão para Tenant: {}, IdBandeira: {}, Gateway: {}, IdContaMovBancaria: {}",
                                                        contasOriginais.get(0).getEmpresa().getId(),
                                                        dto.getIdBandeira(), dto.getAdquirente(),
                                                        dto.getIdContaMovimentacaoBancaria());

                                        bandeiraCartao = criarBandeiraCartaoPadrao(
                                                        contasOriginais.get(0).getEmpresa(),
                                                        dto.getIdBandeira(),
                                                        dto.getAdquirente(),
                                                        dto.getIdContaMovimentacaoBancaria());

                                        log.info("BandeiraCartao criada automaticamente: ID {}, Taxa: {}%, IdContaMovBancaria: {}",
                                                        bandeiraCartao.getIdBandeiraCartao(),
                                                        bandeiraCartao.getTaxaOperacao(),
                                                        bandeiraCartao.getIdContaMovBancaria());
                                }

                                // 4. Criar TransacaoDebitoCredito (apenas para CARTAO)
                                transacao = criarTransacaoDebitoCredito(
                                                dto, contasOriginais.get(0), responsavel, bandeiraCartao);
                                transacaoRepository.save(transacao);
                                log.info("TransacaoDebitoCredito criada com ID: {}, Status: InProgress",
                                                transacao.getId());

                                // 4.1. Atualizar transação com resultado do pagamento aprovado
                                atualizarTransacaoComResultado(transacao, dto);
                                transacaoRepository.save(transacao);
                                log.info("TransacaoDebitoCredito atualizada - Status: {}, NSU: {}",
                                                transacao.getStatus(),
                                                transacao.getNsu());
                        } else {
                                log.info("Pagamento PIX - Não será criada TransacaoDebitoCredito");
                        }

                        // 5. Verificar se é caso de alteração de conta ÚNICA (Pix ou Cartão 1x)
                        // Lógica: 1 conta selecionada E (Pix OU (Cartão E 1 parcela))
                        boolean isContaUnicaAlteracao = contasOriginais.size() == 1
                                        && (isPix || (dto.getNumeroParcelas() != null && dto.getNumeroParcelas() == 1));

                        if (isContaUnicaAlteracao) {
                                // === FLUXO ALTERAÇÃO DE CONTA EXISTENTE ===
                                ContaFinanceira contaOriginal = contasOriginais.get(0);
                                log.info("Processando alteração de conta existente - ID: {}", contaOriginal.getId());

                                // 5.1 Salvar snapshot JSON ANTES da alteração
                                String jsonContaOriginal = serializarContaOriginal(contaOriginal);

                                // 5.2 Criar data de cadastro (Usar data enviada ou agora)
                                LocalDateTime dataCadastroNegociacao = dto.getDataPagamento() != null
                                                ? dto.getDataPagamento().truncatedTo(ChronoUnit.SECONDS)
                                                : LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

                                // 5.3 Criar Negociação
                                Negociacao negociacao = criarNegociacao(contaOriginal.getEmpresa(), responsavel,
                                                dataCadastroNegociacao);
                                negociacaoRepository.save(negociacao);
                                log.info("Negociação criada para alteração - ID: {}", negociacao.getId());

                                // 5.4 Vincular conta na negociação (tipo 3 = Alterada)
                                NegociacaoContaFinanceira ncfAlterada = new NegociacaoContaFinanceira();
                                ncfAlterada.setNegociacao(negociacao);
                                ncfAlterada.setContaFinanceira(contaOriginal);
                                ncfAlterada.setTipoNegociacao(3); // 3 = Alterada
                                ncfAlterada.setEmpresa(negociacao.getEmpresa());
                                ncfAlterada.setResponsavelCadastro(responsavel);
                                ncfAlterada.setContaFinanceiraJson(jsonContaOriginal);
                                ncfAlterada.setDataCadastro(dataCadastroNegociacao);
                                negociacaoContaFinanceiraRepository.save(ncfAlterada);

                                // 5.5 Determinar conta de movimentação bancária (Igual ao fluxo de
                                // consolidação)
                                ContaMovimentacaoBancaria contaMovimentacao = determinarContaMovimentacao(
                                                dto, contaOriginal, bandeiraCartao, isPix);

                                // 5.6 Atualizar conta original IN-PLACE
                                OperadorSistema operadorPadrao = operadorSistemaService.operadorSistemaPadraoCadastro();

                                // Buscar valores do DTO para esta conta
                                ContaFinanceiraParaPagamentoDto contaDto = mapaDtos.get(contaOriginal.getId());

                                atualizarContaExistente(
                                                contaOriginal,
                                                dto,
                                                contaDto, // Passar DTO específico da conta
                                                responsavel,
                                                bandeiraCartao,
                                                contaMovimentacao,
                                                isPix,
                                                transacao,
                                                operadorPadrao);

                                contaFinanceiraRepository.save(contaOriginal);
                                log.info("Conta original {} atualizada com sucesso. Status: {}",
                                                contaOriginal.getId(), contaOriginal.calcularStatus());

                                // 5.7 Se for PIX, criar MovimentacaoBancaria (Igual ao fluxo de consolidação)
                                if (isPix && contaOriginal.getPago()
                                                && contaOriginal.getContaMovimentacaoBancaria() != null) {
                                        criarMovimentacaoBancariaPix(contaOriginal, dto, operadorPadrao);
                                }

                                log.info("Processamento de conta única finalizado. TransacaoId: {}, NegociacaoId: {}",
                                                dto.getIdTransacao(), negociacao.getId());

                                return ProcessamentoPagamentoResponseDto.builder()
                                                .idNegociacao(negociacao.getId())
                                                .mensagem("Pagamento processado com sucesso (Conta Única)")
                                                .status("SUCESSO")
                                                .build();

                        } else {
                                // === FLUXO PADRÃO: CONSOLIDAÇÃO (Cria Nova, Cancela Originais) ===

                                // 5. Criar conta consolidada (passa null para transacao se for PIX)
                                ContaFinanceira contaNova = criarContaConsolidada(contasOriginais, mapaDtos, transacao,
                                                dto,
                                                responsavel,
                                                bandeiraCartao, isPix);
                                contaFinanceiraRepository.save(contaNova);
                                log.info("Conta consolidada criada com ID: {}, Valor: {}, Status: {}, Pago: {}",
                                                contaNova.getId(), contaNova.getValorReceber(),
                                                contaNova.getTipoHistorico(),
                                                contaNova.getPago());

                                // 5.1. Se for PIX, criar MovimentacaoBancaria (pagamento já foi recebido)
                                if (isPix && contaNova.getPago() && contaNova.getContaMovimentacaoBancaria() != null) {
                                        OperadorSistema operadorPadrao = operadorSistemaService
                                                        .operadorSistemaPadraoCadastro();
                                        criarMovimentacaoBancariaPix(contaNova, dto, operadorPadrao);
                                }

                                // 6. Criar data de cadastro (Usar data enviada ou agora)
                                // Mesma data para Negociacao e todas as NegociacaoContaFinanceira
                                LocalDateTime dataCadastroNegociacao = dto.getDataPagamento() != null
                                                ? dto.getDataPagamento().truncatedTo(ChronoUnit.SECONDS)
                                                : LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

                                // 7. Criar Negociação
                                Negociacao negociacao = criarNegociacao(contasOriginais.get(0).getEmpresa(),
                                                responsavel,
                                                dataCadastroNegociacao);
                                negociacaoRepository.save(negociacao);
                                log.info("Negociação criada com ID: {}, DataCadastro: {}", negociacao.getId(),
                                                negociacao.getDataCadastro());

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
                                        String historicoCancelamento = isPix
                                                        ? "Cancelada - Paga via Portal com PIX"
                                                        : "Cancelada - Paga via Portal com Cartão";
                                        contaOriginal.cancelar(responsavel, historicoCancelamento);
                                        contaFinanceiraRepository.save(contaOriginal);

                                        log.info("Conta {} vinculada à negociação e cancelada (JSON salvo)",
                                                        contaOriginal.getId());
                                }

                                log.info(
                                                "Pagamento processado com sucesso. TransacaoId: {}, NSU: {}, NegociacaoId: {}, Contas vinculadas: {}",
                                                dto.getIdTransacao(), dto.getNsu(), negociacao.getId(),
                                                contasOriginais.size() + 1);

                                return ProcessamentoPagamentoResponseDto.builder()
                                                .idNegociacao(negociacao.getId())
                                                .mensagem("Pagamento processado com sucesso (Consolidação)")
                                                .status("SUCESSO")
                                                .build();
                        }

                } catch (OperadorSistemaNotFoundException | ContaFinanceiraNaoEncontradaException
                                | RegraDeNegocioException e) {
                        // Re-lançar exceptions específicas sem wrapper para manter a mensagem limpa no
                        // portal
                        throw e;
                } catch (Exception e) {
                        log.error("Erro ao processar pagamento aprovado", e);
                        throw new PagamentoCartaoException("Erro ao processar pagamento aprovado: " + e.getMessage(),
                                        e);
                }
        }

        // ==================== MÉTODOS AUXILIARES ====================

        private BandeiraCartao buscarBandeiraCartao(Long idTenant, Integer idBandeira, String gatewaySysId) {
                if (idBandeira == null || idBandeira == 0) {
                        throw new PagamentoCartaoException(
                                        "ID da bandeira do cartão é obrigatório para processar pagamento");
                }

                // Extrair apenas "REDE" ou "GETNET" do SysId (ex: "GATEWAY_PAGAMENTO_REDE" ->
                // "REDE")
                String nomeGateway = extrairNomeGateway(gatewaySysId);

                log.info("Buscando BandeiraCartao. Tenant: {}, IdBandeira: {}, Gateway original: '{}', Nome extraído: '{}'",
                                idTenant, idBandeira, gatewaySysId, nomeGateway);

                // Buscar todos os registros que atendem aos critérios (exceto nome)
                // O campo nomeEstabelecimento é criptografado, então precisamos comparar em
                // memória
                java.util.List<BandeiraCartao> candidatos = bandeiraCartaoRepository.findBandeirasAtivasParaPagamento(
                                idTenant,
                                idBandeira);

                log.info("Encontrados {} candidatos de BandeiraCartao para filtrar por nome do gateway",
                                candidatos.size());

                // Filtrar em memória pelo nome do gateway (campo descriptografado)
                BandeiraCartao bandeira = candidatos.stream()
                                .filter(bc -> {
                                        String nomeEstab = bc.getNomeEstabelecimento();
                                        if (nomeEstab == null)
                                                return false;
                                        String nomeEstabUpper = nomeEstab.toUpperCase();
                                        boolean match = nomeEstabUpper.contains(nomeGateway.toUpperCase());
                                        log.debug("Comparando: '{}' contém '{}'? {}", nomeEstabUpper, nomeGateway,
                                                        match);
                                        return match;
                                })
                                .findFirst()
                                .orElse(null);

                if (bandeira == null) {
                        log.warn("BandeiraCartao não encontrada para tenant: {}, idBandeira: {}, nome gateway: '{}'. "
                                        + "Retornando null — o chamador decidirá o fallback ou lançará exceção.",
                                        idTenant, idBandeira, nomeGateway);
                } else {
                        log.info("BandeiraCartao encontrada: ID {}, NomeEstabelecimento contém '{}', TaxaOperacao: {}",
                                        bandeira.getIdBandeiraCartao(), nomeGateway, bandeira.getTaxaOperacao());
                }

                return bandeira;
        }

        /**
         * Extrai o nome do gateway do SysId
         * Ex: "GATEWAY_PAGAMENTO_REDE" -> "REDE"
         * "GATEWAY_PAGAMENTO_GETNET" -> "GETNET"
         */
        private String extrairNomeGateway(String gatewaySysId) {
                if (gatewaySysId == null)
                        return "";

                String upper = gatewaySysId.toUpperCase();
                if (upper.contains("GETNET"))
                        return "GETNET";
                if (upper.contains("REDE"))
                        return "REDE";

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

                // No reprocessamento, dados sensíveis do cartão não são enviados por segurança.
                // Usa mascarado para o número e "N/A" para CVV/validade (satisfaz NOT NULL do
                // banco).
                String numeroCartaoParaSalvar = dto.getNumeroCartao() != null
                                ? dto.getNumeroCartao()
                                : dto.getNumeroCartaoMascarado();
                transacao.setNumeroCartao(numeroCartaoParaSalvar);
                transacao.setCodSegurancaCartao(
                                dto.getCodigoSegurancaCartao() != null ? dto.getCodigoSegurancaCartao() : "N/A");
                transacao.setMesValidadeCartao(
                                dto.getMesValidadeCartao() != null ? dto.getMesValidadeCartao() : "N/A");
                transacao.setAnoValidadeCartao(
                                dto.getAnoValidadeCartao() != null ? dto.getAnoValidadeCartao() : "N/A");
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
         * Atualiza a transação com o resultado do pagamento (chamado após
         * processamento)
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

        // Extrair método para determinar conta movimentação (reuso)
        private ContaMovimentacaoBancaria determinarContaMovimentacao(
                        ProcessarPagamentoAprovadoTseDto dto,
                        ContaFinanceira base,
                        BandeiraCartao bandeiraCartao,
                        boolean isPix) {

                ContaMovimentacaoBancaria contaMovimentacao = null;
                if (isPix) {
                        // Para PIX: usar a conta configurada no gateway (vem do DTO)
                        if (dto.getIdContaMovimentacaoBancaria() != null) {
                                contaMovimentacao = contaMovimentacaoBancariaRepository
                                                .findById(dto.getIdContaMovimentacaoBancaria())
                                                .orElse(null);

                                if (contaMovimentacao != null) {
                                        log.info("PIX: Usando conta de movimentação do gateway - ID: {}, Banco: {}",
                                                        contaMovimentacao.getId(),
                                                        contaMovimentacao.getBanco() != null
                                                                        ? contaMovimentacao.getBanco().getDescricao()
                                                                        : "N/A");
                                } else {
                                        log.warn("PIX: Conta de movimentação ID {} não encontrada, usando conta da base",
                                                        dto.getIdContaMovimentacaoBancaria());
                                        contaMovimentacao = base.getContaMovimentacaoBancaria();
                                }
                        } else {
                                log.warn("PIX: idContaMovimentacaoBancaria não informado no DTO, usando conta da base");
                                contaMovimentacao = base.getContaMovimentacaoBancaria();
                        }
                } else {
                        // Para CARTÃO: usar a conta que está na BandeiraCartao (taxa)
                        if (bandeiraCartao != null && bandeiraCartao.getIdContaMovBancaria() != null) {
                                Integer idContaMov = bandeiraCartao.getIdContaMovBancaria();
                                contaMovimentacao = contaMovimentacaoBancariaRepository.findById(idContaMov.longValue())
                                                .orElse(null);

                                if (contaMovimentacao != null) {
                                        log.info("CARTÃO: Usando conta de movimentação da BandeiraCartao - ID: {}, Banco: {}",
                                                        contaMovimentacao.getId(),
                                                        contaMovimentacao.getBanco() != null
                                                                        ? contaMovimentacao.getBanco().getDescricao()
                                                                        : "N/A");
                                } else {
                                        log.warn("CARTÃO: Conta de movimentação ID {} não encontrada, usando conta da base",
                                                        idContaMov);
                                        contaMovimentacao = base.getContaMovimentacaoBancaria();
                                }
                        } else {
                                log.warn("CARTÃO: BandeiraCartao sem idContaMovBancaria, usando conta da base");
                                contaMovimentacao = base.getContaMovimentacaoBancaria();
                        }
                }
                return contaMovimentacao;
        }

        // Extrair método para criar MovimentacaoBancaria Pix (reuso)
        private void criarMovimentacaoBancariaPix(
                        ContaFinanceira conta,
                        ProcessarPagamentoAprovadoTseDto dto,
                        OperadorSistema operadorPadrao) {

                MovimentacaoBancaria movimentacao = new MovimentacaoBancaria();
                movimentacao.setEmpresa(conta.getEmpresa());
                movimentacao.setContaMovimentacaoBancaria(conta.getContaMovimentacaoBancaria());
                // Regra: data da movimentação (extrato) deve ser no próximo dia útil para PIX
                // em feriados/fds
                LocalDate proximoDiaUtil = DiasAtrasoHelper.obterProximoDiaUtil(LocalDate.now());
                movimentacao.setData(proximoDiaUtil.atStartOfDay());
                movimentacao.setHistorico("Recebimento PIX - Conta " + conta.getId() + " - NSU: " + dto.getNsu());
                movimentacao.setValor(dto.getValorTotal());
                movimentacao.setDebitoCreditoMovimentacaoBancaria(0); // 0 = Crédito (recebimento)
                movimentacao.setLancamentoManual(false);
                movimentacao.setResponsavelCadastro(operadorPadrao);
                movimentacao.setTransferencia(false);
                movimentacao.setEstornado(false);

                movimentacaoBancariaRepository.save(movimentacao);
                log.info("MovimentacaoBancaria criada para PIX - ID: {}, Valor: {}",
                                movimentacao.getId(), movimentacao.getValor());

                MovimentacaoBancariaContaFinanceira vinculo = new MovimentacaoBancariaContaFinanceira();
                vinculo.setMovimentacaoBancaria(movimentacao);
                vinculo.setContaFinanceira(conta);
                vinculo.setValor(dto.getValorTotal());
                vinculo.setEstornado(false);
                vinculo.setResponsavelCadastro(operadorPadrao);
                vinculo.setEmpresa(conta.getEmpresa());

                movimentacaoBancariaContaFinanceiraRepository.save(vinculo);
                log.info("Vínculo MovimentacaoBancariaContaFinanceira criado - ID: {}", vinculo.getId());
        }

        // Método para atualizar conta existente (Single Account Update)
        private void atualizarContaExistente(
                        ContaFinanceira conta,
                        ProcessarPagamentoAprovadoTseDto dto,
                        ContaFinanceiraParaPagamentoDto contaDto,
                        OperadorSistema responsavel,
                        BandeiraCartao bandeiraCartao,
                        ContaMovimentacaoBancaria contaMovimentacaoBancaria,
                        boolean isPix,
                        TransacaoDebitoCredito transacao,
                        OperadorSistema operadorPadrao) {

                // Resolve MeioPagamento (mantém o ATUAL se não encontrar o novo, conforme
                // comportamento original do ifPresent)
                MeioPagamento meioPagamento = null;
                if (isPix) {
                        meioPagamento = meioPagamentoRepository.findById(idMeioPagamentoPortalPix)
                                        .orElse(conta.getMeioPagamento());
                } else {
                        meioPagamento = meioPagamentoRepository.findById(idMeioPagamentoPortalCartao)
                                        .orElse(conta.getMeioPagamento());
                }

                // PRIORIDADE: Juros e multas vindos do DTO (evita recálculo dinâmico que pode
                // falhar)
                BigDecimal valorJurosCobrado = (contaDto != null && contaDto.getValorJuros() != null)
                                ? contaDto.getValorJuros()
                                : conta.calcularJuros();

                BigDecimal valorMultaCobrada = (contaDto != null && contaDto.getValorMulta() != null)
                                ? contaDto.getValorMulta()
                                : conta.calcularMulta();

                // Delegar atualização para a entidade (DDD)
                conta.atualizarDadosPagamentoPortal(
                                responsavel,
                                contaMovimentacaoBancaria,
                                meioPagamento,
                                dto.getValorTotal(),
                                valorJurosCobrado,
                                valorMultaCobrada,
                                dto.getIdTransacao(),
                                bandeiraCartao,
                                isPix,
                                dto.getCodigoAutorizacao(),
                                dto.getNsu(),
                                dto.getAdquirente(),
                                transacao != null ? transacao.getId() : null,
                                operadorPadrao,
                                dto.getPixCopiaECola(),
                                dto.getCodigoAutorizacao(), // txId
                                dto.getDataGeracaoPix());
        }

        private ContaFinanceira criarContaConsolidada(
                        List<ContaFinanceira> contasOriginais,
                        Map<Long, ContaFinanceiraParaPagamentoDto> mapaDtos,
                        TransacaoDebitoCredito transacao,
                        ProcessarPagamentoAprovadoTseDto dto,
                        OperadorSistema responsavel,
                        BandeiraCartao bandeiraCartao,
                        boolean isPix) {

                // Usar a primeira conta como base
                ContaFinanceira base = contasOriginais.get(0);

                // Buscar origem da conta - Se alguma conta for de SALDO, usar SALDO
                TipoOrigemContaFinanceira origemConta = determinarOrigemConta(contasOriginais, base);

                // Calcular valores totais das contas canceladas
                ValoresTotais valores = calcularValoresTotais(contasOriginais);

                // Calcular número da parcela (buscar último número de parcela do
                // contrato/origem)
                Integer numeroParcela = calcularNumeroParcela(base.getContrato().getId(),
                                origemConta.getIdTipoOrigemContaFinanceira());

                // PRIORIDADE: Juros e multas vindos dos DTOs vinculados
                BigDecimal valorJurosCalculado = contasOriginais.stream()
                                .map(co -> {
                                        ContaFinanceiraParaPagamentoDto cd = mapaDtos.get(co.getId());
                                        return (cd != null && cd.getValorJuros() != null) ? cd.getValorJuros()
                                                        : co.calcularJuros();
                                })
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal valorMultaCalculado = contasOriginais.stream()
                                .map(co -> {
                                        ContaFinanceiraParaPagamentoDto cd = mapaDtos.get(co.getId());
                                        return (cd != null && cd.getValorMulta() != null) ? cd.getValorMulta()
                                                        : co.calcularMulta();
                                })
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                log.info("Valores de juros e multa utilizados - Juros: {}, Multa: {}",
                                valorJurosCalculado, valorMultaCalculado);

                // Buscar meio de pagamento apropriado
                MeioPagamento meioPagamento;
                if (isPix) {
                        // Para PIX, usar o meio de pagamento configurado
                        meioPagamento = meioPagamentoRepository.findById(idMeioPagamentoPortalPix)
                                        .orElseThrow(() -> new PagamentoCartaoException(
                                                        "Meio de pagamento PIX não encontrado para ID: "
                                                                        + idMeioPagamentoPortalPix));

                        log.info("Usando meio de pagamento PIX - ID: {}, Código: {}",
                                        idMeioPagamentoPortalPix, meioPagamento.getCodMeioPagamento());
                } else {
                        // Para cartão, usar o configurado (MANTÉM FLUXO ORIGINAL)
                        meioPagamento = meioPagamentoRepository.findById(idMeioPagamentoPortalCartao)
                                        .orElseThrow(() -> new PagamentoCartaoException(
                                                        "Meio de pagamento CARTÃO não encontrado para ID: "
                                                                        + idMeioPagamentoPortalCartao));
                }

                // Determinar conta de movimentação bancária
                ContaMovimentacaoBancaria contaMovimentacao = determinarContaMovimentacao(dto, base, bandeiraCartao,
                                isPix);

                // Buscar operador padrão do sistema
                OperadorSistema operadorPadrao = operadorSistemaService.operadorSistemaPadraoCadastro();

                // Usar método factory do Aggregate Root
                ContaFinanceira contaNova = ContaFinanceira.criarContaConsolidadaPagamentoPortal(
                                base,
                                dto.getValorTotal(),
                                valores.totalAcrescimo,
                                valorJurosCalculado, // Soma dos juros calculados em tela
                                valorMultaCalculado, // Soma das multas calculadas em tela
                                valores.totalCorrecaoMonetaria, // Soma da correção monetária
                                isPix ? null : transacao.getId(), // Para PIX, não há transação de cartão
                                dto.getCodigoAutorizacao(),
                                isPix ? "PIX" : dto.getAdquirente(),
                                dto.getNsu(),
                                dto.getIdTransacao(), // ID do pedido do portal para guidMerchantOrderId
                                meioPagamento,
                                origemConta,
                                responsavel,
                                isPix ? null : bandeiraCartao, // Para PIX, não há bandeira de cartão
                                LocalDateTime.now(), // Data da autorização (momento atual)
                                contaMovimentacao, // Conta de movimentação definida acima
                                isPix, // Flag para diferenciar PIX de CARTÃO
                                dto.getPixCopiaECola(), // Código PIX copia e cola
                                dto.getDataGeracaoPix(), // Data de geração do PIX
                                operadorPadrao // Operador padrão do sistema
                );

                // Configurar campos adicionais conforme especificação
                configurarCamposAdicionaisContaNova(contaNova, contasOriginais, mapaDtos, bandeiraCartao, numeroParcela,
                                dto,
                                isPix);

                return contaNova;
        }

        private Integer calcularNumeroParcela(Long idContrato, Integer idOrigemConta) {
                Integer ultimoNroParcela = contaFinanceiraRepository.obterUltimoNroParcelaContratoOrigem(idContrato,
                                idOrigemConta);
                if (ultimoNroParcela == null) {
                        ultimoNroParcela = 0;
                }
                return ultimoNroParcela + 1;
        }

        private void configurarCamposAdicionaisContaNova(
                        ContaFinanceira contaNova,
                        List<ContaFinanceira> contasOriginais,
                        Map<Long, ContaFinanceiraParaPagamentoDto> mapaDtos,
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

                // Taxa do cartão (da tabela bandeiracartao) - apenas para CARTAO; para PIX não
                // há desconto de taxa
                BigDecimal taxaCartao = BigDecimal.ZERO;
                BigDecimal descontoTaxaCartao = BigDecimal.ZERO;
                Long idBandeirasAceitas = null;

                if (isPix) {
                        // Nova conta PIX: desconto da taxa de cartão não se aplica (mantém zero)
                        descontoTaxaCartao = BigDecimal.ZERO;
                } else if (bandeiraCartao != null) {
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

                // PRIORIDADE: Juros e multa vindos dos DTOs vinculados
                BigDecimal valorJuros = contasOriginais.stream()
                                .map(co -> {
                                        ContaFinanceiraParaPagamentoDto cd = mapaDtos.get(co.getId());
                                        return (cd != null && cd.getValorJuros() != null) ? cd.getValorJuros()
                                                        : co.calcularJuros();
                                })
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal valorMulta = contasOriginais.stream()
                                .map(co -> {
                                        ContaFinanceiraParaPagamentoDto cd = mapaDtos.get(co.getId());
                                        return (cd != null && cd.getValorMulta() != null) ? cd.getValorMulta()
                                                        : co.calcularMulta();
                                })
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                log.info("DEBUG - Configurando campos adicionais - Juros UTILIZADOS: {}, Multa UTILIZADA: {}",
                                valorJuros,
                                valorMulta);

                // Logar valores individuais das contas para debug
                contasOriginais
                                .forEach(conta -> log.info(
                                                "DEBUG - Conta ID: {}, Status: {}, Juros Calculado: {}, Multa Calculada: {}",
                                                conta.getId(), conta.calcularStatus(), conta.calcularJuros(),
                                                conta.calcularMulta()));

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
                                idBandeirasAceitas);

                log.info("DEBUG - Após configurar - Conta nova Juros: {}, Multa: {}",
                                contaNova.getValorJuros(), contaNova.getValorMulta());
        }

        private ValoresTotais calcularValoresTotais(List<ContaFinanceira> contasOriginais) {
                BigDecimal totalAcrescimo = BigDecimal.ZERO;
                BigDecimal totalCorrecaoMonetaria = BigDecimal.ZERO;

                // Somar todos os valores das contas originais
                for (ContaFinanceira conta : contasOriginais) {
                        if (conta.getValorAcrescimo() != null) {
                                totalAcrescimo = totalAcrescimo.add(conta.getValorAcrescimo());
                        }

                        if (conta.getValorAcrescimoAcumuladoCorrecaoMonetaria() != null) {
                                totalCorrecaoMonetaria = totalCorrecaoMonetaria
                                                .add(conta.getValorAcrescimoAcumuladoCorrecaoMonetaria());
                        }
                }

                log.info("Valores calculados - Acréscimo: {}, Correção Monetária: {}",
                                totalAcrescimo, totalCorrecaoMonetaria);

                return new ValoresTotais(totalAcrescimo, totalCorrecaoMonetaria);
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
                        dadosConta.put("ValorAcrescimoAcumuladoCorrecaoMonetaria",
                                        conta.getValorAcrescimoAcumuladoCorrecaoMonetaria());
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

                // Usar origem da primeira conta da lista (assumindo que vem ordenada por data
                // de cadastro)
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

        /**
         * Cria automaticamente um registro BandeiraCartao quando não encontrado
         * Isso garante que o fluxo de pagamento aprovado sempre seja concluído
         */
        private BandeiraCartao criarBandeiraCartaoPadrao(
                        Empresa empresa,
                        Integer idBandeira,
                        String gatewaySysId,
                        Long idContaMovimentacaoBancaria) {

                try {
                        // Extrair nome do gateway (GETNET ou REDE)
                        String nomeGateway = extrairNomeGateway(gatewaySysId);

                        // Buscar BandeirasAceitas para pegar a descrição da bandeira
                        BandeirasAceitas bandeirasAceitas = bandeirasAceitasRepository.findById(idBandeira.longValue())
                                        .orElseThrow(() -> new PagamentoCartaoException(
                                                        "BandeirasAceitas não encontrada para ID: " + idBandeira));

                        // Criar nome no padrão dos filtros para ser encontrado na próxima vez
                        String nomeEstabelecimento = String.format("CREDITO A VISTA - %s", nomeGateway);

                        // Usar método factory da entidade BandeiraCartao (padrão DDD)
                        BandeiraCartao novaBandeira = BandeiraCartao.criarConfiguracaoPadrao(
                                        empresa,
                                        nomeEstabelecimento,
                                        bandeirasAceitas.getBandeira(),
                                        idBandeira,
                                        idContaMovimentacaoBancaria != null ? idContaMovimentacaoBancaria.intValue()
                                                        : null);

                        // Salvar usando repository - o @Convert criptografará automaticamente o
                        // nomeEstabelecimento
                        BandeiraCartao bandeiraCartaoSalva = bandeiraCartaoRepository.save(novaBandeira);

                        log.info("BandeiraCartao criada automaticamente: ID {}, NomeEstabelecimento: '{}', Bandeira: {}, Taxa: {}%",
                                        bandeiraCartaoSalva.getIdBandeiraCartao(),
                                        nomeEstabelecimento,
                                        bandeiraCartaoSalva.getBandeira(),
                                        bandeiraCartaoSalva.getTaxaOperacao());

                        return bandeiraCartaoSalva;

                } catch (PagamentoCartaoException e) {
                        // Re-lançar exceptions específicas
                        throw e;
                } catch (Exception e) {
                        log.error("Erro ao criar BandeiraCartao padrão", e);
                        throw new PagamentoCartaoException(
                                        "Não foi possível criar configuração de bandeira automaticamente: "
                                                        + e.getMessage(),
                                        e);
                }
        }

        /**
         * Inicializa os relacionamentos lazy necessários para o cálculo de juros e
         * multa.
         * Como findAllById não faz fetch automático dos relacionamentos lazy,
         * precisamos fazer fetch manual usando JPQL com JOIN FETCH e substituir as
         * contas na lista.
         * 
         * @param contasFinanceiras Lista de contas financeiras a serem inicializadas
         */
        private void inicializarRelacionamentosParaCalculo(List<ContaFinanceira> contasFinanceiras) {
                if (contasFinanceiras.isEmpty()) {
                        return;
                }

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
                                WHERE cf.id IN :ids
                                """;

                List<ContaFinanceira> contasComRelacionamentos = entityManager.createQuery(jpql, ContaFinanceira.class)
                                .setParameter("ids", idsContas)
                                .getResultList();

                // Criar um mapa para acesso rápido
                java.util.Map<Long, ContaFinanceira> mapaContas = contasComRelacionamentos.stream()
                                .collect(Collectors.toMap(ContaFinanceira::getId, conta -> conta, (a, b) -> a));

                // Substituir as contas na lista original pelas contas com relacionamentos
                // carregados
                for (int i = 0; i < contasFinanceiras.size(); i++) {
                        ContaFinanceira contaOriginal = contasFinanceiras.get(i);
                        ContaFinanceira contaComRelacionamentos = mapaContas.get(contaOriginal.getId());
                        if (contaComRelacionamentos != null) {
                                contasFinanceiras.set(i, contaComRelacionamentos);

                                // Log para debug
                                log.debug("Conta ID {} - CarteiraBoleto: {}, Empresa: {}, Status: {}, Juros: {}, Multa: {}",
                                                contaComRelacionamentos.getId(),
                                                contaComRelacionamentos.getCarteiraBoleto() != null ? "carregada"
                                                                : "null",
                                                contaComRelacionamentos.getEmpresa() != null ? "carregada" : "null",
                                                contaComRelacionamentos.calcularStatus(),
                                                contaComRelacionamentos.calcularJuros(),
                                                contaComRelacionamentos.calcularMulta());
                        } else {
                                log.warn("Conta ID {} não foi encontrada no fetch com relacionamentos",
                                                contaOriginal.getId());
                        }
                }
        }

        // ==================== CLASSES INTERNAS ====================

        private static class ValoresTotais {
                final BigDecimal totalAcrescimo;
                final BigDecimal totalCorrecaoMonetaria;

                ValoresTotais(BigDecimal totalAcrescimo, BigDecimal totalCorrecaoMonetaria) {
                        this.totalAcrescimo = totalAcrescimo;
                        this.totalCorrecaoMonetaria = totalCorrecaoMonetaria;
                }
        }
}
