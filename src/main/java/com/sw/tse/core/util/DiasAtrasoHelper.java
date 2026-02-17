package com.sw.tse.core.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sw.tse.client.FeriadoApiClient;
import com.sw.tse.domain.model.api.response.ProximoDiaUtilResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Helper para cálculo de dias de atraso considerando o primeiro dia útil.
 * Se o vencimento cai em sábado, domingo ou feriado, o primeiro dia para juros
 * é o próximo dia útil. A partir daí, todos os dias (incluindo fins de semana
 * e feriados) entram na contagem.
 */
@Component
@Slf4j
public class DiasAtrasoHelper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private static FeriadoApiClient feriadoApiClient;

    private static final Map<String, LocalDate> cachePrimeiroDiaUtil = new ConcurrentHashMap<>();

    @Autowired
    public void setFeriadoApiClient(FeriadoApiClient feriadoApiClient) {
        DiasAtrasoHelper.feriadoApiClient = feriadoApiClient;
    }

    /**
     * Calcula os dias de atraso para aplicação de juros.
     * Considera o primeiro dia útil após o vencimento (excluindo sábado, domingo e feriados
     * da cidade da empresa). A partir do primeiro dia útil, conta todos os dias corridos.
     * Feriados municipais: consultados por cidadeNome + cidadeUf (evita divergência de IDs entre bancos).
     * Feriados estaduais: consultados por estadoSigla (sigla do estado).
     *
     * @param dataVencimento data de vencimento da parcela
     * @param dataReferencia data de referência (geralmente hoje)
     * @param cidadeNome nome da cidade da empresa para feriados municipais (pode ser null)
     * @param cidadeUf UF da cidade da empresa para feriados municipais (pode ser null)
     * @param estadoSigla sigla do estado para feriados estaduais (pode ser null)
     * @return número de dias de atraso para cálculo de juros (0 se ainda não passou do primeiro dia útil)
     */
    public static long obterDiasAtraso(
            LocalDate dataVencimento,
            LocalDate dataReferencia,
            String cidadeNome,
            String cidadeUf,
            String estadoSigla) {

        if (dataVencimento == null || dataReferencia == null) {
            return 0;
        }

        if (!dataReferencia.isAfter(dataVencimento) && !dataReferencia.equals(dataVencimento)) {
            return 0;
        }

        LocalDate primeiroDiaUtil = obterProximoDiaUtil(dataVencimento, cidadeNome, cidadeUf, estadoSigla);
        if (primeiroDiaUtil == null) {
            return fallbackDiasCorridos(dataVencimento, dataReferencia);
        }

        if (dataReferencia.isBefore(primeiroDiaUtil)) {
            return 0;
        }

        long dias = ChronoUnit.DAYS.between(primeiroDiaUtil, dataReferencia) + 1;
        return Math.max(0, dias);
    }

    /**
     * Retorna o próximo dia útil após a data informada (considerando sábado, domingo e feriados).
     * Útil para exibição na memória de cálculo.
     * Feriados municipais: cidadeNome + cidadeUf. Feriados estaduais: estadoSigla.
     */
    public static LocalDate obterProximoDiaUtil(LocalDate data, String cidadeNome, String cidadeUf, String estadoSigla) {
        String cacheKey = data + "|" + (cidadeNome != null ? cidadeNome : "") + "|" + (cidadeUf != null ? cidadeUf : "") + "|" + (estadoSigla != null ? estadoSigla : "");

        LocalDate cached = cachePrimeiroDiaUtil.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        if (feriadoApiClient == null) {
            log.warn("FeriadoApiClient não injetado - usando fallback de dias corridos");
            return null;
        }

        try {
            String dataStr = data.format(DATE_FORMATTER);
            ProximoDiaUtilResponse response = feriadoApiClient.obterProximoDiaUtil(
                    dataStr, cidadeNome, cidadeUf, estadoSigla);

            if (response == null || response.getData() == null || response.getData().isEmpty()) {
                log.warn("Resposta vazia do Portal API para proximo-dia-util - usando fallback");
                return null;
            }

            LocalDate proximoDiaUtil = LocalDate.parse(response.getData(), DATE_FORMATTER);
            cachePrimeiroDiaUtil.put(cacheKey, proximoDiaUtil);
            return proximoDiaUtil;
        } catch (Exception e) {
            log.warn("Erro ao obter próximo dia útil da API Portal (data={}, cidadeNome={}, cidadeUf={}, estadoSigla={}): {}. Usando fallback.",
                    data, cidadeNome, cidadeUf, estadoSigla, e.getMessage());
            return null;
        }
    }

    private static long fallbackDiasCorridos(LocalDate dataVencimento, LocalDate dataReferencia) {
        long dias = ChronoUnit.DAYS.between(dataVencimento, dataReferencia);
        return Math.max(0, dias);
    }

    /**
     * Limpa o cache de primeiro dia útil.
     * Útil para testes ou quando os feriados são atualizados.
     */
    public static void limparCache() {
        cachePrimeiroDiaUtil.clear();
    }
}
