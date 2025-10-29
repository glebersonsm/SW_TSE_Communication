package com.sw.tse.domain.model.dto;

import java.util.List;

import com.sw.tse.api.dto.ContaFinanceiraClienteDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContasPaginadasDto {
    private List<ContaFinanceiraClienteDto> contas;
    private int pageNumber;
    private int lastPageNumber;
}

