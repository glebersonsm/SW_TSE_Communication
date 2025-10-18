package com.sw.tse.domain.service.impl.db;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sw.tse.domain.expection.DataNascimentoNullException;
import com.sw.tse.domain.expection.FaixaEtariaNotFoundException;
import com.sw.tse.domain.model.db.FaixaEtaria;
import com.sw.tse.domain.repository.FaixaEtariaRepository;
import com.sw.tse.domain.service.interfaces.FaixaEtariaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FaixaEtariaServiceImpl implements FaixaEtariaService {
    
    private final FaixaEtariaRepository faixaEtariaRepository;
    
    @Value("${sw.tse.faixa.etaria.ch1.idade.inicial}")
    private Integer idadeInicialCH1;
    
    @Value("${sw.tse.faixa.etaria.ch2.idade.inicial}")
    private Integer idadeInicialCH2;
    
    @Value("${sw.tse.faixa.etaria.adt.idade.inicial}")
    private Integer idadeInicialADT;
    
    @Override
    public FaixaEtaria calcularFaixaEtariaPorDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            throw new DataNascimentoNullException();
        }
        
        // Calcular idade em anos
        int idade = Period.between(dataNascimento, LocalDate.now()).getYears();
        
        log.debug("Calculando faixa etária para idade: {} anos", idade);
        
        String sigla;
        
        // Determinar sigla baseado nos parâmetros (verificar do maior para o menor)
        if (idade >= idadeInicialADT) {
            sigla = "ADT";
        } else if (idade >= idadeInicialCH2) {
            sigla = "CH2";
        } else if (idade >= idadeInicialCH1) {
            sigla = "CH1";
        } else {
            // Para idades menores que CH1, usar CH1 como padrão
            sigla = "CH1";
            log.warn("Idade {} menor que idade inicial CH1 ({}), usando CH1 como padrão", idade, idadeInicialCH1);
        }
        
        log.debug("Sigla da faixa etária determinada: {} para idade {}", sigla, idade);
        
        return faixaEtariaRepository.findBySigla(sigla)
            .orElseThrow(() -> new FaixaEtariaNotFoundException(sigla));
    }
}
