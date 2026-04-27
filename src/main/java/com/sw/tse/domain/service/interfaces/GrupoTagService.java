package com.sw.tse.domain.service.interfaces;

import java.util.List;

import com.sw.tse.domain.model.api.response.GrupoTagApiResponse;

public interface GrupoTagService {
    List<GrupoTagApiResponse> listarGruposDeTags();
}
