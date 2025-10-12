package com.sw.tse.domain.service.interfaces;

import java.util.List;
import java.util.Optional;

import com.sw.tse.domain.model.db.ContaMovimentacaoBancaria;

public interface ContaMovimentacaoBancariaService {

    ContaMovimentacaoBancaria salvar(ContaMovimentacaoBancaria conta);

    Optional<ContaMovimentacaoBancaria> buscarPorId(Long id);

    List<ContaMovimentacaoBancaria> listarTodas();

    List<ContaMovimentacaoBancaria> buscarPorEmpresa(Long idEmpresa);

    List<ContaMovimentacaoBancaria> buscarPorBanco(Long idBanco);

    List<ContaMovimentacaoBancaria> buscarPorStatus(Boolean inativa);

    ContaMovimentacaoBancaria atualizar(ContaMovimentacaoBancaria conta);

    void excluirPorId(Long id);
}
