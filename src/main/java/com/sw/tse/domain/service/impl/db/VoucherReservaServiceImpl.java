package com.sw.tse.domain.service.impl.db;

import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.api.dto.VoucherReservaResponse;
import com.sw.tse.domain.model.db.Contrato;
import com.sw.tse.domain.model.db.Empresa;
import com.sw.tse.domain.model.db.Pessoa;
import com.sw.tse.domain.repository.ContratoRepository;
import com.sw.tse.domain.service.interfaces.ReservarSemanaService;
import com.sw.tse.domain.service.interfaces.VoucherReservaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(name = "database.enabled", havingValue = "true", matchIfMissing = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherReservaServiceImpl implements VoucherReservaService {

    private final ReservarSemanaService reservarSemanaService;
    private final ContratoRepository contratoRepository;

    @Override
    public Optional<VoucherReservaResponse> obterDadosVoucherReserva(Long idUtilizacaoContrato, Long idPessoaCliente) {
        log.info("Obtendo dados de voucher para utilização {} e cliente {}", idUtilizacaoContrato, idPessoaCliente);

        var utilizacao = reservarSemanaService.buscarUtilizacao(idUtilizacaoContrato, idPessoaCliente);

        var contratoOptional = contratoRepository.findByIdWithRelacionamentos(utilizacao.getIdContrato());
        if (contratoOptional.isEmpty()) {
            log.warn("Contrato {} associado à utilização {} não encontrado", utilizacao.getIdContrato(), idUtilizacaoContrato);
            return Optional.empty();
        }

        Contrato contrato = contratoOptional.get();
        Pessoa cessionario = contrato.getPessoaCessionario();
        Pessoa cocessionario = contrato.getPessaoCocessionario();
        
        String hotelNome = contrato.getCotaUh().getUnidadeHoteleira().getEdificioHotel().getHotel().getDescricao();

        var response = VoucherReservaResponse.builder()
                .idUtilizacaoContrato(utilizacao.getIdUtilizacaoContrato())
                .idContrato(utilizacao.getIdContrato())
                .numeroContrato(utilizacao.getNumeroContrato())
                .tipoUtilizacao(utilizacao.getTipoUtilizacao())
                .descricaoPeriodo(utilizacao.getDescricaoPeriodo())
                .checkin(utilizacao.getCheckin())
                .checkout(utilizacao.getCheckout())
                .capacidade(utilizacao.getCapacidade())
                .empresa(hotelNome)
                .nomeCessionario(cessionario != null ? cessionario.getNome() : null)
                .cpfCessionario(cessionario != null ? cessionario.getCpfCnpj() : null)
                .nomeCocessionario(cocessionario != null ? cocessionario.getNome() : null)
                .cpfCocessionario(cocessionario != null ? cocessionario.getCpfCnpj() : null)
                .hospedes(utilizacao.getHospedes())
                .build();

        return Optional.of(response);
    }
}

