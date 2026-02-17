package com.sw.tse.domain.service.interfaces;

import java.util.List;
import com.sw.tse.domain.model.db.EscolhaPeriodoModeloCota;

public interface EscolhaPeriodoModeloCotaService {
    EscolhaPeriodoModeloCota salvar(EscolhaPeriodoModeloCota escolha);

    List<EscolhaPeriodoModeloCota> listarPorModeloCota(Long idModeloCota);

    void deletar(Long id);
}
