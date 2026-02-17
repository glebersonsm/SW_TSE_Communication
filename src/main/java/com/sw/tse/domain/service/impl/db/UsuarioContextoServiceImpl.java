package com.sw.tse.domain.service.impl.db;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.domain.model.api.dto.ContratoClienteApiResponse;
import com.sw.tse.domain.model.db.StatusFinanceiroCondominio;
import com.sw.tse.domain.model.db.StatusFinanceiroContrato;
import com.sw.tse.domain.model.dto.UsuarioContextoDto;
import com.sw.tse.domain.repository.ContratoRepository;
import com.sw.tse.domain.repository.EscolhaPeriodoModeloCotaRepository;
import com.sw.tse.domain.repository.StatusFinanceiroCondominioRepository;
import com.sw.tse.domain.repository.StatusFinanceiroContratoRepository;
import com.sw.tse.domain.repository.UtilizacaoContratoRepository;
import com.sw.tse.domain.service.interfaces.ContratoClienteService;
import com.sw.tse.domain.service.interfaces.UsuarioContextoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(name = "database.enabled", havingValue = "true")
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioContextoServiceImpl implements UsuarioContextoService {

    private final ContratoClienteService contratoClienteService;
    private final ContratoRepository contratoRepository;
    private final UtilizacaoContratoRepository utilizacaoContratoRepository;
    private final StatusFinanceiroContratoRepository statusFinanceiroContratoRepository;
    private final StatusFinanceiroCondominioRepository statusFinanceiroCondominioRepository;
    private final EscolhaPeriodoModeloCotaRepository escolhaPeriodoModeloCotaRepository;

    @Override
    public UsuarioContextoDto obterContextoUsuario(Long idPessoa) {
        if (idPessoa == null) {
            log.warn("idPessoa nulo ao obter contexto do usuário");
            return UsuarioContextoDto.builder()
                    .idPessoa(null)
                    .temContratoAdimplente(false)
                    .temContratoInadimplente(false)
                    .idsGrupoCota(Collections.emptyList())
                    .idsEmpresa(Collections.emptyList())
                    .build();
        }

        log.debug("Construindo contexto do usuário para idPessoa: {}", idPessoa);

        List<ContratoClienteApiResponse> contratos = contratoClienteService.buscarContratosPorIdUsuario(idPessoa);
        if (contratos == null || contratos.isEmpty()) {
            log.debug("Usuário {} não possui contratos ativos", idPessoa);
            return UsuarioContextoDto.builder()
                    .idPessoa(idPessoa)
                    .temContratoAdimplente(false)
                    .temContratoInadimplente(false)
                    .idsGrupoCota(Collections.emptyList())
                    .idsEmpresa(Collections.emptyList())
                    .build();
        }

        boolean temInadimplente = false;

        for (ContratoClienteApiResponse c : contratos) {
            Long idContrato = c.getIdContrato();

            // 1. Inadimplência de Contrato (Via View)
            Optional<StatusFinanceiroContrato> statusFin = statusFinanceiroContratoRepository
                    .findById(idContrato);
            if (statusFin.isPresent() && "INADIMPLENTE".equalsIgnoreCase(statusFin.get().getStatus())) {
                temInadimplente = true;
                break;
            }

            // 2. Inadimplência de Condomínio (Via View)
            Optional<StatusFinanceiroCondominio> statusCond = statusFinanceiroCondominioRepository
                    .findByIdContratoSpe(idContrato);
            if (statusCond.isPresent() && "INADIMPLENTE".equalsIgnoreCase(statusCond.get().getStatus())) {
                temInadimplente = true;
                break;
            }
        }
        boolean temAdimplente = !temInadimplente;

        List<Long> idsEmpresa = contratos.stream()
                .map(ContratoClienteApiResponse::getIdEmpresa)
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();

        List<Long> idsGrupoCota = contratoRepository.findIdsGrupoCotaAtivosByPessoaId(idPessoa);
        if (idsGrupoCota == null) {
            idsGrupoCota = Collections.emptyList();
        }

        // Lógica de Próximo Check-in (Sem limite de janela futura, incluindo hoje)
        LocalDateTime dataInicio = LocalDate.now().atStartOfDay();

        LocalDate proximoCheckin = utilizacaoContratoRepository
                .findProximoCheckin(idPessoa, dataInicio)
                .map(LocalDateTime::toLocalDate)
                .orElse(null);

        // Lógica de Abertura de Calendário
        List<Long> idsModeloCota = contratoRepository.findIdsModeloCotaAtivosByPessoaId(idPessoa);
        List<LocalDate> periodosAberturaCalendario = null;
        if (idsModeloCota != null && !idsModeloCota.isEmpty()) {
            periodosAberturaCalendario = escolhaPeriodoModeloCotaRepository
                    .findDatasAberturaCalendario(idsModeloCota, LocalDate.now().getYear(),
                            LocalDateTime.now().minusDays(15))
                    .stream()
                    .map(LocalDateTime::toLocalDate)
                    .toList();
        }

        return UsuarioContextoDto.builder()
                .idPessoa(idPessoa)
                .temContratoAdimplente(temAdimplente)
                .temContratoInadimplente(temInadimplente)
                .idsGrupoCota(idsGrupoCota)
                .idsEmpresa(idsEmpresa)
                .proximoCheckin(proximoCheckin)
                .periodosAberturaCalendario(periodosAberturaCalendario)
                .build();
    }
}
