package com.sw.tse.core.util;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sw.tse.domain.model.db.ParametroFinanceiro;
import com.sw.tse.domain.repository.ParametroFinanceiroRepository;

@Component
public class ParametroFinanceiroHelper {

    private static ParametroFinanceiroRepository repository;
    
    // Cache de parâmetros financeiros por empresa (evita N+1 queries)
    // Chave: idEmpresa, Valor: ParametroFinanceiro
    private static final Map<Long, ParametroFinanceiro> cache = new ConcurrentHashMap<>();
    
    // Set de empresas que não têm parâmetro financeiro (evita queries repetidas)
    private static final Set<Long> empresasSemParametro = ConcurrentHashMap.newKeySet();

    @Autowired
    public void setParametroFinanceiroRepository(ParametroFinanceiroRepository repository) {
        ParametroFinanceiroHelper.repository = repository;
    }

    /**
     * Busca parâmetros financeiros por empresa com cache.
     * O cache evita múltiplas queries ao banco quando várias contas financeiras
     * da mesma empresa precisam calcular juros e multas.
     * 
     * @param idEmpresa ID da empresa
     * @return ParametroFinanceiro ou null se não encontrado
     */
    public static ParametroFinanceiro buscarPorEmpresa(Long idEmpresa) {
        if (repository == null || idEmpresa == null) {
            return null;
        }
        
        // Verificar cache primeiro
        ParametroFinanceiro parametroCache = cache.get(idEmpresa);
        if (parametroCache != null) {
            return parametroCache;
        }
        
        // Verificar se já sabemos que esta empresa não tem parâmetro
        if (empresasSemParametro.contains(idEmpresa)) {
            return null;
        }
        
        // Buscar no banco se não estiver em cache
        Optional<ParametroFinanceiro> parametro = repository.findByEmpresaId(idEmpresa);
        ParametroFinanceiro resultado = parametro.orElse(null);
        
        // Armazenar no cache
        if (resultado != null) {
            cache.put(idEmpresa, resultado);
        } else {
            // Marcar que esta empresa não tem parâmetro para evitar queries futuras
            empresasSemParametro.add(idEmpresa);
        }
        
        return resultado;
    }
    
    /**
     * Limpa o cache de parâmetros financeiros.
     * Útil quando parâmetros são atualizados no banco de dados.
     */
    public static void limparCache() {
        cache.clear();
        empresasSemParametro.clear();
    }
    
    /**
     * Remove um parâmetro específico do cache.
     * Útil quando um parâmetro de uma empresa específica é atualizado.
     * 
     * @param idEmpresa ID da empresa
     */
    public static void limparCache(Long idEmpresa) {
        if (idEmpresa != null) {
            cache.remove(idEmpresa);
            empresasSemParametro.remove(idEmpresa);
        }
    }
    
    /**
     * Pré-popula o cache com um parâmetro financeiro.
     * Útil para batch loading de parâmetros.
     * 
     * @param idEmpresa ID da empresa
     * @param parametro ParametroFinanceiro a ser armazenado no cache
     */
    public static void preencherCache(Long idEmpresa, ParametroFinanceiro parametro) {
        if (idEmpresa != null) {
            if (parametro != null) {
                cache.put(idEmpresa, parametro);
                empresasSemParametro.remove(idEmpresa); // Remove do set se estava lá
            } else {
                // Se parametro é null, marca que a empresa não tem parâmetro
                empresasSemParametro.add(idEmpresa);
                cache.remove(idEmpresa); // Remove do cache se estava lá
            }
        }
    }
}
