package com.sw.tse.domain.service.impl.db;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;


import com.sw.tse.domain.expection.ContratoBloqueadoPorTagException;
import com.sw.tse.domain.expection.ContratoInadimplenteException;
import com.sw.tse.domain.expection.ContratoIntegralizacaoInsuficienteException;
import com.sw.tse.domain.expection.ContratoNotFoundException;
import com.sw.tse.domain.expection.TipoValidacaoIntegralizacaoInvalidoException;
import com.sw.tse.domain.model.dto.InadimplenciaDto;
import com.sw.tse.domain.model.dto.ValidacaoDisponibilidadeParametros;
import com.sw.tse.domain.repository.ContaFinanceiraRepository;
import com.sw.tse.domain.repository.ContratoRepository;
import com.sw.tse.domain.repository.ContratoTagRepository;
import com.sw.tse.domain.service.interfaces.ContratoDisponibilidadeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContratoDisponibilidadeServiceImpl implements ContratoDisponibilidadeService {
    
    private final ContratoTagRepository contratoTagRepository;
    private final ContaFinanceiraRepository contaFinanceiraRepository;
    private final ContratoRepository contratoRepository;
    
    @Override
    public void validarDisponibilidadeParaReserva(Long idContrato) 
            throws ContratoBloqueadoPorTagException, ContratoIntegralizacaoInsuficienteException, ContratoInadimplenteException {
        
        // Este método não deve ser usado mais - sempre deve passar os parâmetros via request
        // Mantido apenas para compatibilidade, mas não valida integralização (sem parâmetros)
        log.warn("Método validarDisponibilidadeParaReserva(idContrato) chamado sem parâmetros. Validação de integralização será pulada. Use o método com ValidacaoDisponibilidadeParametros.");
        
        ValidacaoDisponibilidadeParametros parametros = ValidacaoDisponibilidadeParametros.builder()
            .idsGrupoTagBloqueio(null) // Sem parâmetros configurados globalmente
            .tipoValidacaoIntegralizacao(null) 
            .valorIntegralizacao(null)
            .validarInadimplencia(true) // Default de segurança
            .validarInadimplenciaCondominio(false)
            .build();
            
        validarDisponibilidadeParaReserva(idContrato, parametros);
    }
    
    @Override
    public void validarDisponibilidadeParaReserva(Long idContrato, ValidacaoDisponibilidadeParametros parametros) 
            throws ContratoBloqueadoPorTagException, ContratoIntegralizacaoInsuficienteException, ContratoInadimplenteException, TipoValidacaoIntegralizacaoInvalidoException, ContratoNotFoundException {
        
        log.info("Iniciando validação de disponibilidade para contrato ID: {}", idContrato);
        
        // 1. Validar bloqueio por tags
        validarBloqueioPorTags(idContrato, parametros);
        
        // 2. Validar integralização mínima (apenas se parâmetros foram fornecidos na request)
        validarIntegralizacaoMinima(idContrato, parametros);
        
        // 3. Validar inadimplência
        validarInadimplencia(idContrato, parametros);
        
        log.info("Contrato ID {} aprovado para reserva - todas as validações passaram", idContrato);
    }
    
    private String obterNumeroContrato(Long idContrato) {
        return contratoRepository.findById(idContrato)
            .map(contrato -> contrato.getNumeroContrato())
            .orElse(String.valueOf(idContrato));
    }
    
    private void validarBloqueioPorTags(Long idContrato, ValidacaoDisponibilidadeParametros parametros) 
            throws ContratoBloqueadoPorTagException {
        
        List<Long> idsGrupoTag = parametros.getIdsGrupoTagBloqueio();
        
        // Se não tem parâmetros de bloqueio, pula validação
        if (idsGrupoTag == null || idsGrupoTag.isEmpty()) {
            log.debug("Nenhum parâmetro de bloqueio por grupo de tag configurado - pulando validação");
            return;
        }
        
        log.debug("Validando bloqueio por grupos de tags - IDs: {}", idsGrupoTag);
        
        boolean temBloqueio = contratoTagRepository.existsTagAtivaVincudaAGrupos(idContrato, idsGrupoTag);
        
        if (temBloqueio) {
            String numeroContrato = obterNumeroContrato(idContrato);
            List<String> tags = contratoTagRepository.findTagsBloqueantes(idContrato, idsGrupoTag);
            String detalheTags = String.join(", ", tags);
            
            log.warn("Contrato {} bloqueado por vínculo com grupos de tags: {} (Tags: {})", 
                numeroContrato, idsGrupoTag, detalheTags);
            
            // Mensagem amigável para o cliente (sem detalhes técnicos)
            // Mas passamos o detalhe técnico para a exceção para que o log e histórico capturem
            throw new ContratoBloqueadoPorTagException(idContrato, numeroContrato, detalheTags, "BLOQUEIO_RESERVA");
        }

        
        log.debug("Contrato ID {} não possui bloqueio por tags", idContrato);
    }
    
    private void validarIntegralizacaoMinima(Long idContrato, ValidacaoDisponibilidadeParametros parametros) 
            throws ContratoIntegralizacaoInsuficienteException, TipoValidacaoIntegralizacaoInvalidoException, ContratoNotFoundException {
        
        String tipoValidacao = parametros.getTipoValidacaoIntegralizacao();
        BigDecimal valorIntegralizacao = parametros.getValorIntegralizacao();
        
        // Se não tem parâmetros de integralização configurados, pula validação
        if (tipoValidacao == null || tipoValidacao.isEmpty() || valorIntegralizacao == null) {
            log.debug("Nenhum parâmetro de integralização configurado - pulando validação");
            return;
        }
        
        tipoValidacao = tipoValidacao.toUpperCase();
        BigDecimal valorMinimo = null;
        
        // Validar se o tipo é FIXO ou PERCENTUAL
        if (!"FIXO".equals(tipoValidacao) && !"PERCENTUAL".equals(tipoValidacao)) {
            throw new TipoValidacaoIntegralizacaoInvalidoException(tipoValidacao);
        }
        
        if ("FIXO".equals(tipoValidacao)) {
            // Usar valor fixo diretamente
            valorMinimo = valorIntegralizacao;
            log.debug("Validação de integralização (FIXO) - valor mínimo: R$ {}", valorMinimo);
        } else if ("PERCENTUAL".equals(tipoValidacao)) {
            // Calcular valor mínimo baseado no percentual e valor negociado do contrato
            var contratoOptional = contratoRepository.findById(idContrato);
            if (!contratoOptional.isPresent()) {
                throw new ContratoNotFoundException(idContrato);
            }
            
            var contrato = contratoOptional.get();
            if (contrato.getValorNegociado() == null) {
                log.warn("Valor negociado do contrato não disponível para cálculo percentual. Validação de integralização será pulada.");
                return;
            }
            
            BigDecimal valorNegociado = contrato.getValorNegociado();
            valorMinimo = valorNegociado
                .multiply(valorIntegralizacao)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            
            log.debug("Validação de integralização (PERCENTUAL) - valor mínimo calculado: R$ {} ({}% de R$ {})", 
                valorMinimo, valorIntegralizacao, valorNegociado);
        }
        
        log.debug("Validando integralização mínima - valor mínimo: R$ {}", valorMinimo);
        
        BigDecimal valorIntegralizado = contaFinanceiraRepository.calcularValorIntegralizado(idContrato);
        
        if (valorIntegralizado.compareTo(valorMinimo) < 0) {
            String numeroContrato = obterNumeroContrato(idContrato);
            log.warn("Contrato {} com integralização insuficiente: R$ {} (mínimo: R$ {})", 
                numeroContrato, valorIntegralizado, valorMinimo);
            throw new ContratoIntegralizacaoInsuficienteException(idContrato, numeroContrato, valorIntegralizado, valorMinimo);
        }
        
        log.debug("Contrato ID {} aprovado na validação de integralização: R$ {}", idContrato, valorIntegralizado);
    }
    
    private void validarInadimplencia(Long idContrato, ValidacaoDisponibilidadeParametros parametros) 
            throws ContratoInadimplenteException {
        
        // Validar inadimplência de contrato
        if (Boolean.TRUE.equals(parametros.getValidarInadimplencia())) {
            log.debug("Validando inadimplência de contrato");
            
            Optional<InadimplenciaDto> inadimplenciaContrato = contaFinanceiraRepository.buscarInadimplenciaContrato(idContrato);
            
            if (inadimplenciaContrato.isPresent() && inadimplenciaContrato.get().getQuantidadeParcelas() > 0) {
                InadimplenciaDto dto = inadimplenciaContrato.get();
                String numeroContrato = obterNumeroContrato(idContrato);
                log.warn("Contrato {} inadimplente: {} parcelas, valor R$ {}", 
                    numeroContrato, dto.getQuantidadeParcelas(), dto.getValorInadimplente());
                throw new ContratoInadimplenteException(
                    idContrato,
                    numeroContrato,
                    dto.getTipo(), 
                    dto.getQuantidadeParcelas(), 
                    dto.getValorInadimplente()
                );
            }
            
            log.debug("Contrato ID {} adimplente (contrato)", idContrato);
        }
        
        // Validar inadimplência de condomínio
        if (Boolean.TRUE.equals(parametros.getValidarInadimplenciaCondominio())) {
            log.debug("Validando inadimplência de condomínio");
            
            Optional<InadimplenciaDto> inadimplenciaCondominio = contaFinanceiraRepository.buscarInadimplenciaCondominio(idContrato);
            
            if (inadimplenciaCondominio.isPresent() && inadimplenciaCondominio.get().getQuantidadeParcelas() > 0) {
                InadimplenciaDto dto = inadimplenciaCondominio.get();
                String numeroContrato = obterNumeroContrato(idContrato);
                log.warn("Contrato {} inadimplente (condomínio): {} parcelas, valor R$ {}", 
                    numeroContrato, dto.getQuantidadeParcelas(), dto.getValorInadimplente());
                throw new ContratoInadimplenteException(
                    idContrato,
                    numeroContrato,
                    dto.getTipo(), 
                    dto.getQuantidadeParcelas(), 
                    dto.getValorInadimplente()
                );
            }
            
            log.debug("Contrato ID {} adimplente (condomínio)", idContrato);
        }
    }
}
