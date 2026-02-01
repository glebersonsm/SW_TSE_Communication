package com.sw.tse.domain.service.impl.db;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sw.tse.core.config.DisponibilidadeContratoProperties;
import com.sw.tse.core.config.PeriodoUtilizacaoParametros;
import com.sw.tse.domain.expection.ContratoNaoPertenceAoClienteException;
import com.sw.tse.domain.expection.TokenJwtInvalidoException;
import com.sw.tse.domain.model.dto.PeriodoUtilizacaoDisponivel;
import com.sw.tse.domain.model.dto.ValidacaoDisponibilidadeParametros;
import com.sw.tse.domain.repository.ContratoRepository;
import com.sw.tse.domain.repository.PeriodoUtilizacaoCustomRepository;
import com.sw.tse.domain.service.interfaces.ContratoDisponibilidadeService;
import com.sw.tse.domain.service.interfaces.PeriodoUtilizacaoService;
import com.sw.tse.security.JwtTokenUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PeriodoUtilizacaoServiceImpl implements PeriodoUtilizacaoService {

    private final PeriodoUtilizacaoCustomRepository customRepository;
    private final PeriodoUtilizacaoParametros parametros;
    private final ContratoRepository contratoRepository;
    private final ContratoDisponibilidadeService contratoDisponibilidadeService;
    private final DisponibilidadeContratoProperties disponibilidadeProperties;

    @Override
    public List<PeriodoUtilizacaoDisponivel> buscarPeriodosDisponiveisParaReserva(Long idContrato, Integer ano,
            String tipoValidacaoIntegralizacao, BigDecimal valorIntegralizacao) {
       
        Long idPessoaCliente = JwtTokenUtil.getIdPessoaCliente();
        
        if (idPessoaCliente == null) {
            log.error("ID da pessoa cliente não encontrado no contexto JWT");
            throw new TokenJwtInvalidoException("ID da pessoa cliente não está disponível no token de autenticação");
        }
        
        log.info("Validando se o contrato {} pertence ao cliente com ID: {}", idContrato, idPessoaCliente);
        
        boolean contratoPerteceAoCliente = contratoRepository.contratoPerteceAoCliente(idContrato, idPessoaCliente);
        
        if (!contratoPerteceAoCliente) {
            log.warn("Tentativa de acesso negada: Contrato {} não pertence ao cliente {}", idContrato, idPessoaCliente);
            throw new ContratoNaoPertenceAoClienteException(
                String.format("O contrato %d não pertence ao cliente autenticado", idContrato)
            );
        }
        
        log.info("Contrato {} validado com sucesso para o cliente {}", idContrato, idPessoaCliente);
        
        // Validar disponibilidade do contrato para reserva (com params de integralização vindos da Portal API)
        log.info("Validando disponibilidade do contrato {} para reserva", idContrato);
        ValidacaoDisponibilidadeParametros validacaoParametros = ValidacaoDisponibilidadeParametros.builder()
            .idsTipoTagBloqueio(disponibilidadeProperties.getBloqueio().getIdsTipoTag())
            .sysIdsGrupoBloqueio(disponibilidadeProperties.getBloqueio().getSysidsGrupo())
            .tipoValidacaoIntegralizacao(tipoValidacaoIntegralizacao)
            .valorIntegralizacao(valorIntegralizacao)
            .validarInadimplencia(disponibilidadeProperties.getInadimplencia().getValidarContrato())
            .validarInadimplenciaCondominio(disponibilidadeProperties.getInadimplencia().getValidarCondominio())
            .build();
        contratoDisponibilidadeService.validarDisponibilidadeParaReserva(idContrato, validacaoParametros);
        
        List<Object[]> resultados = customRepository.buscarPeriodosDisponiveisParaReserva(
            idContrato,
            ano,
            parametros.getAntecedenciaMinimaDias(),
            parametros.getRciDiasMinimos(),
            parametros.getPoolDiaLimite(),
            parametros.getPoolMesLimite(),
            parametros.getIntercambiadoraRciId()
        );

        log.info("Encontrados {} períodos disponíveis para o contrato {}", resultados.size(), idContrato);

        return resultados.stream()
            .map(this::mapearParaDTO)
            .collect(Collectors.toList());
    }

    private PeriodoUtilizacaoDisponivel mapearParaDTO(Object[] row) {
        PeriodoUtilizacaoDisponivel dto = new PeriodoUtilizacaoDisponivel();
        
        dto.setIdPeriodoUtilizacao(row[0] != null ? ((Number) row[0]).longValue() : null);
        dto.setDescricaoPeriodo((String) row[1]);
        dto.setCheckin(row[2] != null ? ((Date) row[2]).toLocalDate() : null);
        dto.setCheckout(row[3] != null ? ((Date) row[3]).toLocalDate() : null);
        dto.setIdTipoPeriodoUtilizacao(row[4] != null ? ((Number) row[4]).longValue() : null);
        dto.setDescricaoTipoPeriodo((String) row[5]);
        dto.setAno(row[6] != null ? ((Number) row[6]).intValue() : null);
        dto.setReserva(row[7] != null ? ((Number) row[7]).intValue() : null);
        dto.setRci(row[8] != null ? ((Number) row[8]).intValue() : null);
        dto.setPool(row[9] != null ? ((Number) row[9]).intValue() : null);
        dto.setCapacidade(row[10] != null ? ((Number) row[10]).intValue() : null);
        
        return dto;
    }
}
