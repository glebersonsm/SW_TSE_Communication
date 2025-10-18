package com.sw.tse.domain.service.interfaces;

import java.time.LocalDate;

import com.sw.tse.domain.model.db.FaixaEtaria;

public interface FaixaEtariaService {
    
    /**
     * Calcula a faixa etária baseado na data de nascimento
     * 
     * @param dataNascimento Data de nascimento da pessoa
     * @return FaixaEtaria correspondente à idade calculada
     * @throws IllegalArgumentException se não encontrar faixa etária para a idade
     */
    FaixaEtaria calcularFaixaEtariaPorDataNascimento(LocalDate dataNascimento);
}
