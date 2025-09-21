package com.sw.tse.domain.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sw.tse.domain.model.api.response.ContratoPessoaApiResponse;
import com.sw.tse.domain.model.db.Contrato;


@Component
public class ContratoConverter {


    public ContratoPessoaApiResponse toDto(Contrato entidade) {
        if (entidade == null) {
            return null;
        }

        return new ContratoPessoaApiResponse(entidade.getId(), entidade.getNumeroContrato(), entidade.getStatus(), 
        		entidade.getValorNegociado(), entidade.getEmpresa().getId(), entidade.getEmpresa().getSigla());
    }
    


    public List<ContratoPessoaApiResponse> toDtoList(List<Contrato> listaDeEntidades) {
        if (listaDeEntidades == null || listaDeEntidades.isEmpty()) {
            return Collections.emptyList();
        }

        return listaDeEntidades.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
   
}