package com.sw.tse.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.sw.tse.api.dto.ApiResponseDto;
import com.sw.tse.api.dto.VoucherReservaResponse;
import com.sw.tse.client.config.GeralClientConfig;

@FeignClient(
    name = "voucherReservaApiClient",
    url = "${api.tse.url}",
    configuration = GeralClientConfig.class
)
public interface VoucherReservaApiClient {

    @GetMapping("/api/v1/painelcliente/reservas/{idUtilizacaoContrato}/voucher")
    ApiResponseDto<VoucherReservaResponse> obterDadosVoucherReserva(
        @RequestHeader("Authorization") String tokenCliente,
        @PathVariable("idUtilizacaoContrato") Long idUtilizacaoContrato);
}

