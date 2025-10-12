package com.sw.tse.core.util;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sw.tse.domain.model.db.ParametroFinanceiro;
import com.sw.tse.domain.repository.ParametroFinanceiroRepository;

@Component
public class ParametroFinanceiroHelper {

    private static ParametroFinanceiroRepository repository;

    @Autowired
    public void setParametroFinanceiroRepository(ParametroFinanceiroRepository repository) {
        ParametroFinanceiroHelper.repository = repository;
    }

    public static ParametroFinanceiro buscarPorEmpresa(Long idEmpresa) {
        if (repository == null || idEmpresa == null) {
            return null;
        }
        
        Optional<ParametroFinanceiro> parametro = repository.findByEmpresaId(idEmpresa);
        return parametro.orElse(null);
    }
}
