package com.sw.tse.domain.expection;

/**
 * Exceção lançada quando não é encontrada uma BandeiraCartao cadastrada
 * e ativa para o tenant, idBandeira e gateway informados.
 *
 * <p>
 * Indica um problema de configuração no sistema: é necessário cadastrar
 * ou ativar um registro em <code>bandeiracartao</code> com operacao='CREDAV'
 * e nomeestabelecimento contendo o nome do gateway.
 * </p>
 */
public class BandeiraCartaoNaoEncontradaException extends RegraDeNegocioException {

    private static final long serialVersionUID = 1L;

    public BandeiraCartaoNaoEncontradaException(Long idTenant, Integer idBandeira, String nomeGateway) {
        super(String.format(
                "Configuração de bandeira de cartão não encontrada. " +
                        "Verifique o cadastro de bandeiras aceitas para o tenant %d, " +
                        "bandeira ID %d e gateway '%s'. " +
                        "É necessário que exista um registro ativo com operacao='CREDAV' " +
                        "e nomeestabelecimento contendo '%s'.",
                idTenant, idBandeira, nomeGateway, nomeGateway));
    }

    public BandeiraCartaoNaoEncontradaException(String message) {
        super(message);
    }
}
