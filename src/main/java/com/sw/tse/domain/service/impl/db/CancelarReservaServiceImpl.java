package com.sw.tse.domain.service.impl.db;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sw.tse.core.config.CancelamentoParametros;
import com.sw.tse.domain.expection.CancelamentoForaDoPrazoException;
import com.sw.tse.domain.expection.CancelamentoNaoPermitidoException;
import com.sw.tse.domain.expection.ContratoNaoPertenceAoClienteException;
import com.sw.tse.domain.expection.OperadorSistemaNotFoundException;
import com.sw.tse.domain.expection.UtilizacaoContratoCanceladaException;
import com.sw.tse.domain.expection.UtilizacaoContratoNotFoundException;
import com.sw.tse.domain.model.db.OperadorSistema;
import com.sw.tse.domain.model.db.PeriodoModeloCota;
import com.sw.tse.domain.model.db.UtilizacaoContrato;
import com.sw.tse.domain.repository.OperadorSistemaRepository;
import com.sw.tse.domain.repository.PeriodoModeloCotaRepository;
import com.sw.tse.domain.repository.UtilizacaoContratoRepository;
import com.sw.tse.domain.service.interfaces.CancelarReservaService;
import com.sw.tse.security.JwtTokenUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancelarReservaServiceImpl implements CancelarReservaService {
    
    private final UtilizacaoContratoRepository utilizacaoContratoRepository;
    private final PeriodoModeloCotaRepository periodoModeloCotaRepository;
    private final OperadorSistemaRepository operadorSistemaRepository;
    private final CancelamentoParametros cancelamentoParametros;
    
    @Override
    @Transactional
    public void cancelarReserva(Long idUtilizacaoContrato, String motivo, Long idPessoaCliente) {
        
        log.info("Iniciando cancelamento da utilizacao {} por motivo: {}", idUtilizacaoContrato, motivo);
        
        // 1. Buscar UtilizacaoContrato
        UtilizacaoContrato utilizacao = utilizacaoContratoRepository.findById(idUtilizacaoContrato)
            .orElseThrow(() -> new UtilizacaoContratoNotFoundException(idUtilizacaoContrato));
        
        // 2. Buscar OperadorSistema do JWT
        Long idUsuarioCliente = JwtTokenUtil.getIdUsuarioCliente();
        OperadorSistema operador = operadorSistemaRepository.findById(idUsuarioCliente)
            .orElseThrow(() -> new OperadorSistemaNotFoundException(
                String.format("Operador sistema com ID %d nao encontrado", idUsuarioCliente)
            ));
        
        // 3. Validar se pertence ao cliente
        validarPropriedade(utilizacao, idPessoaCliente);
        
        // 4. Validar se já está cancelada
        if ("CANCELADO".equals(utilizacao.getStatus())) {
            throw new UtilizacaoContratoCanceladaException(idUtilizacaoContrato);
        }
        
        // 5. Obter tipo de utilização
        String sigla = utilizacao.getTipoUtilizacaoContrato().getSigla();
        String tipoUtilizacao;
        if ("DEPSEMANA".equals(sigla)) {
            tipoUtilizacao = "RCI";
        } else if ("DEPPOOL".equals(sigla)) {
            tipoUtilizacao = "POOL";
        } else {
            tipoUtilizacao = sigla;
        }
        
        // 6. Validar se cancelamento é permitido
        validarCancelamentoPermitido(tipoUtilizacao);
        
        // 7. Validar prazo mínimo
        validarPrazoMinimo(utilizacao, tipoUtilizacao);
        
        // 8. Cancelar UtilizacaoContrato
        utilizacao.cancelarUtilizacao(motivo, operador);
        utilizacaoContratoRepository.save(utilizacao);
        
        log.info("UtilizacaoContrato {} cancelada", idUtilizacaoContrato);
        
        // 9. Cancelar PeriodoModeloCota se nao houver outras utilizacoes ativas
        Long idPeriodoModeloCota = utilizacaoContratoRepository.findById(idUtilizacaoContrato)
            .map(u -> u.getPeriodoModeloCota())
            .map(p -> p.getId())
            .orElse(null);
        
        if (idPeriodoModeloCota != null) {
            // Verificar se ha outras utilizacoes ativas no mesmo periodo
            long countOutrasUtilizacoes = periodoModeloCotaRepository
                .countUtilizacoesAtivasByPeriodoModeloCota(idPeriodoModeloCota, idUtilizacaoContrato);
            
            if (countOutrasUtilizacoes == 0) {
                // Nao ha outras utilizacoes ativas, pode deletar o periodo
                PeriodoModeloCota periodoRef = periodoModeloCotaRepository.getReferenceById(idPeriodoModeloCota);
                periodoRef.deletarPeriodo(operador);
                log.info("PeriodoModeloCota {} deletado (sem outras utilizacoes ativas)", idPeriodoModeloCota);
            } else {
                log.info("PeriodoModeloCota {} NAO foi deletado (existem {} utilizacoes ativas)", 
                    idPeriodoModeloCota, countOutrasUtilizacoes);
            }
        }
        
        log.info("Cancelamento da utilizacao {} concluido com sucesso", idUtilizacaoContrato);
    }
    
    private void validarPropriedade(UtilizacaoContrato utilizacao, Long idPessoaCliente) {
        Long idCessionario = utilizacao.getContrato().getPessoaCessionario() != null ? 
            utilizacao.getContrato().getPessoaCessionario().getIdPessoa() : null;
        Long idCoCessionario = utilizacao.getContrato().getPessaoCocessionario() != null ? 
            utilizacao.getContrato().getPessaoCocessionario().getIdPessoa() : null;
        
        if (!idPessoaCliente.equals(idCessionario) && 
            (idCoCessionario == null || !idPessoaCliente.equals(idCoCessionario))) {
            throw new ContratoNaoPertenceAoClienteException(
                String.format("A utilizacao %d nao pertence ao cliente autenticado", utilizacao.getId())
            );
        }
    }
    
    private void validarCancelamentoPermitido(String tipoUtilizacao) {
        boolean permitido = false;
        
        if ("RCI".equals(tipoUtilizacao)) {
            permitido = cancelamentoParametros.getRciPermitido();
        } else if ("RESERVA".equals(tipoUtilizacao)) {
            permitido = cancelamentoParametros.getReservaPermitido();
        } else if ("POOL".equals(tipoUtilizacao)) {
            permitido = cancelamentoParametros.getPoolPermitido();
        }
        
        if (!permitido) {
            throw new CancelamentoNaoPermitidoException(tipoUtilizacao);
        }
    }
    
    private void validarPrazoMinimo(UtilizacaoContrato utilizacao, String tipoUtilizacao) {
        LocalDate dataCheckin = utilizacao.getDataCheckin().toLocalDate();
        LocalDate hoje = LocalDate.now();
        long diasRestantes = ChronoUnit.DAYS.between(hoje, dataCheckin);
        
        int diasMinimos = 0;
        
        if ("RCI".equals(tipoUtilizacao)) {
            diasMinimos = cancelamentoParametros.getRciDiasMinimos();
        } else if ("RESERVA".equals(tipoUtilizacao)) {
            diasMinimos = cancelamentoParametros.getReservaDiasMinimos();
        } else if ("POOL".equals(tipoUtilizacao)) {
            diasMinimos = cancelamentoParametros.getPoolDiasMinimos();
        }
        
        if (diasRestantes < diasMinimos) {
            throw new CancelamentoForaDoPrazoException(tipoUtilizacao, diasMinimos, (int) diasRestantes);
        }
    }
}

