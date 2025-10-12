package com.sw.tse.domain.service.impl.db;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sw.tse.domain.model.db.ParametroFinanceiro;
import com.sw.tse.domain.repository.ParametroFinanceiroRepository;
import com.sw.tse.domain.service.interfaces.ParametroFinanceiroService;

@Service
public class ParametroFinanceiroServiceImpl implements ParametroFinanceiroService {

    @Autowired
    private ParametroFinanceiroRepository parametroFinanceiroRepository;

    @Override
    public Optional<ParametroFinanceiro> buscarPorEmpresa(Long idEmpresa) {
        if (idEmpresa == null) {
            return Optional.empty();
        }
        return parametroFinanceiroRepository.findByEmpresaId(idEmpresa);
    }
}
