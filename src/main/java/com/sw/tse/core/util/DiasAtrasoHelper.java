package com.sw.tse.core.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import com.sw.tse.core.context.FeriadosContext;

import lombok.extern.slf4j.Slf4j;

/**
 * Helper para cálculo de dias de atraso considerando o primeiro dia útil.
 * Se o vencimento cai em sábado, domingo ou feriado, o primeiro dia para juros
 * é o próximo dia útil. A partir daí, todos os dias (incluindo fins de semana
 * e feriados) entram na contagem.
 *
 * Os feriados são obtidos do FeriadosContext, definido pelo chamador da API
 * (ex: Portal envia a lista ao chamar o TSE). Assim o TSE não depende da API do Portal.
 */
@Slf4j
public class DiasAtrasoHelper {

    /**
     * Calcula os dias de atraso para aplicação de juros.
     * Considera o primeiro dia útil após o vencimento (excluindo sábado, domingo e feriados).
     * Os feriados vêm do FeriadosContext (enviados pelo chamador da API).
     *
     * @param dataVencimento data de vencimento da parcela
     * @param dataReferencia data de referência (geralmente hoje)
     * @param cidadeNome (ignorado - mantido por compatibilidade de assinatura)
     * @param cidadeUf (ignorado - mantido por compatibilidade de assinatura)
     * @param estadoSigla (ignorado - mantido por compatibilidade de assinatura)
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
     * Os feriados vêm do FeriadosContext. Se não houver feriados no contexto, usa fallback (dias corridos).
     */
    public static LocalDate obterProximoDiaUtil(LocalDate data, String cidadeNome, String cidadeUf, String estadoSigla) {
        Set<LocalDate> feriados = FeriadosContext.getFeriados();

        return obterProximoDiaUtilComFeriados(data, feriados);
    }

    /**
     * Calcula o próximo dia útil considerando sábado, domingo e a lista de feriados.
     */
    private static LocalDate obterProximoDiaUtilComFeriados(LocalDate data, Set<LocalDate> feriados) {
        LocalDate candidata = data;
        while (isFimDeSemanaOuFeriado(candidata, feriados)) {
            candidata = candidata.plusDays(1);
        }
        return candidata;
    }

    private static boolean isFimDeSemanaOuFeriado(LocalDate data, Set<LocalDate> feriados) {
        if (data.getDayOfWeek() == DayOfWeek.SATURDAY || data.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return true;
        }
        return feriados.contains(data);
    }

    private static long fallbackDiasCorridos(LocalDate dataVencimento, LocalDate dataReferencia) {
        long dias = ChronoUnit.DAYS.between(dataVencimento, dataReferencia);
        return Math.max(0, dias);
    }

    /**
     * Limpa o cache de primeiro dia útil.
     * Mantido por compatibilidade (cache removido - feriados vêm do contexto).
     */
    public static void limparCache() {
        // Cache removido - feriados vêm do FeriadosContext por requisição
    }
}
