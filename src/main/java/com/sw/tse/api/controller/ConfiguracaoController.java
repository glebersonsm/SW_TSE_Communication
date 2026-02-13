package com.sw.tse.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.api.model.EmpresaTseDto;
import com.sw.tse.api.model.GrupoCotaDto;
import com.sw.tse.api.model.TorreDto;
import com.sw.tse.domain.service.interfaces.ConfiguracaoService;

@RestController
@RequestMapping("/api/configuracao")
public class ConfiguracaoController {

    @Autowired
    private ConfiguracaoService configuracaoService;

    @GetMapping("/empresas")
    public ResponseEntity<List<EmpresaTseDto>> listarEmpresas() {
        List<EmpresaTseDto> empresas = configuracaoService.listarEmpresas();
        return ResponseEntity.ok(empresas);
    }

    @GetMapping("/empresas/{idEmpresa}/torres")
    public ResponseEntity<List<TorreDto>> listarTorresPorEmpresa(@PathVariable Long idEmpresa) {
        List<TorreDto> torres = configuracaoService.listarTorresPorEmpresa(idEmpresa);
        return ResponseEntity.ok(torres);
    }

    @GetMapping("/grupos-cota")
    public ResponseEntity<List<GrupoCotaDto>> listarGruposCota() {
        List<GrupoCotaDto> grupos = configuracaoService.listarGruposCota();
        return ResponseEntity.ok(grupos);
    }
}

