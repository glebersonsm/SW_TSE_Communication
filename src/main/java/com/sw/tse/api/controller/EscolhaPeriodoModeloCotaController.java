package com.sw.tse.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.api.dto.ApiResponseDto;
import com.sw.tse.api.dto.EscolhaPeriodoModeloCotaDto;
import com.sw.tse.domain.model.db.Empresa;
import com.sw.tse.domain.model.db.EscolhaPeriodoModeloCota;
import com.sw.tse.domain.model.db.ModeloCota;
import com.sw.tse.domain.model.db.OperadorSistema;
import com.sw.tse.domain.repository.ModeloCotaRepository;
import com.sw.tse.domain.repository.OperadorSistemaRepository;
import com.sw.tse.domain.service.interfaces.EscolhaPeriodoModeloCotaService;
import com.sw.tse.security.JwtTokenUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/escolha-periodo-modelo-cota")
@RequiredArgsConstructor
@Tag(name = "Escolha de Período por Modelo de Cota", description = "Gerenciamento das aberturas de calendário")
public class EscolhaPeriodoModeloCotaController {

    private final EscolhaPeriodoModeloCotaService service;
    private final ModeloCotaRepository modeloCotaRepository;
    private final OperadorSistemaRepository operadorSistemaRepository;

    @Operation(summary = "Salvar abertura de calendário", description = "Cadastra ou atualiza uma abertura de calendário para um modelo de cota.")
    @PostMapping
    public ResponseEntity<ApiResponseDto<EscolhaPeriodoModeloCotaDto>> salvar(
            @RequestBody EscolhaPeriodoModeloCotaDto dto) {
        ModeloCota modeloCota = modeloCotaRepository.findById(dto.getIdModeloCota())
                .orElseThrow(() -> new RuntimeException("Modelo de cota não encontrado: " + dto.getIdModeloCota()));

        Empresa empresa = modeloCota.getEmpresa();
        Long idOperador = JwtTokenUtil.getIdPessoaCliente();
        OperadorSistema responsavel = idOperador != null ? operadorSistemaRepository.findById(idOperador).orElse(null)
                : null;

        EscolhaPeriodoModeloCota entidade = EscolhaPeriodoModeloCota.novaEscolhaPeriodoModeloCota(
                dto.getAno(),
                dto.getMes(),
                dto.getAtivo() != null ? dto.getAtivo() : true,
                modeloCota,
                responsavel,
                empresa,
                dto.getInicioPeriodo(),
                dto.getFimPeriodo());

        EscolhaPeriodoModeloCota salva = service.salvar(entidade);

        return ResponseEntity
                .ok(new ApiResponseDto<>(200, true, mapToDto(salva), "Período de abertura salvo com sucesso"));
    }

    @Operation(summary = "Listar aberturas por modelo de cota")
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<EscolhaPeriodoModeloCotaDto>>> listar(@RequestParam Long idModeloCota) {
        List<EscolhaPeriodoModeloCota> lista = service.listarPorModeloCota(idModeloCota);
        List<EscolhaPeriodoModeloCotaDto> dtos = lista.stream().map(this::mapToDto).collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponseDto<>(200, true, dtos, "Listagem obtida com sucesso"));
    }

    @Operation(summary = "Deletar abertura de calendário")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.ok(new ApiResponseDto<>(200, true, null, "Período removido com sucesso"));
    }

    private EscolhaPeriodoModeloCotaDto mapToDto(EscolhaPeriodoModeloCota e) {
        return EscolhaPeriodoModeloCotaDto.builder()
                .id(e.getId())
                .ano(e.getAno())
                .mes(e.getMes())
                .ativo(e.getAtivo())
                .idModeloCota(e.getModeloCota().getId())
                .inicioPeriodo(e.getInicioPeriodo())
                .fimPeriodo(e.getFimPeriodo())
                .build();
    }
}
