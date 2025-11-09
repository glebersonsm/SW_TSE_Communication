package com.sw.tse.domain.service.interfaces;

import java.util.Optional;

import com.sw.tse.api.dto.VoucherReservaResponse;

public interface VoucherReservaService {

    Optional<VoucherReservaResponse> obterDadosVoucherReserva(Long idUtilizacaoContrato, Long idPessoaCliente);
}

