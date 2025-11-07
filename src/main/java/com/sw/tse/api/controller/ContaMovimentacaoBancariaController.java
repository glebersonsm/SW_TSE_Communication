package com.sw.tse.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.api.dto.ContaMovimentacaoBancariaDto;
import com.sw.tse.domain.model.db.ContaMovimentacaoBancaria;
import com.sw.tse.domain.repository.ContaMovimentacaoBancariaRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/contas-movimentacao-bancaria")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Conta Movimentação Bancária", description = "Endpoints para consulta de contas de movimentação bancária")
public class ContaMovimentacaoBancariaController {
    
    private final ContaMovimentacaoBancariaRepository contaMovimentacaoBancariaRepository;
    
    @GetMapping("/empresa/{idEmpresa}")
    @Operation(summary = "Listar contas de movimentação ativas por empresa", 
               description = "Retorna todas as contas de movimentação bancária ativas de uma empresa")
    public ResponseEntity<List<ContaMovimentacaoBancariaDto>> listarPorEmpresa(@PathVariable Long idEmpresa) {
        log.info("Buscando contas de movimentação bancária ativas para empresa: {}", idEmpresa);
        
        // Buscar contas ativas (inativa = false) da empresa
        List<ContaMovimentacaoBancaria> contas = contaMovimentacaoBancariaRepository
                .findByEmpresaIdAndInativa(idEmpresa, false);
        
        log.info("Encontradas {} contas de movimentação ativas para empresa {}", contas.size(), idEmpresa);
        
        // Converter para DTO
        List<ContaMovimentacaoBancariaDto> dtos = contas.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    private ContaMovimentacaoBancariaDto toDto(ContaMovimentacaoBancaria conta) {
        return ContaMovimentacaoBancariaDto.builder()
                .id(conta.getId())
                .idEmpresa(conta.getEmpresa() != null ? conta.getEmpresa().getId() : null)
                .idBanco(conta.getBanco() != null ? conta.getBanco().getId() : null)
                .nomeBanco(conta.getBanco() != null ? conta.getBanco().getDescricao() : null)
                .titularConta(conta.getTitularConta())
                .agencia(conta.getAgencia())
                .digitoAgencia(conta.getDigitoAgencia())
                .numeroConta(conta.getNumeroConta())
                .digitoConta(conta.getDigitoConta())
                .tipoConta(conta.getTipoConta())
                .operacao(conta.getOperacao())
                .inativa(conta.getInativa())
                .build();
    }
}

