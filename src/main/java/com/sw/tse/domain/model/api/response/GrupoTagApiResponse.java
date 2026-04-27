package com.sw.tse.domain.model.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrupoTagApiResponse {
    private Long id;
    private String sysId;
    private String descricao;
}
