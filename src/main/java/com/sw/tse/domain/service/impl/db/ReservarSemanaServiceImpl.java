package com.sw.tse.domain.service.impl.db;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.tse.api.dto.EnderecoResponse;
import com.sw.tse.api.dto.HospedeDto;
import com.sw.tse.api.dto.HospedeResponse;
import com.sw.tse.api.dto.ReservaResumoResponse;
import com.sw.tse.api.dto.ReservaSemanaResponse;
import com.sw.tse.api.dto.ReservarSemanaRequest;
import com.sw.tse.api.dto.TelefoneResponse;
import com.sw.tse.core.config.UtilizacaoContratoPropertiesCustom;
import com.sw.tse.core.util.StringUtil;
import com.sw.tse.domain.expection.ApiTseException;
import com.sw.tse.domain.expection.BrasilApiException;
import com.sw.tse.domain.expection.CapacidadeUhExcedidaException;
import com.sw.tse.domain.expection.CepInvalidoException;
import com.sw.tse.domain.expection.CepObrigatorioException;
import com.sw.tse.domain.expection.ContratoNotFoundException;
import com.sw.tse.domain.expection.ContratoNaoPertenceAoClienteException;
import com.sw.tse.domain.expection.ContratoSemIntercambioException;
import com.sw.tse.domain.expection.HospedeNotFoundException;
import com.sw.tse.domain.expection.HospedesObrigatoriosException;
import com.sw.tse.domain.expection.HospedePrincipalInvalidoException;
import com.sw.tse.domain.expection.OperadorSistemaNaoEncontradoException;
import com.sw.tse.domain.expection.PeriodoNaoPermitePoolException;
import com.sw.tse.domain.expection.PeriodoNaoPermiteRciException;
import com.sw.tse.domain.expection.PeriodoUtilizacaoNotFoundException;
import com.sw.tse.domain.expection.PessoaNotFoundException;
import com.sw.tse.domain.expection.TipoHospedeInvalidoException;
import com.sw.tse.domain.expection.TipoHospedeNotFoundException;
import com.sw.tse.domain.expection.TipoUtilizacaoContratoInvalidoException;
import com.sw.tse.domain.expection.TipoUtilizacaoNaoEncontradoException;
import com.sw.tse.domain.expection.ReservaCanceladaNaoEditavelException;
import com.sw.tse.domain.expection.ReservaCheckinPassadoException;
import com.sw.tse.domain.expection.UtilizacaoContratoNaoEditavelException;
import com.sw.tse.domain.expection.UtilizacaoContratoNotFoundException;
import com.sw.tse.domain.model.db.ContatoTelefonico;
import com.sw.tse.domain.model.db.Contrato;
import com.sw.tse.domain.model.db.ContratoIntercambio;
import com.sw.tse.domain.model.db.EnderecoPessoa;
import com.sw.tse.domain.model.db.FaixaEtaria;
import com.sw.tse.domain.model.db.OperadorSistema;
import com.sw.tse.domain.model.db.PeriodoModeloCota;
import com.sw.tse.domain.model.db.PeriodoUtilizacao;
import com.sw.tse.domain.model.db.Pessoa;
import com.sw.tse.domain.model.db.TipoHospede;
import com.sw.tse.domain.model.db.TipoUtilizacaoContrato;
import com.sw.tse.domain.model.db.UnidadeHoteleira;
import com.sw.tse.domain.model.db.UtilizacaoContrato;
import com.sw.tse.domain.model.db.UtilizacaoContratoHospede;
import com.sw.tse.domain.model.dto.BrasilApiErrorResponse;
import com.sw.tse.domain.model.dto.PeriodoUtilizacaoDisponivel;
import com.sw.tse.domain.repository.ContratoIntercambioRepository;
import com.sw.tse.domain.repository.ContratoRepository;
import com.sw.tse.domain.repository.OperadorSistemaRepository;
import com.sw.tse.domain.repository.PeriodoModeloCotaRepository;
import com.sw.tse.domain.repository.PeriodoUtilizacaoRepository;
import com.sw.tse.domain.repository.PessoaRepository;
import com.sw.tse.domain.repository.TipoHospedeRepository;
import com.sw.tse.domain.repository.TipoUtilizacaoContratoRepository;
import com.sw.tse.domain.repository.UtilizacaoContratoHospedeRepository;
import com.sw.tse.domain.repository.UtilizacaoContratoRepository;
import com.sw.tse.domain.service.interfaces.CidadeService;
import com.sw.tse.domain.service.interfaces.ContratoDisponibilidadeService;
import com.sw.tse.domain.service.interfaces.FaixaEtariaService;
import com.sw.tse.domain.service.interfaces.PeriodoDisponibilidadeService;
import com.sw.tse.domain.service.interfaces.PeriodoUtilizacaoService;
import com.sw.tse.domain.service.interfaces.PessoaService;
import com.sw.tse.domain.service.interfaces.ReservarSemanaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservarSemanaServiceImpl implements ReservarSemanaService {
    
    private final ContratoRepository contratoRepository;
    private final ContratoDisponibilidadeService contratoDisponibilidadeService;
    private final PeriodoDisponibilidadeService periodoDisponibilidadeService;
    private final PeriodoUtilizacaoRepository periodoUtilizacaoRepository;
    private final PeriodoUtilizacaoService periodoUtilizacaoService;
    private final OperadorSistemaRepository operadorSistemaRepository;
    private final TipoUtilizacaoContratoRepository tipoUtilizacaoContratoRepository;
    private final PeriodoModeloCotaRepository periodoModeloCotaRepository;
    private final UtilizacaoContratoRepository utilizacaoContratoRepository;
    private final UtilizacaoContratoHospedeRepository utilizacaoContratoHospedeRepository;
    private final PessoaRepository pessoaRepository;
    private final PessoaService pessoaService;
    private final TipoHospedeRepository tipoHospedeRepository;
    private final FaixaEtariaService faixaEtariaService;
    private final ContratoIntercambioRepository contratoIntercambioRepository;
    private final CidadeService cidadeService;
    private final UtilizacaoContratoPropertiesCustom utilizacaoContratoConfig;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${sw.tse.rci.id-pessoa-hospede-padrao}")
    private Long idPessoaHospedePadraoRCI;
    
    @Value("${sw.tse.rci.id-tipo-hospede-padrao}")
    private Long idTipoHospedePadraoRCI;
    
    @Value("${sw.tse.hospede.id-tipo-hospede-proprietario}")
    private Long idTipoHospedeProprietario;
    
    @Value("${sw.tse.hospede.id-tipo-hospede-convidado}")
    private Long idTipoHospedeConvidado;
    
    @Override
    public void validarReserva(Long idContrato, Long idPeriodoUtilizacao, Long idPessoaCliente) {
        
        log.info("Iniciando validação de reserva - Contrato: {}, Período: {}, Cliente: {}", 
            idContrato, idPeriodoUtilizacao, idPessoaCliente);
        
        // 1. Validar se contrato pertence ao cliente
        log.debug("Validando se contrato {} pertence ao cliente {}", idContrato, idPessoaCliente);
        boolean contratoPerteceAoCliente = contratoRepository.contratoPerteceAoCliente(
            idContrato, 
            idPessoaCliente
        );
        
        if (!contratoPerteceAoCliente) {
            log.warn("Tentativa de reserva negada: Contrato {} não pertence ao cliente {}", 
                idContrato, idPessoaCliente);
            throw new ContratoNaoPertenceAoClienteException(
                String.format("O contrato %d não pertence ao cliente autenticado", idContrato)
            );
        }
        
        log.info("Contrato {} validado como pertencente ao cliente {}", idContrato, idPessoaCliente);
        
        // 2. Validar disponibilidade do contrato (inadimplência, integralização, tags)
        log.debug("Validando disponibilidade do contrato {}", idContrato);
        contratoDisponibilidadeService.validarDisponibilidadeParaReserva(idContrato);
        
        // 3. Validar disponibilidade do período específico (dupla checagem)
        log.debug("Validando disponibilidade do período {}", idPeriodoUtilizacao);
        periodoDisponibilidadeService.validarPeriodoDisponivel(idContrato, idPeriodoUtilizacao);
        
        log.info("Todas as validações passaram - Reserva pode ser criada");
    }
    
    @Override
    @Transactional
    public ReservaSemanaResponse criarReserva(ReservarSemanaRequest request, Long idPessoaCliente) {
        
        log.info("Iniciando criação de reserva - Contrato: {}, Período: {}, Tipo: {}", 
            request.getIdContrato(), request.getIdPeriodoUtilizacao(), request.getTipoUtilizacao());
        
        // Verificar se é edição ou criação
        if (request.getIdUtilizacaoContrato() != null && request.getIdUtilizacaoContrato() > 0) {
            log.info("Modo edição detectado - Utilização: {}", request.getIdUtilizacaoContrato());
            return editarReserva(request, idPessoaCliente);
        }
        
        // 1. Validações
        validarReserva(request.getIdContrato(), request.getIdPeriodoUtilizacao(), idPessoaCliente);
        
        // 2. Validar regras específicas por tipo
        validarRegrasEspecificasTipo(request);
        
        // 3. Buscar entidades necessárias
        Contrato contrato = contratoRepository.findById(request.getIdContrato())
            .orElseThrow(() -> new ContratoNotFoundException(request.getIdContrato()));
        
        PeriodoUtilizacao periodoUtilizacao = periodoUtilizacaoRepository.findById(request.getIdPeriodoUtilizacao())
            .orElseThrow(() -> new PeriodoUtilizacaoNotFoundException(request.getIdPeriodoUtilizacao()));
        
        OperadorSistema operadorSistema = operadorSistemaRepository.findByPessoa_IdPessoa(idPessoaCliente)
            .orElseThrow(() -> new OperadorSistemaNaoEncontradoException(idPessoaCliente));
        
        String siglaBanco = mapearTipoUtilizacao(request.getTipoUtilizacao());
        TipoUtilizacaoContrato tipoUtilizacao = tipoUtilizacaoContratoRepository.findBySigla(siglaBanco)
            .orElseThrow(() -> new TipoUtilizacaoNaoEncontradoException(siglaBanco));
        
        // 4. Criar PeriodoModeloCota
        log.info("Criando PeriodoModeloCota para contrato {} e período {}", 
            contrato.getId(), periodoUtilizacao.getId());
        
        PeriodoModeloCota periodoModeloCota = PeriodoModeloCota.novoPeriodoModeloCota(
            contrato, 
            periodoUtilizacao, 
            operadorSistema
        );
        periodoModeloCota = periodoModeloCotaRepository.save(periodoModeloCota);
        
        log.info("PeriodoModeloCota criado com ID: {}", periodoModeloCota.getId());
        
        // 5. Criar UtilizacaoContrato usando o método factory adequado
        log.info("Criando UtilizacaoContrato do tipo {}", siglaBanco);
        
        UtilizacaoContrato utilizacaoContrato;
        if ("RESERVA".equals(request.getTipoUtilizacao())) {
            utilizacaoContrato = UtilizacaoContrato.criarUtilizacaoContratoReserva(
                periodoModeloCota, 
                operadorSistema, 
                tipoUtilizacao
            );
        } else if ("RCI".equals(request.getTipoUtilizacao())) {
            utilizacaoContrato = UtilizacaoContrato.criarUtilizacaoContratoRci(
                periodoModeloCota, 
                operadorSistema, 
                tipoUtilizacao
            );
        } else if ("POOL".equals(request.getTipoUtilizacao())) {
            utilizacaoContrato = UtilizacaoContrato.criarUtilizacaoContratoPool(
                periodoModeloCota, 
                operadorSistema, 
                tipoUtilizacao
            );
        } else {
            throw new TipoUtilizacaoContratoInvalidoException(request.getTipoUtilizacao());
        }
        
        // 6. Processar dados específicos por tipo
        if ("RESERVA".equals(request.getTipoUtilizacao())) {
            processarReserva(utilizacaoContrato, request.getHospedes(), operadorSistema, contrato);
        } else if ("RCI".equals(request.getTipoUtilizacao())) {
            processarRCI(utilizacaoContrato, contrato, operadorSistema);
        } else if ("POOL".equals(request.getTipoUtilizacao())) {
            processarPool(utilizacaoContrato);
        }
        
        utilizacaoContrato = utilizacaoContratoRepository.save(utilizacaoContrato);
        
        log.info("UtilizacaoContrato criada com ID: {}", utilizacaoContrato.getId());
        
        // 7. Retornar response
        return mapearParaResponse(utilizacaoContrato, contrato, periodoUtilizacao);
    }
    
    private void validarRegrasEspecificasTipo(ReservarSemanaRequest request) {
        if ("RESERVA".equals(request.getTipoUtilizacao())) {
            if (request.getHospedes() == null || request.getHospedes().isEmpty()) {
                log.warn("Tentativa de criar reserva sem hóspedes");
                throw new HospedesObrigatoriosException();
            }
            
            // Validar que existe exatamente 1 hóspede principal
            validarHospedePrincipal(request.getHospedes());
        } else if ("RCI".equals(request.getTipoUtilizacao())) {
            // Validar que contrato possui intercâmbio ativo e período permite RCI
            validarContratoIntercambioAtivo(request.getIdContrato(), request.getIdPeriodoUtilizacao());
        } else if ("POOL".equals(request.getTipoUtilizacao())) {
            // Validar que período permite POOL
            validarPeriodoPermitePool(request.getIdContrato(), request.getIdPeriodoUtilizacao());
        }
    }
    
    private void validarHospedePrincipal(List<HospedeDto> hospedes) {
        long quantidadePrincipais = hospedes.stream()
            .filter(h -> "S".equalsIgnoreCase(h.principal()))
            .count();
        
        if (quantidadePrincipais == 0) {
            log.warn("Tentativa de criar reserva sem hóspede principal");
            throw new HospedePrincipalInvalidoException(
                "É obrigatório informar exatamente 1 hóspede principal. Nenhum hóspede foi marcado como principal.");
        }
        
        if (quantidadePrincipais > 1) {
            log.warn("Tentativa de criar reserva com {} hóspedes principais", quantidadePrincipais);
            throw new HospedePrincipalInvalidoException(
                String.format("É obrigatório informar exatamente 1 hóspede principal. Foram informados %d hóspedes principais.", quantidadePrincipais));
        }
        
        log.debug("Validação de hóspede principal OK - 1 principal identificado");
    }
    
    private void validarContratoIntercambioAtivo(Long idContrato, Long idPeriodoUtilizacao) {
        log.debug("Validando se contrato {} possui intercâmbio ativo e período {} permite RCI", idContrato, idPeriodoUtilizacao);
        
        Contrato contrato = contratoRepository.findById(idContrato)
            .orElseThrow(() -> new ContratoNotFoundException(idContrato));
        
        // Validar que contrato possui intercâmbio ativo
        ContratoIntercambio contratoIntercambio = contratoIntercambioRepository
            .findByContratoIdAndTipoHistoricoAtivo(idContrato)
            .orElseThrow(() -> new ContratoSemIntercambioException(
                contrato.getId(), 
                contrato.getNumeroContrato()
            ));
        
        log.info("Contrato {} possui intercâmbio ativo (ID: {})", idContrato, contratoIntercambio.getId());
        
        // Validar que o período permite RCI
        PeriodoUtilizacao periodoUtilizacao = periodoUtilizacaoRepository.findById(idPeriodoUtilizacao)
            .orElseThrow(() -> new PeriodoUtilizacaoNotFoundException(idPeriodoUtilizacao));
        
        // Buscar períodos disponíveis para verificar se o período permite RCI
        List<PeriodoUtilizacaoDisponivel> periodosDisponiveis = periodoUtilizacaoService
            .buscarPeriodosDisponiveisParaReserva(idContrato, periodoUtilizacao.getAnoInicio());
        
        PeriodoUtilizacaoDisponivel periodoDisponivel = periodosDisponiveis.stream()
            .filter(p -> p.getIdPeriodoUtilizacao().equals(idPeriodoUtilizacao))
            .findFirst()
            .orElseThrow(() -> new PeriodoUtilizacaoNotFoundException(idPeriodoUtilizacao));
        
        // Debug: Log detalhado dos valores
        log.debug("DEBUG RCI - Período encontrado: ID={}, Descrição={}", 
            periodoDisponivel.getIdPeriodoUtilizacao(), periodoDisponivel.getDescricaoPeriodo());
        log.debug("DEBUG RCI - Campo rci do período: {}", periodoDisponivel.getRci());
        log.debug("DEBUG RCI - Intercâmbio ativo encontrado: ID {}", contratoIntercambio.getId());
        log.debug("DEBUG RCI - Intercambiadora: ID {}", contratoIntercambio.getIdIntercambiadora());
        
        // Verificar se o período permite RCI (rci = 1)
        if (periodoDisponivel.getRci() == null || periodoDisponivel.getRci() != 1) {
            log.warn("Período {} não permite RCI - flag rci: {}", idPeriodoUtilizacao, periodoDisponivel.getRci());
            throw new PeriodoNaoPermiteRciException(idPeriodoUtilizacao, periodoUtilizacao.getDescricaoPeriodo());
        }
        
        log.info("Período {} validado com sucesso para RCI", idPeriodoUtilizacao);
    }
    
    private void validarPeriodoPermitePool(Long idContrato, Long idPeriodoUtilizacao) {
        log.debug("Validando se período {} permite POOL para contrato {}", idPeriodoUtilizacao, idContrato);
        
        PeriodoUtilizacao periodoUtilizacao = periodoUtilizacaoRepository.findById(idPeriodoUtilizacao)
            .orElseThrow(() -> new PeriodoUtilizacaoNotFoundException(idPeriodoUtilizacao));
        
        // Buscar períodos disponíveis para verificar se o período permite POOL
        List<PeriodoUtilizacaoDisponivel> periodosDisponiveis = periodoUtilizacaoService
            .buscarPeriodosDisponiveisParaReserva(idContrato, periodoUtilizacao.getAnoInicio());
        
        PeriodoUtilizacaoDisponivel periodoDisponivel = periodosDisponiveis.stream()
            .filter(p -> p.getIdPeriodoUtilizacao().equals(idPeriodoUtilizacao))
            .findFirst()
            .orElseThrow(() -> new PeriodoUtilizacaoNotFoundException(idPeriodoUtilizacao));
        
        // Debug: Log detalhado dos valores
        log.debug("DEBUG POOL - Período encontrado: ID={}, Descrição={}", 
            periodoDisponivel.getIdPeriodoUtilizacao(), periodoDisponivel.getDescricaoPeriodo());
        log.debug("DEBUG POOL - Campo pool do período: {}", periodoDisponivel.getPool());
        
        // Verificar se o período permite POOL (pool = 1)
        if (periodoDisponivel.getPool() == null || periodoDisponivel.getPool() != 1) {
            log.warn("Período {} não permite POOL - flag pool: {}", idPeriodoUtilizacao, periodoDisponivel.getPool());
            throw new PeriodoNaoPermitePoolException(idPeriodoUtilizacao, periodoUtilizacao.getDescricaoPeriodo());
        }
        
        log.info("Período {} validado com sucesso para POOL", idPeriodoUtilizacao);
    }
    
    private void processarReserva(UtilizacaoContrato utilizacaoContrato, 
            List<HospedeDto> hospedesDto, OperadorSistema operador, Contrato contrato) {
        
        log.info("Processando {} hóspedes para a reserva", hospedesDto.size());
        
        // VALIDAR CAPACIDADE DA UH
        validarCapacidadeUh(utilizacaoContrato, hospedesDto.size());
        
        // VALIDAR CEP DE TODOS OS HÓSPEDES ANTES DE PROSSEGUIR
        log.debug("Iniciando validação de CEP para {} hóspedes", hospedesDto.size());
        for (HospedeDto dto : hospedesDto) {
            validarCepHospede(dto);
        }
        log.debug("Validação de CEP concluída para todos os hóspedes");
        
        for (int i = 0; i < hospedesDto.size(); i++) {
            HospedeDto dto = hospedesDto.get(i);
            
            // Salvar/atualizar pessoa usando service existente
            // PessoaService já busca por CPF, atualiza se existe, cria se não existe
            Long idPessoa = pessoaService.salvar(dto, contrato);
            
            // Buscar pessoa salva
            Pessoa pessoa = pessoaRepository.findById(idPessoa)
                .orElseThrow(() -> new PessoaNotFoundException(idPessoa));
            
            // Determinar TipoHospede automaticamente
            Long idTipoHospede = determinarTipoHospede(dto, pessoa, contrato);
            TipoHospede tipoHospede = tipoHospedeRepository.findById(idTipoHospede)
                .orElseThrow(() -> new TipoHospedeNotFoundException(idTipoHospede));
            
            // Calcular faixa etária
            FaixaEtaria faixaEtaria = faixaEtariaService.calcularFaixaEtariaPorDataNascimento(pessoa.getDataNascimento());
            
            // Separar nome completo em nome e sobrenome
            String nomeCompleto = pessoa.getNome();
            String[] partesNome = nomeCompleto != null ? nomeCompleto.split(" ", 2) : new String[]{"", ""};
            String nome = partesNome[0];
            String sobrenome = partesNome.length > 1 ? partesNome[1] : "";
            
            // Criar hóspede
            UtilizacaoContratoHospede hospede = UtilizacaoContratoHospede.novoHospede(
                utilizacaoContrato,
                nome,
                sobrenome,
                pessoa.getCpfCnpj(),
                pessoa.getSexo() != null ? pessoa.getSexo().getCodigo() : null,
                pessoa.getDataNascimento(),
                faixaEtaria,
                tipoHospede,
                "S".equalsIgnoreCase(dto.principal()),
                operador
            );
            
            hospede.setPessoa(pessoa);
            utilizacaoContrato.adicionarHospede(hospede);
            
            log.debug("Hóspede {} adicionado: {}", i + 1, pessoa.getNome());
        }
        
        // Atualizar quantitativos
        long qtdAdultos = utilizacaoContrato.getHospedes().stream()
            .filter(h -> "ADT".equals(h.getFaixaEtariaSigla()))
            .count();
        long qtdCriancas = utilizacaoContrato.getHospedes().size() - qtdAdultos;
        
        utilizacaoContrato.setQuantitativosHospedes((int) qtdAdultos, (int) qtdCriancas);
        
        // Calcular quantidade de pagantes
        int qtdPagantes = calcularQtdPagantes(utilizacaoContrato.getHospedes());
        utilizacaoContrato.definirQtdPagantes(qtdPagantes);
        
        // Configurar pensão padrão
        utilizacaoContrato.definirIdUtilizacaoContratoTsTipoPensao(utilizacaoContratoConfig.getIdPensaoPadrao());
        
        log.info("Reserva processada com {} adultos, {} crianças e {} pagantes", qtdAdultos, qtdCriancas, qtdPagantes);
    }
    
    private int calcularQtdPagantes(List<UtilizacaoContratoHospede> hospedes) {
        return (int) hospedes.stream()
            .filter(h -> {
                if (h.getFaixaEtaria() == null) return false;
                
                // Se faixa etária marca como pagante, incluir
                if (Boolean.TRUE.equals(h.getFaixaEtaria().getIsPagante())) {
                    return true;
                }
                
                // Ou se tem idade >= idade mínima configurada, incluir
                if (h.getDataNascimento() != null) {
                    LocalDate dataAtual = LocalDate.now();
                    long anos = ChronoUnit.YEARS.between(h.getDataNascimento(), dataAtual);
                    return anos >= utilizacaoContratoConfig.getIdadeMinimaPagante();
                }
                
                return false;
            })
            .count();
    }
    
    private void processarRCI(UtilizacaoContrato utilizacaoContrato, Contrato contrato, OperadorSistema operador) {
        
        log.info("Processando RCI - Buscando ContratoIntercambio ativo do contrato {}", contrato.getId());
        
        // 1. Buscar e vincular ContratoIntercambio
        ContratoIntercambio contratoIntercambio = contratoIntercambioRepository
            .findByContratoIdAndTipoHistoricoAtivo(contrato.getId())
            .orElseThrow(() -> new ContratoSemIntercambioException(
                contrato.getId(), 
                contrato.getNumeroContrato()
            ));
        
        utilizacaoContrato.setContratoIntercambio(contratoIntercambio);
        log.info("ContratoIntercambio ID {} vinculado à utilização", contratoIntercambio.getId());
        
        // 2. Criar hóspede principal usando pessoa padrão RCI
        Pessoa pessoaRCI = pessoaRepository.findById(idPessoaHospedePadraoRCI)
            .orElseThrow(() -> new PessoaNotFoundException(idPessoaHospedePadraoRCI));
        
        TipoHospede tipoHospedePadrao = tipoHospedeRepository.findById(idTipoHospedePadraoRCI)
            .orElseThrow(() -> new TipoHospedeNotFoundException(idTipoHospedePadraoRCI));
        
        FaixaEtaria faixaEtaria = faixaEtariaService.calcularFaixaEtariaPorDataNascimento(pessoaRCI.getDataNascimento());
        
        UtilizacaoContratoHospede hospedeRCI = UtilizacaoContratoHospede.novoHospede(
            utilizacaoContrato,
            pessoaRCI.getNome(),
            "",
            pessoaRCI.getCpfCnpj(),
            pessoaRCI.getSexo() != null ? pessoaRCI.getSexo().getCodigo() : null,
            pessoaRCI.getDataNascimento(),
            faixaEtaria,
            tipoHospedePadrao,
            true,  // Principal
            operador
        );
        
        hospedeRCI.setPessoa(pessoaRCI);
        utilizacaoContrato.adicionarHospede(hospedeRCI);
        utilizacaoContrato.setQuantitativosHospedes(1, 0);  // 1 adulto padrão
        
        log.info("Hóspede padrão RCI adicionado (Pessoa ID: {}, TipoHospede ID: {})", 
            idPessoaHospedePadraoRCI, idTipoHospedePadraoRCI);
    }
    
    private void processarPool(UtilizacaoContrato utilizacaoContrato) {
        log.info("Processando POOL - sem hóspedes (disponibilizado para o hotel comercializar)");
        
        // POOL não tem hóspedes - a semana é disponibilizada para o hotel comercializar
        // Não adicionar hóspedes à utilização
        
        // Definir quantitativos como 0
        utilizacaoContrato.setQuantitativosHospedes(0, 0);
        utilizacaoContrato.definirQtdPagantes(0);
        
        // Configurar pensão padrão
        utilizacaoContrato.definirIdUtilizacaoContratoTsTipoPensao(utilizacaoContratoConfig.getIdPensaoPadrao());
        
        log.info("POOL processado - sem hóspedes, quantitativos zerados");
    }
    
    private void validarCepHospede(HospedeDto dto) {
        boolean isPrincipal = "S".equalsIgnoreCase(dto.principal());
        log.debug("Validando CEP do hóspede - Principal: {}, Logradouro: {}, CEP: {}", 
            isPrincipal, 
            StringUtils.hasText(dto.logradouro()) ? "Informado" : "Não informado",
            StringUtils.hasText(dto.cep()) ? dto.cep() : "Não informado");
        
        // Se informou CEP, SEMPRE validar (independente de ter logradouro)
        if (StringUtils.hasText(dto.cep())) {
            String cep = StringUtil.removerMascaraCep(dto.cep());
            
            // Validar formato
            if (!cep.matches("\\d{8}")) {
                throw new CepInvalidoException("CEP deve conter apenas números e ter 8 dígitos");
            }
            
            // Validar com Brasil API
            try {
                cidadeService.buscarPorCep(cep);
                log.info("CEP {} validado com sucesso", cep);
            } catch (BrasilApiException | ApiTseException e) {
                log.error("CEP {} inválido: {}", cep, e.getMessage());
                
                // Tentar extrair mensagem específica do correios-alt
                String mensagemEspecifica = extrairMensagemCorreios(e.getMessage(), cep);
                throw new CepInvalidoException(mensagemEspecifica);
            }
        } else if (isPrincipal && StringUtils.hasText(dto.logradouro())) {
            // Principal COM logradouro mas SEM CEP
            throw new CepObrigatorioException("CEP é obrigatório para o hóspede principal");
        } else {
            log.debug("Hóspede sem CEP - validação pulada");
        }
    }
    
    private String extrairMensagemCorreios(String mensagemErro, String cep) {
        try {
            // A mensagem da ApiTseException pode conter o JSON completo
            // Exemplo: "Erro na API externa (HTTP 404): {\"message\":\"Todos...\",\"errors\":[...]}"
            
            // Extrair o JSON do erro
            int jsonStart = mensagemErro.indexOf("{");
            if (jsonStart != -1) {
                String jsonPart = mensagemErro.substring(jsonStart);
                
                // Parse do JSON
                BrasilApiErrorResponse errorResponse = objectMapper.readValue(jsonPart, BrasilApiErrorResponse.class);
                
                // Buscar erro do correios-alt
                if (errorResponse.getErrors() != null) {
                    for (BrasilApiErrorResponse.BrasilApiError error : errorResponse.getErrors()) {
                        if ("correios-alt".equals(error.getService())) {
                            log.debug("Mensagem específica extraída do correios-alt: {}", error.getMessage());
                            // Adicionar CEP na mensagem
                            String mensagem = error.getMessage();
                            if (mensagem.toLowerCase().startsWith("cep")) {
                                // Se a mensagem já começa com "CEP", substituir por "CEP {cep}"
                                return String.format("CEP %s %s", cep, mensagem.substring(4));
                            } else {
                                // Adicionar "CEP {cep}" no início
                                return String.format("CEP %s %s", cep, 
                                    mensagem.substring(0, 1).toLowerCase() + mensagem.substring(1));
                            }
                        }
                    }
                }
                
                // Se não encontrou correios-alt, usar a mensagem principal
                if (errorResponse.getMessage() != null) {
                    log.debug("Usando mensagem principal do erro: {}", errorResponse.getMessage());
                    return String.format("CEP %s: %s", cep, errorResponse.getMessage());
                }
            }
        } catch (Exception ex) {
            log.warn("Não foi possível extrair mensagem específica do erro: {}", ex.getMessage());
        }
        
        // Fallback para mensagem genérica
        return String.format("CEP %s não pôde ser validado. Verifique se o CEP está correto.", cep);
    }
    
    private Long determinarTipoHospede(HospedeDto dto, Pessoa pessoa, Contrato contrato) {
        // Se informou no DTO, usar o valor informado
        if (dto.tipoHospede() != null && !dto.tipoHospede().trim().isEmpty()) {
            try {
                return Long.parseLong(dto.tipoHospede());
            } catch (NumberFormatException e) {
                log.error("TipoHospede inválido no DTO: {}", dto.tipoHospede());
                throw new TipoHospedeInvalidoException(dto.tipoHospede());
            }
        }
        
        // Determinar automaticamente baseado na pessoa
        Long idPessoa = pessoa.getIdPessoa();
        
        // Verificar se pessoa é cessionário ou cocessionário do contrato
        boolean isProprietario = false;
        if (contrato.getPessoaCessionario() != null && idPessoa.equals(contrato.getPessoaCessionario().getIdPessoa())) {
            isProprietario = true;
            log.debug("Pessoa {} é cessionário do contrato - TipoHospede: PROPRIETÁRIO", idPessoa);
        } else if (contrato.getPessaoCocessionario() != null && idPessoa.equals(contrato.getPessaoCocessionario().getIdPessoa())) {
            isProprietario = true;
            log.debug("Pessoa {} é cocessionário do contrato - TipoHospede: PROPRIETÁRIO", idPessoa);
        }
        
        Long idTipoHospede = isProprietario ? idTipoHospedeProprietario : idTipoHospedeConvidado;
        log.info("TipoHospede determinado automaticamente: {} (Pessoa: {}, Proprietário: {})", 
            idTipoHospede, idPessoa, isProprietario);
        
        return idTipoHospede;
    }
    
    private String mapearTipoUtilizacao(String tipoFrontend) {
        return switch (tipoFrontend) {
            case "RESERVA" -> "RESERVA";
            case "RCI" -> "DEPSEMANA";
            case "POOL" -> "DEPPOOL";
            default -> throw new TipoUtilizacaoContratoInvalidoException(tipoFrontend);
        };
    }
    
    private ReservaSemanaResponse mapearParaResponse(UtilizacaoContrato utilizacaoContrato, 
            Contrato contrato, PeriodoUtilizacao periodoUtilizacao) {
        
        String siglaTipo = utilizacaoContrato.getTipoUtilizacaoContrato().getSigla();
        boolean isRci = "DEPSEMANA".equals(siglaTipo);
        boolean isPool = "DEPPOOL".equals(siglaTipo);
        
        // Traduzir DEPSEMANA para RCI e DEPPOOL para POOL no response
        String tipoUtilizacaoResponse;
        if (isRci) {
            tipoUtilizacaoResponse = "RCI";
        } else if (isPool) {
            tipoUtilizacaoResponse = "POOL";
        } else {
            tipoUtilizacaoResponse = siglaTipo;
        }
        
        // Para RCI e POOL, não retornar hóspedes (informação interna do sistema / POOL não tem hóspedes)
        List<HospedeResponse> hospedesResponse = null;
        if (!isRci && !isPool && utilizacaoContrato.getHospedes() != null && !utilizacaoContrato.getHospedes().isEmpty()) {
            hospedesResponse = utilizacaoContrato.getHospedes().stream()
                .map(this::mapearHospedeParaResponse)
                .collect(Collectors.toList());
        }
        
        return ReservaSemanaResponse.builder()
            .idUtilizacaoContrato(utilizacaoContrato.getId())
            .idPeriodoModeloCota(utilizacaoContrato.getPeriodoModeloCota().getId())
            .idContrato(contrato.getId())
            .numeroContrato(contrato.getNumeroContrato())
            .tipoUtilizacao(tipoUtilizacaoResponse)
            .tipoSemana(utilizacaoContrato.getTipoPeriodoUtilizacao() != null ? 
                utilizacaoContrato.getTipoPeriodoUtilizacao().getDescricao() : null)
            .checkin(utilizacaoContrato.getDataCheckin().toLocalDate())
            .checkout(utilizacaoContrato.getDataCheckout().toLocalDate())
            .descricaoPeriodo(periodoUtilizacao.getDescricaoPeriodo())
            .status(utilizacaoContrato.getStatus())
            .dataCriacao(utilizacaoContrato.getDataCadastro())
            .capacidade(utilizacaoContrato.getUnidadeHoteleira() != null ? 
                utilizacaoContrato.getUnidadeHoteleira().getCapacidade() : null)
            .hospedes(hospedesResponse)
            .build();
    }
    
    private HospedeResponse mapearHospedeParaResponse(UtilizacaoContratoHospede hospede) {
        // Converter sexo: 0 (Integer) -> "M" (String), 1 (Integer) -> "F" (String)
        String sexoConvertido = null;
        if (hospede.getSexo() != null) {
            sexoConvertido = hospede.getSexo() == 0 ? "M" : hospede.getSexo() == 1 ? "F" : null;
        }
        
        HospedeResponse.HospedeResponseBuilder builder = HospedeResponse.builder()
            .idHospede(hospede.getId())
            .nome(hospede.getNome())
            .sobrenome(hospede.getSobrenome())
            .cpf(hospede.getCpf())
            .dataNascimento(hospede.getDataNascimento())
            .sexo(sexoConvertido)
            .isPrincipal(hospede.getIsPrincipal())
            .faixaEtaria(hospede.getFaixaEtariaSigla());
        
        // Adicionar dados adicionais se for o hóspede principal e tiver Pessoa vinculada
        if (Boolean.TRUE.equals(hospede.getIsPrincipal()) && hospede.getPessoa() != null) {
            Pessoa pessoa = hospede.getPessoa();
            
            // Buscar endereço de correspondência
            if (pessoa.getEnderecos() != null && !pessoa.getEnderecos().isEmpty()) {
                EnderecoPessoa enderecoCorrespondencia = pessoa.getEnderecos().stream()
                    .filter(EnderecoPessoa::getParaCorrespondencia)
                    .findFirst()
                    .orElse(pessoa.getEnderecos().get(0));
                
                if (enderecoCorrespondencia != null) {
                    // Se UF estiver vazio no endereço, buscar da cidade relacionada
                    String uf = enderecoCorrespondencia.getUf();
                    if ((uf == null || uf.isEmpty()) && enderecoCorrespondencia.getCidade() != null) {
                        uf = enderecoCorrespondencia.getCidade().getUf();
                    }
                    
                    builder.endereco(EnderecoResponse.builder()
                        .logradouro(enderecoCorrespondencia.getLogradouro())
                        .numero(enderecoCorrespondencia.getNumero())
                        .complemento(enderecoCorrespondencia.getComplemento())
                        .bairro(enderecoCorrespondencia.getBairro())
                        .cep(enderecoCorrespondencia.getCep())
                        .cidade(enderecoCorrespondencia.getCidade() != null ? enderecoCorrespondencia.getCidade().getNome() : null)
                        .uf(uf)
                        .build());
                }
            }
            
            // Buscar primeiro email
            if (pessoa.getEmails() != null && !pessoa.getEmails().isEmpty()) {
                builder.email(pessoa.getEmails().get(0).getEmail());
            }
            
            // Verificar se é proprietário (para bloquear edição de dados sensíveis no frontend)
            boolean isProprietario = contratoRepository.pessoaEhProprietariaDeAlgumContrato(pessoa.getIdPessoa());
            builder.isProprietario(isProprietario);
            
            // Buscar primeiro telefone
            if (pessoa.getTelefones() != null && !pessoa.getTelefones().isEmpty()) {
                ContatoTelefonico telefone = pessoa.getTelefones().get(0);
                builder.telefone(TelefoneResponse.builder()
                    .ddi(telefone.getDdi())
                    .ddd(telefone.getDdd())
                    .numero(telefone.getNumero())
                    .build());
            }
        }
        
        return builder.build();
    }
    
    // ========== MÉTODOS PARA EDIÇÃO DE RESERVA ==========
    
    private ReservaSemanaResponse editarReserva(ReservarSemanaRequest request, Long idPessoaCliente) {
        log.info("Iniciando edição de reserva - Utilização: {}, Contrato: {}, Período: {}", 
            request.getIdUtilizacaoContrato(), request.getIdContrato(), request.getIdPeriodoUtilizacao());
        
        // 1. Buscar utilização existente
        UtilizacaoContrato utilizacaoContrato = utilizacaoContratoRepository.findById(request.getIdUtilizacaoContrato())
            .orElseThrow(() -> new UtilizacaoContratoNotFoundException(request.getIdUtilizacaoContrato()));
        
        // 2. Validar se não está cancelada
        if (utilizacaoContrato.isCancelada()) {
            log.warn("Tentativa de editar reserva cancelada - Utilização: {}", request.getIdUtilizacaoContrato());
            throw new ReservaCanceladaNaoEditavelException(request.getIdUtilizacaoContrato());
        }
        
        // 3. Validar se check-in já não passou ou é hoje
        if (utilizacaoContrato.getDataCheckin() != null) {
            LocalDate dataCheckin = utilizacaoContrato.getDataCheckin().toLocalDate();
            LocalDate hoje = LocalDate.now();
            
            if (dataCheckin.isBefore(hoje) || dataCheckin.isEqual(hoje)) {
                log.warn("Tentativa de editar reserva com check-in passado - Utilização: {}, Check-in: {}", 
                    request.getIdUtilizacaoContrato(), dataCheckin);
                throw new ReservaCheckinPassadoException(request.getIdUtilizacaoContrato(), dataCheckin);
            }
        }
        
        // 4. Validar que pertence ao cliente autenticado
        Contrato contrato = contratoRepository.findById(request.getIdContrato())
            .orElseThrow(() -> new ContratoNotFoundException(request.getIdContrato()));
        
        boolean contratoPerteceAoCliente = contratoRepository.contratoPerteceAoCliente(
            request.getIdContrato(), 
            idPessoaCliente
        );
        
        if (!contratoPerteceAoCliente) {
            log.warn("Tentativa de editar utilização negada: Contrato {} não pertence ao cliente {}", 
                request.getIdContrato(), idPessoaCliente);
            throw new ContratoNaoPertenceAoClienteException(
                String.format("O contrato %d não pertence ao cliente autenticado", request.getIdContrato())
            );
        }
        
        // 5. Validar que tipo é RESERVA
        if (!"RESERVA".equals(request.getTipoUtilizacao())) {
            String tipoAtual = utilizacaoContrato.getTipoUtilizacaoContrato() != null ? 
                utilizacaoContrato.getTipoUtilizacaoContrato().getSigla() : "N/A";
            throw new UtilizacaoContratoNaoEditavelException(request.getIdUtilizacaoContrato(), tipoAtual);
        }
        
        // 6. Validar capacidade da UH
        validarCapacidadeUh(utilizacaoContrato, request.getHospedes() != null ? request.getHospedes().size() : 0);
        
        // 7. Validar regras de hóspedes
        if (request.getHospedes() == null || request.getHospedes().isEmpty()) {
            throw new HospedesObrigatoriosException();
        }
        validarHospedePrincipal(request.getHospedes());
        
        // 8. Buscar operador responsável pela edição
        OperadorSistema operadorSistema = operadorSistemaRepository.findByPessoa_IdPessoa(idPessoaCliente)
            .orElseThrow(() -> new OperadorSistemaNaoEncontradoException(idPessoaCliente));
        
        // 9. Processar hóspedes (adicionar/atualizar/remover)
        processarHospedesEdicao(utilizacaoContrato, request.getHospedes(), operadorSistema, contrato);
        
        // 10. Recalcular quantitativos
        long qtdAdultos = utilizacaoContrato.getHospedes().stream()
            .filter(h -> "ADT".equals(h.getFaixaEtariaSigla()))
            .count();
        long qtdCriancas = utilizacaoContrato.getHospedes().size() - qtdAdultos;
        utilizacaoContrato.setQuantitativosHospedes((int) qtdAdultos, (int) qtdCriancas);
        
        int qtdPagantes = calcularQtdPagantes(utilizacaoContrato.getHospedes());
        utilizacaoContrato.definirQtdPagantes(qtdPagantes);
        
        // 11. Salvar alterações
        utilizacaoContrato = utilizacaoContratoRepository.save(utilizacaoContrato);
        
        log.info("Reserva editada com sucesso - ID: {}, {} adultos, {} crianças, {} pagantes", 
            utilizacaoContrato.getId(), qtdAdultos, qtdCriancas, qtdPagantes);
        
        // 12. Buscar período para retornar na response
        PeriodoUtilizacao periodoUtilizacao = utilizacaoContrato.getPeriodoModeloCota() != null ?
            utilizacaoContrato.getPeriodoModeloCota().getPeriodoUtilizacao() : null;
        
        if (periodoUtilizacao == null) {
            throw new PeriodoUtilizacaoNotFoundException(0L);
        }
        
        return mapearParaResponse(utilizacaoContrato, contrato, periodoUtilizacao);
    }
    
    private void validarCapacidadeUh(UtilizacaoContrato utilizacaoContrato, int quantidadeHospedes) {
        UnidadeHoteleira unidadeHoteleira = utilizacaoContrato.getUnidadeHoteleira();
        
        if (unidadeHoteleira == null) {
            log.warn("Utilização {} não possui unidade hoteleira configurada - validação de capacidade ignorada", 
                utilizacaoContrato.getId());
            return;
        }
        
        Integer capacidadeMaxima = unidadeHoteleira.getCapacidade();
        
        if (capacidadeMaxima == null || capacidadeMaxima <= 0) {
            log.warn("Unidade hoteleira {} não possui capacidade configurada - validação de capacidade ignorada", 
                unidadeHoteleira.getId());
            return;
        }
        
        if (quantidadeHospedes > capacidadeMaxima) {
            log.error("Capacidade da UH excedida - Máxima: {}, Informada: {}", capacidadeMaxima, quantidadeHospedes);
            throw new CapacidadeUhExcedidaException(capacidadeMaxima, quantidadeHospedes);
        }
        
        log.debug("Capacidade da UH validada - Máxima: {}, Informada: {}", capacidadeMaxima, quantidadeHospedes);
    }
    
    private void processarHospedesEdicao(UtilizacaoContrato utilizacaoContrato, 
            List<HospedeDto> hospedesDto, OperadorSistema operador, Contrato contrato) {
        
        log.info("Processando edição de {} hóspedes", hospedesDto.size());
        
        // Buscar hóspedes atuais no banco de dados
        List<UtilizacaoContratoHospede> hospedesAtuais = new ArrayList<>(utilizacaoContrato.getHospedes());
        
        // Criar mapa de hóspedes por ID para acesso rápido
        Map<Long, UtilizacaoContratoHospede> mapaHospedesAtuais = new HashMap<>();
        for (UtilizacaoContratoHospede hospede : hospedesAtuais) {
            mapaHospedesAtuais.put(hospede.getId(), hospede);
        }
        
        // VALIDAR CEP DE TODOS OS HÓSPEDES ANTES DE PROSSEGUIR
        log.debug("Iniciando validação de CEP para {} hóspedes", hospedesDto.size());
        for (HospedeDto dto : hospedesDto) {
            validarCepHospede(dto);
        }
        log.debug("Validação de CEP concluída para todos os hóspedes");
        
        // Criar Set com os IDs dos hóspedes que vêm no request (excluindo nulls)
        Set<Long> idsHospedesNoRequest = hospedesDto.stream()
            .map(HospedeDto::idHospede)
            .filter(id -> id != null && id > 0)
            .collect(Collectors.toSet());
        
        log.debug("IDs de hóspedes no request: {}", idsHospedesNoRequest);
        
        // Processar cada hóspede do request
        for (HospedeDto dto : hospedesDto) {
            if (dto.idHospede() == null || dto.idHospede() <= 0) {
                // NOVO HÓSPEDE - Criar
                log.info("Criando novo hóspede: {}", dto.nome());
                criarNovoHospede(utilizacaoContrato, dto, operador, contrato);
            } else {
                // HÓSPEDE EXISTENTE - Atualizar
                log.info("Atualizando hóspede existente: ID {}", dto.idHospede());
                atualizarHospedeExistente(utilizacaoContrato, dto, operador, contrato, mapaHospedesAtuais);
            }
        }
        
        // Identificar e remover hóspedes que estão no BD mas NÃO estão no request
        List<UtilizacaoContratoHospede> hospedesParaRemover = hospedesAtuais.stream()
            .filter(h -> !idsHospedesNoRequest.contains(h.getId()))
            .collect(Collectors.toList());
        
        for (UtilizacaoContratoHospede hospedeRemover : hospedesParaRemover) {
            log.info("Removendo hóspede que não está no request: ID {} - Nome: {}", 
                hospedeRemover.getId(), hospedeRemover.getNome());
            utilizacaoContrato.removerHospede(hospedeRemover);
        }
        
        log.info("Processamento de hóspedes concluído - {} adicionados/atualizados, {} removidos", 
            hospedesDto.size(), hospedesParaRemover.size());
    }
    
    private void criarNovoHospede(UtilizacaoContrato utilizacaoContrato, HospedeDto dto, 
            OperadorSistema operador, Contrato contrato) {
        // Salvar/atualizar pessoa
        Long idPessoa = pessoaService.salvar(dto, contrato);
        
        Pessoa pessoa = pessoaRepository.findById(idPessoa)
            .orElseThrow(() -> new PessoaNotFoundException(idPessoa));
        
        Long idTipoHospede = determinarTipoHospede(dto, pessoa, contrato);
        TipoHospede tipoHospede = tipoHospedeRepository.findById(idTipoHospede)
            .orElseThrow(() -> new TipoHospedeNotFoundException(idTipoHospede));
        
        FaixaEtaria faixaEtaria = faixaEtariaService.calcularFaixaEtariaPorDataNascimento(pessoa.getDataNascimento());
        
        String nomeCompleto = pessoa.getNome();
        String[] partesNome = nomeCompleto != null ? nomeCompleto.split(" ", 2) : new String[]{"", ""};
        String nome = partesNome[0];
        String sobrenome = partesNome.length > 1 ? partesNome[1] : "";
        
        UtilizacaoContratoHospede hospede = UtilizacaoContratoHospede.novoHospede(
            utilizacaoContrato,
            nome,
            sobrenome,
            pessoa.getCpfCnpj(),
            pessoa.getSexo() != null ? pessoa.getSexo().getCodigo() : null,
            pessoa.getDataNascimento(),
            faixaEtaria,
            tipoHospede,
            "S".equalsIgnoreCase(dto.principal()),
            operador
        );
        
        hospede.setPessoa(pessoa);
        utilizacaoContrato.adicionarHospede(hospede);
    }
    
    private void atualizarHospedeExistente(UtilizacaoContrato utilizacaoContrato, HospedeDto dto,
            OperadorSistema operador, Contrato contrato, Map<Long, UtilizacaoContratoHospede> mapaHospedesAtuais) {
        
        UtilizacaoContratoHospede hospede = mapaHospedesAtuais.get(dto.idHospede());
        
        if (hospede == null) {
            throw new HospedeNotFoundException(dto.idHospede());
        }
        
        // Validar que o hóspede pertence à utilização
        if (!hospede.getUtilizacaoContrato().getId().equals(utilizacaoContrato.getId())) {
            throw new HospedeNotFoundException(dto.idHospede());
        }
        
        // Salvar/atualizar pessoa (com proteção de proprietário)
        Long idPessoa = pessoaService.salvar(dto, contrato);
        Pessoa pessoa = pessoaRepository.findById(idPessoa)
            .orElseThrow(() -> new PessoaNotFoundException(idPessoa));
        
        // Atualizar dados do hóspede
        hospede.alterarDadosPessoais(
            pessoa.getNome().split(" ", 2)[0],
            pessoa.getNome().split(" ", 2).length > 1 ? pessoa.getNome().split(" ", 2)[1] : "",
            pessoa.getCpfCnpj(),
            pessoa.getSexo() != null ? pessoa.getSexo().getCodigo() : null,
            pessoa.getDataNascimento(),
            operador
        );
        
        // Recalcular faixa etária se necessário
        FaixaEtaria novaFaixaEtaria = faixaEtariaService.calcularFaixaEtariaPorDataNascimento(pessoa.getDataNascimento());
        if (!novaFaixaEtaria.getId().equals(hospede.getFaixaEtaria().getId())) {
            hospede.alterarFaixaEtaria(novaFaixaEtaria, operador);
        }
        
        // Atualizar tipo se necessário
        Long idTipoHospede = determinarTipoHospede(dto, pessoa, contrato);
        if (!idTipoHospede.equals(hospede.getTipoHospede().getId())) {
            TipoHospede novoTipoHospede = tipoHospedeRepository.findById(idTipoHospede)
                .orElseThrow(() -> new TipoHospedeNotFoundException(idTipoHospede));
            hospede.alterarTipoHospede(novoTipoHospede, operador);
        }
        
        // Atualizar flag principal
        boolean isPrincipal = "S".equalsIgnoreCase(dto.principal());
        if (isPrincipal != Boolean.TRUE.equals(hospede.getIsPrincipal())) {
            // TODO: Verificar se há algum método para alterar flag principal
            log.warn("Tentativa de alterar flag principal do hóspede {} - verificar implementação", dto.idHospede());
        }
        
        hospede.setPessoa(pessoa);
    }
    
    // ========== MÉTODO PARA BUSCAR UTILIZAÇÃO ==========
    
    @Override
    public ReservaSemanaResponse buscarUtilizacao(Long idUtilizacaoContrato, Long idPessoaCliente) {
        log.info("Buscando utilização de contrato - ID: {}", idUtilizacaoContrato);
        
        // 1. Buscar utilização
        UtilizacaoContrato utilizacaoContrato = utilizacaoContratoRepository.findById(idUtilizacaoContrato)
            .orElseThrow(() -> new UtilizacaoContratoNotFoundException(idUtilizacaoContrato));
        
        // 2. Buscar contrato
        Long idContrato = utilizacaoContrato.getContrato() != null ? utilizacaoContrato.getContrato().getId() : null;
        
        if (idContrato == null) {
            log.error("Utilização {} não possui contrato associado", idUtilizacaoContrato);
            throw new ContratoNotFoundException(0L);
        }
        
        Contrato contrato = contratoRepository.findById(idContrato)
            .orElseThrow(() -> new ContratoNotFoundException(idContrato));
        
        // 3. Validar que o contrato pertence ao cliente autenticado
        boolean contratoPerteceAoCliente = contratoRepository.contratoPerteceAoCliente(
            idContrato,
            idPessoaCliente
        );
        
        if (!contratoPerteceAoCliente) {
            log.warn("Tentativa de buscar utilização negada: Contrato {} não pertence ao cliente {}",
                idContrato, idPessoaCliente);
            throw new ContratoNaoPertenceAoClienteException(
                String.format("O contrato %d não pertence ao cliente autenticado", idContrato)
            );
        }
        
        // 4. Buscar período de utilização
        PeriodoUtilizacao periodoUtilizacao = utilizacaoContrato.getPeriodoModeloCota() != null ?
            utilizacaoContrato.getPeriodoModeloCota().getPeriodoUtilizacao() : null;
        
        if (periodoUtilizacao == null) {
            throw new PeriodoUtilizacaoNotFoundException(0L);
        }
        
        // 5. Mapear para response
        ReservaSemanaResponse response = mapearParaResponse(utilizacaoContrato, contrato, periodoUtilizacao);
        
        log.info("Utilização encontrada com sucesso - ID: {}, Tipo: {}", 
            idUtilizacaoContrato, response.getTipoUtilizacao());
        
        return response;
    }
    
    // ========== MÉTODO PARA LISTAR RESERVAS POR ANO ==========
    
    @Override
    public List<ReservaResumoResponse> listarReservasPorAno(int ano, Long idPessoaCliente) {
        log.info("Listando reservas do ano {} para cliente {}", ano, idPessoaCliente);
        
        List<UtilizacaoContrato> utilizacoes = utilizacaoContratoRepository
            .findUtilizacoesPorAnoECliente(ano, idPessoaCliente);
        
        log.info("Encontradas {} utilizações para o ano {}", utilizacoes.size(), ano);
        
        return utilizacoes.stream()
            .map(this::mapearParaResumo)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ReservaResumoResponse> listarReservasPorContratoEAno(Long idContrato, int ano, Long idPessoaCliente) {
        log.info("Listando reservas do contrato {} no ano {} para cliente {}", idContrato, ano, idPessoaCliente);
        
        List<UtilizacaoContrato> utilizacoes = utilizacaoContratoRepository
            .findUtilizacoesPorContratoAnoECliente(idContrato, ano, idPessoaCliente);
        
        log.info("Encontradas {} utilizações para o contrato {} no ano {}", utilizacoes.size(), idContrato, ano);
        
        return utilizacoes.stream()
            .map(this::mapearParaResumo)
            .collect(Collectors.toList());
    }
    
    private ReservaResumoResponse mapearParaResumo(UtilizacaoContrato utilizacao) {
        PeriodoUtilizacao periodo = utilizacao.getPeriodoModeloCota() != null ?
            utilizacao.getPeriodoModeloCota().getPeriodoUtilizacao() : null;
        
        String descricaoPeriodo = periodo != null ? 
            String.format("%02d/%02d/%d - %02d/%02d/%d",
                periodo.getDiaInicio(), periodo.getMesInicio(), periodo.getAnoInicio(),
                periodo.getDiaFim(), periodo.getMesFim(), periodo.getAnoFim()) : "";
        
        return ReservaResumoResponse.builder()
            .idUtilizacaoContrato(utilizacao.getId())
            .tipoUtilizacao(utilizacao.getTipoUtilizacaoContrato() != null ? 
                utilizacao.getTipoUtilizacaoContrato().getSigla() : "")
            .checkin(utilizacao.getDataCheckin() != null ? 
                utilizacao.getDataCheckin().toLocalDate() : null)
            .checkout(utilizacao.getDataCheckout() != null ? 
                utilizacao.getDataCheckout().toLocalDate() : null)
            .descricaoPeriodo(descricaoPeriodo)
            .status(utilizacao.getStatus())
            .contrato(utilizacao.getContrato() != null ? 
                utilizacao.getContrato().getNumeroContrato() : null)
            .empresa(utilizacao.getEmpresa() != null ? 
                utilizacao.getEmpresa().getSigla() : null)
            .tipoSemana(utilizacao.getTipoPeriodoUtilizacao() != null ? 
                utilizacao.getTipoPeriodoUtilizacao().getDescricao() : null)
            .build();
    }
}
