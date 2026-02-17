package com.sw.tse.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sw.tse.client.config.GeralClientConfig;
import com.sw.tse.domain.model.api.response.ProximoDiaUtilResponse;

@FeignClient(
    name = "feriadoApiClient",
    url = "${sw.tse.portal.api.url}",
    configuration = GeralClientConfig.class
)
public interface FeriadoApiClient {

    /**
     * Obtém o próximo dia útil a partir da data informada, considerando
     * sábados, domingos e feriados (Nacional, Estadual, Municipal conforme cidade/estado).
     * Feriados municipais: consultados por cidadeNome + cidadeUf (evita divergência de IDs entre bancos).
     * Feriados estaduais: consultados por estadoSigla (sigla do estado).
     *
     * @param data data de referência (formato yyyy-MM-dd)
     * @param cidadeNome nome da cidade para feriados municipais (opcional)
     * @param cidadeUf UF da cidade para feriados municipais (opcional)
     * @param estadoSigla sigla do estado para feriados estaduais (opcional)
     * @return resposta com a data no campo Data (yyyy-MM-dd)
     */
    @GetMapping("/api/feriado/proximo-dia-util")
    ProximoDiaUtilResponse obterProximoDiaUtil(
        @RequestParam("data") String data,
        @RequestParam(value = "cidadeNome", required = false) String cidadeNome,
        @RequestParam(value = "cidadeUf", required = false) String cidadeUf,
        @RequestParam(value = "estadoSigla", required = false) String estadoSigla);
}
