package com.sw.tse.domain.service.impl.db;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sw.tse.domain.model.api.response.GrupoTagApiResponse;
import com.sw.tse.domain.repository.ConvencaoSistemaRepository;
import com.sw.tse.domain.service.interfaces.GrupoTagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GrupoTagServiceImpl implements GrupoTagService {

    private final ConvencaoSistemaRepository repository;

    @Override
    public List<GrupoTagApiResponse> listarGruposDeTags() {
        // O grupo para tags no TSE é "CONTRATOTS_TIPOTAG_GRUPOTAG"
        return repository.findByGrupo("CONTRATOTS_TIPOTAG_GRUPOTAG").stream()
                .map(cs -> GrupoTagApiResponse.builder()
                        .id(cs.getId())
                        .sysId(cs.getSysId())
                        .descricao(cs.getDescricao())
                        .build())
                .collect(Collectors.toList());
    }
}
