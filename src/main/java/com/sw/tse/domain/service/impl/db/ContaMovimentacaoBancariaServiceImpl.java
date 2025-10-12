package com.sw.tse.domain.service.impl.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sw.tse.domain.model.db.ContaMovimentacaoBancaria;
import com.sw.tse.domain.repository.ContaMovimentacaoBancariaRepository;
import com.sw.tse.domain.service.interfaces.ContaMovimentacaoBancariaService;

@Service
public class ContaMovimentacaoBancariaServiceImpl implements ContaMovimentacaoBancariaService {

    @Autowired
    private ContaMovimentacaoBancariaRepository contaMovimentacaoBancariaRepository;

    @Override
    public ContaMovimentacaoBancaria salvar(ContaMovimentacaoBancaria conta) {
        return contaMovimentacaoBancariaRepository.save(conta);
    }

    @Override
    public Optional<ContaMovimentacaoBancaria> buscarPorId(Long id) {
        return contaMovimentacaoBancariaRepository.findById(id);
    }

    @Override
    public List<ContaMovimentacaoBancaria> listarTodas() {
        return contaMovimentacaoBancariaRepository.findAll();
    }

    @Override
    public List<ContaMovimentacaoBancaria> buscarPorEmpresa(Long idEmpresa) {
        return contaMovimentacaoBancariaRepository.findByEmpresaId(idEmpresa);
    }

    @Override
    public List<ContaMovimentacaoBancaria> buscarPorBanco(Long idBanco) {
        return contaMovimentacaoBancariaRepository.findByBancoId(idBanco);
    }

    @Override
    public List<ContaMovimentacaoBancaria> buscarPorStatus(Boolean inativa) {
        return contaMovimentacaoBancariaRepository.findByInativa(inativa);
    }

    @Override
    public ContaMovimentacaoBancaria atualizar(ContaMovimentacaoBancaria conta) {
        return contaMovimentacaoBancariaRepository.save(conta);
    }

    @Override
    public void excluirPorId(Long id) {
        contaMovimentacaoBancariaRepository.deleteById(id);
    }
}
