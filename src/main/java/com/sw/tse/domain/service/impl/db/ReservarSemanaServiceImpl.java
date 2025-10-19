package com.sw.tse.domain.service.impl.db;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.tse.api.dto.HospedeDto;
import com.sw.tse.api.dto.HospedeResponse;
import com.sw.tse.api.dto.ReservaSemanaResponse;
import com.sw.tse.api.dto.ReservarSemanaRequest;
import com.sw.tse.core.util.StringUtil;
import com.sw.tse.domain.expection.ApiTseException;
import com.sw.tse.domain.expection.BrasilApiException;
import com.sw.tse.domain.expection.CepInvalidoException;
import com.sw.tse.domain.expection.CepObrigatorioException;
import com.sw.tse.domain.expection.ContratoNotFoundException;
import com.sw.tse.domain.expection.ContratoNaoPertenceAoClienteException;
import com.sw.tse.domain.expection.ContratoSemIntercambioException;
import com.sw.tse.domain.expection.HospedesObrigatoriosException;
import com.sw.tse.domain.expection.OperadorSistemaNaoEncontradoException;
import com.sw.tse.domain.expection.PeriodoUtilizacaoNotFoundException;
import com.sw.tse.domain.expection.PessoaNotFoundException;
import com.sw.tse.domain.expection.HospedePrincipalInvalidoException;
import com.sw.tse.domain.expection.TipoHospedeInvalidoException;
import com.sw.tse.domain.expection.TipoHospedeNotFoundException;
import com.sw.tse.domain.expection.TipoUtilizacaoContratoInvalidoException;
import com.sw.tse.domain.expection.TipoUtilizacaoNaoEncontradoException;
import com.sw.tse.domain.model.db.Contrato;
import com.sw.tse.domain.model.db.ContratoIntercambio;
import com.sw.tse.domain.model.db.FaixaEtaria;
import com.sw.tse.domain.model.db.OperadorSistema;
import com.sw.tse.domain.model.db.PeriodoModeloCota;
import com.sw.tse.domain.model.db.PeriodoUtilizacao;
import com.sw.tse.domain.model.db.Pessoa;
import com.sw.tse.domain.model.db.TipoHospede;
import com.sw.tse.domain.model.db.TipoUtilizacaoContrato;
import com.sw.tse.domain.model.db.UtilizacaoContrato;
import com.sw.tse.domain.model.db.UtilizacaoContratoHospede;
import com.sw.tse.domain.model.dto.BrasilApiErrorResponse;
import com.sw.tse.domain.repository.ContratoIntercambioRepository;
import com.sw.tse.domain.repository.ContratoRepository;
import com.sw.tse.domain.repository.OperadorSistemaRepository;
import com.sw.tse.domain.repository.PeriodoModeloCotaRepository;
import com.sw.tse.domain.repository.PeriodoUtilizacaoRepository;
import com.sw.tse.domain.repository.PessoaRepository;
import com.sw.tse.domain.repository.TipoHospedeRepository;
import com.sw.tse.domain.repository.TipoUtilizacaoContratoRepository;
import com.sw.tse.domain.repository.UtilizacaoContratoRepository;
import com.sw.tse.domain.service.interfaces.CidadeService;
import com.sw.tse.domain.service.interfaces.ContratoDisponibilidadeService;
import com.sw.tse.domain.service.interfaces.FaixaEtariaService;
import com.sw.tse.domain.service.interfaces.PeriodoDisponibilidadeService;
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
    private final OperadorSistemaRepository operadorSistemaRepository;
    private final TipoUtilizacaoContratoRepository tipoUtilizacaoContratoRepository;
    private final PeriodoModeloCotaRepository periodoModeloCotaRepository;
    private final UtilizacaoContratoRepository utilizacaoContratoRepository;
    private final PessoaRepository pessoaRepository;
    private final PessoaService pessoaService;
    private final TipoHospedeRepository tipoHospedeRepository;
    private final FaixaEtariaService faixaEtariaService;
    private final ContratoIntercambioRepository contratoIntercambioRepository;
    private final CidadeService cidadeService;
    
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
        
        // 5. Criar UtilizacaoContrato
        log.info("Criando UtilizacaoContrato do tipo {}", siglaBanco);
        
        UtilizacaoContrato utilizacaoContrato = UtilizacaoContrato.criarUtilizacaoContratoReserva(
            periodoModeloCota, 
            operadorSistema, 
            tipoUtilizacao
        );
        
        // 6. Processar dados específicos por tipo
        if ("RESERVA".equals(request.getTipoUtilizacao())) {
            processarReserva(utilizacaoContrato, request.getHospedes(), operadorSistema, contrato);
        } else if ("RCI".equals(request.getTipoUtilizacao())) {
            processarRCI(utilizacaoContrato, contrato, operadorSistema);
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
        }
        // Para RCI não precisa validar nada no request, buscaremos do contrato
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
    
    private void processarReserva(UtilizacaoContrato utilizacaoContrato, 
            List<HospedeDto> hospedesDto, OperadorSistema operador, Contrato contrato) {
        
        log.info("Processando {} hóspedes para a reserva", hospedesDto.size());
        
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
            Long idPessoa = pessoaService.salvar(dto);
            
            // Buscar pessoa salva
            Pessoa pessoa = pessoaRepository.findById(idPessoa)
                .orElseThrow(() -> new PessoaNotFoundException(idPessoa));
            
            // Determinar TipoHospede automaticamente
            Long idTipoHospede = determinarTipoHospede(dto, pessoa, contrato);
            TipoHospede tipoHospede = tipoHospedeRepository.findById(idTipoHospede)
                .orElseThrow(() -> new TipoHospedeNotFoundException(idTipoHospede));
            
            // Calcular faixa etária
            FaixaEtaria faixaEtaria = faixaEtariaService.calcularFaixaEtariaPorDataNascimento(pessoa.getDataNascimento());
            
            // Criar hóspede
            UtilizacaoContratoHospede hospede = UtilizacaoContratoHospede.novoHospede(
                utilizacaoContrato,
                pessoa.getNome(),
                "",  // sobrenome vazio (não usado)
                pessoa.getCpfCnpj(),
                pessoa.getSexo() != null ? pessoa.getSexo().getCodigo() : null,
                pessoa.getDataNascimento() != null ? pessoa.getDataNascimento().atStartOfDay() : null,
                faixaEtaria,
                tipoHospede,
                i == 0,  // Primeiro é principal
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
        
        log.info("Reserva processada com {} adultos e {} crianças", qtdAdultos, qtdCriancas);
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
            pessoaRCI.getDataNascimento() != null ? pessoaRCI.getDataNascimento().atStartOfDay() : null,
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
            default -> throw new TipoUtilizacaoContratoInvalidoException(tipoFrontend);
        };
    }
    
    private ReservaSemanaResponse mapearParaResponse(UtilizacaoContrato utilizacaoContrato, 
            Contrato contrato, PeriodoUtilizacao periodoUtilizacao) {
        
        List<HospedeResponse> hospedesResponse = null;
        
        // Se houver hóspedes, mapear para o response
        if (utilizacaoContrato.getHospedes() != null && !utilizacaoContrato.getHospedes().isEmpty()) {
            hospedesResponse = utilizacaoContrato.getHospedes().stream()
                .map(this::mapearHospedeParaResponse)
                .collect(Collectors.toList());
        }
        
        return ReservaSemanaResponse.builder()
            .idUtilizacaoContrato(utilizacaoContrato.getId())
            .idPeriodoModeloCota(utilizacaoContrato.getPeriodoModeloCota().getId())
            .numeroContrato(contrato.getNumeroContrato())
            .tipoUtilizacao(utilizacaoContrato.getTipoUtilizacaoContrato().getSigla())
            .checkin(utilizacaoContrato.getDataCheckin().toLocalDate())
            .checkout(utilizacaoContrato.getDataCheckout().toLocalDate())
            .descricaoPeriodo(periodoUtilizacao.getDescricaoPeriodo())
            .status(utilizacaoContrato.getStatus())
            .dataCriacao(utilizacaoContrato.getDataCadastro())
            .hospedes(hospedesResponse)
            .build();
    }
    
    private HospedeResponse mapearHospedeParaResponse(UtilizacaoContratoHospede hospede) {
        return HospedeResponse.builder()
            .idHospede(hospede.getId())
            .nome(hospede.getNome())
            .sobrenome(hospede.getSobrenome())
            .cpf(hospede.getCpf())
            .isPrincipal(hospede.getIsPrincipal())
            .faixaEtaria(hospede.getFaixaEtariaSigla())
            .build();
    }
}
