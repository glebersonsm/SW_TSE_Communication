package com.sw.tse.domain.service.impl.db;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sw.tse.api.model.EmpresaTseDto;
import com.sw.tse.api.model.GrupoCotaDto;
import com.sw.tse.api.model.TorreDto;
import com.sw.tse.domain.model.db.GrupoCota;
import com.sw.tse.domain.repository.EdificioHotelRepository;
import com.sw.tse.domain.repository.GrupoCotaRepository;
import com.sw.tse.domain.repository.EmpresaRepository;
import com.sw.tse.domain.service.interfaces.ConfiguracaoService;

@Service
public class ConfiguracaoServiceImpl implements ConfiguracaoService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private EdificioHotelRepository edificioHotelRepository;

    @Autowired
    private GrupoCotaRepository grupoCotaRepository;

    @Override
    public List<EmpresaTseDto> listarEmpresas() {
        return empresaRepository.findAllOrdered().stream()
            .map(empresa -> new EmpresaTseDto(
                empresa.getId(),
                empresa.getSigla() != null 
                    ? empresa.getSigla() 
                    : "Empresa " + empresa.getId()
            ))
            .collect(Collectors.toList());
    }

    @Override
    public List<TorreDto> listarTorresPorEmpresa(Long idEmpresa) {
        // Verificar se a empresa é uma administradora de condomínio
        // Se for, buscar torres das empresas que ela administra
        if (empresaRepository.existsByEmpresaAdministracaoCondominioId(idEmpresa)) {
            return buscarTorresDasEmpresasAdministradas(idEmpresa);
        }
        
        // Caso contrário, buscar torres da própria empresa
        return edificioHotelRepository.findByIdEmpresa(idEmpresa).stream()
            .map(edificio -> new TorreDto(
                edificio.getId(),
                edificio.getDescricao()
            ))
            .collect(Collectors.toList());
    }
    
    private List<TorreDto> buscarTorresDasEmpresasAdministradas(Long idEmpresaAdministradora) {
        // Buscar todas as empresas que têm esta como administradora
        var empresasAdministradas = empresaRepository.findByEmpresaAdministracaoCondominioId(idEmpresaAdministradora);
        
        // Buscar torres de todas essas empresas
        return empresasAdministradas.stream()
            .flatMap(empresa -> edificioHotelRepository.findByIdEmpresa(empresa.getId()).stream())
            .map(edificio -> new TorreDto(
                edificio.getId(),
                edificio.getDescricao()
            ))
            .collect(Collectors.toList());
    }

    @Override
    public List<GrupoCotaDto> listarGruposCota() {
        return grupoCotaRepository.findAllWithEmpresaOrdered().stream()
            .map(this::toGrupoCotaDto)
            .collect(Collectors.toList());
    }

    private GrupoCotaDto toGrupoCotaDto(GrupoCota g) {
        String nome = g.getDescricao();
        if (nome == null || nome.isBlank()) {
            nome = "ID " + g.getId();
        }
        if (g.getEmpresa() != null && g.getEmpresa().getSigla() != null && !g.getEmpresa().getSigla().isBlank()) {
            nome = nome + " (" + g.getEmpresa().getSigla() + ")";
        }
        return new GrupoCotaDto(g.getId(), nome);
    }
}

