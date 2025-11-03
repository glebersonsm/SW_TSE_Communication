package com.sw.tse.domain.service.interfaces;

import com.sw.tse.api.dto.*;
import java.util.List;

public interface CartaoVinculadoPessoaService {
    CartaoVinculadoResponseDto salvarCartao(SalvarCartaoRequestDto dto, Long pessoaId);
    List<CartaoVinculadoResponseDto> listarCartoesPessoa(Long pessoaId);
    CartaoParaPagamentoDto obterCartaoParaPagamento(Long cartaoId, Long pessoaId);
    void removerCartao(Long cartaoId, Long pessoaId);
    List<BandeiraAceitaDto> listarBandeirasAceitas();
}

