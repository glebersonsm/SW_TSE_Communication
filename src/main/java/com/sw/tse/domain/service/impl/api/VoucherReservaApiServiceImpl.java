package com.sw.tse.domain.service.impl.api;

import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.api.dto.VoucherReservaResponse;
import com.sw.tse.client.VoucherReservaApiClient;
import com.sw.tse.domain.service.interfaces.VoucherReservaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(name = "database.enabled", havingValue = "false")
@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherReservaApiServiceImpl implements VoucherReservaService {

    private final VoucherReservaApiClient voucherReservaApiClient;

    @Override
    public Optional<VoucherReservaResponse> obterDadosVoucherReserva(Long idUtilizacaoContrato, Long idPessoaCliente) {
        return null;
    }
}

