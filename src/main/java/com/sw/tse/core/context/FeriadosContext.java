package com.sw.tse.core.context;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * Contexto de requisição para lista de feriados.
 * Permite que o chamador da API envie a lista de feriados e o TSE use no cálculo de juros,
 * sem depender de consumir a API do Portal.
 */
@Slf4j
public final class FeriadosContext {

    private static final ThreadLocal<Set<LocalDate>> FERIADOS_HOLDER = new ThreadLocal<>();

    private FeriadosContext() {
    }

    /**
     * Define a lista de feriados para a requisição atual.
     * Deve ser chamado no início do processamento (ex: controller/interceptor).
     */
    public static void setFeriados(List<LocalDate> feriados) {
        if (feriados == null || feriados.isEmpty()) {
            FERIADOS_HOLDER.remove();
            return;
        }
        FERIADOS_HOLDER.set(feriados.stream().collect(Collectors.toSet()));
    }

    /**
     * Retorna a lista de feriados da requisição atual, ou conjunto vazio se não definida.
     */
    public static Set<LocalDate> getFeriados() {
        Set<LocalDate> feriados = FERIADOS_HOLDER.get();
        return feriados != null ? feriados : Collections.emptySet();
    }

    /**
     * Verifica se há feriados definidos no contexto.
     */
    public static boolean hasFeriados() {
        Set<LocalDate> feriados = FERIADOS_HOLDER.get();
        return feriados != null && !feriados.isEmpty();
    }

    /**
     * Limpa o contexto. Deve ser chamado ao final do processamento da requisição.
     */
    public static void clear() {
        FERIADOS_HOLDER.remove();
    }
}
