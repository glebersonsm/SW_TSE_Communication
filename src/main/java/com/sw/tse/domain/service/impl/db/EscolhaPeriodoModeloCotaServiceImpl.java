package com.sw.tse.domain.service.impl.db;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sw.tse.domain.model.db.EscolhaPeriodoModeloCota;
import com.sw.tse.domain.repository.EscolhaPeriodoModeloCotaRepository;
import com.sw.tse.domain.service.interfaces.EscolhaPeriodoModeloCotaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EscolhaPeriodoModeloCotaServiceImpl implements EscolhaPeriodoModeloCotaService {

    private final EscolhaPeriodoModeloCotaRepository repository;

    @Override
    @Transactional
    public EscolhaPeriodoModeloCota salvar(EscolhaPeriodoModeloCota escolha) {
        return repository.save(escolha);
    }

    @Override
    public List<EscolhaPeriodoModeloCota> listarPorModeloCota(Long idModeloCota) {
        return repository.findByModeloCotaIdAndAtivoTrueOrderByInicioPeriodoAsc(idModeloCota);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
