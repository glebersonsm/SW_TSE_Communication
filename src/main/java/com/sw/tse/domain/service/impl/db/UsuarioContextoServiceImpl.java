package com.sw.tse.domain.service.impl.db;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.domain.model.api.dto.ContratoClienteApiResponse;
import com.sw.tse.domain.model.dto.InadimplenciaDto;
import com.sw.tse.domain.model.dto.UsuarioContextoDto;
import com.sw.tse.domain.repository.ContaFinanceiraRepository;
import com.sw.tse.domain.repository.ContratoRepository;
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
    private final ContaFinanceiraRepository contaFinanceiraRepository;
    private final ContratoRepository contratoRepository;

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
            Optional<InadimplenciaDto> inadimplenciaContrato = contaFinanceiraRepository.buscarInadimplenciaContrato(idContrato);
            if (inadimplenciaContrato.isPresent() && inadimplenciaContrato.get().getQuantidadeParcelas() != null && inadimplenciaContrato.get().getQuantidadeParcelas() > 0) {
                temInadimplente = true;
                break;
            }
            Optional<InadimplenciaDto> inadimplenciaCondominio = contaFinanceiraRepository.buscarInadimplenciaCondominio(idContrato);
            if (inadimplenciaCondominio.isPresent() && inadimplenciaCondominio.get().getQuantidadeParcelas() != null && inadimplenciaCondominio.get().getQuantidadeParcelas() > 0) {
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

        LocalDate proximoCheckin = contratos.stream()
                .map(ContratoClienteApiResponse::getProximaUtilizacao)
                .filter(d -> d != null)
                .map(odt -> odt.atZoneSameInstant(ZoneId.systemDefault()).toLocalDate())
                .filter(d -> !d.isBefore(LocalDate.now()))
                .min(LocalDate::compareTo)
                .orElse(null);

        return UsuarioContextoDto.builder()
                .idPessoa(idPessoa)
                .temContratoAdimplente(temAdimplente)
                .temContratoInadimplente(temInadimplente)
                .idsGrupoCota(idsGrupoCota)
                .idsEmpresa(idsEmpresa)
                .proximoCheckin(proximoCheckin)
                .periodoAberturaCalendario(null)
                .build();
    }
}
