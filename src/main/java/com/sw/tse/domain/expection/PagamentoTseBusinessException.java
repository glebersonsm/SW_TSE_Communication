package com.sw.tse.domain.expection;

/**
 * Exceção para erros de regra de negócio no processamento de pagamentos.
 * Sobrescreve fillInStackTrace para evitar logs de stack trace extensos em
 * erros esperados.
 */
public class PagamentoTseBusinessException extends RegraDeNegocioException {
    private static final long serialVersionUID = 1L;

    public PagamentoTseBusinessException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        // Evita a geração do stack trace para erros de negócio,
        // mantendo o log limpo e focado na mensagem de erro.
        return this;
    }
}
