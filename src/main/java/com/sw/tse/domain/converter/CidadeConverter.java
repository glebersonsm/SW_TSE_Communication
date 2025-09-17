package com.sw.tse.domain.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sw.tse.domain.model.api.response.CidadeApiResponse;
import com.sw.tse.domain.model.db.Cidade;

/**
 * Conversor responsável por transformar objetos entre CidadeApiResponse e Cidade (entidade).
 * Suporta conversões bidirecionais para objetos individuais e listas.
 */
@Component
public class CidadeConverter {

    /**
     * Converte uma entidade Cidade para CidadeApiResponse.
     * 
     * @param entidade a entidade Cidade a ser convertida
     * @return CidadeApiResponse correspondente ou null se a entidade for null
     */
    public CidadeApiResponse toDto(Cidade entidade) {
        if (entidade == null) {
            return null;
        }

        return CidadeApiResponse.builder()
                .idCidade(entidade.getId())
                .nome(entidade.getNome())
                .codigoIbege(entidade.getCodigoIbge())
                .idEstado(entidade.getIdEstado())
                .uf(entidade.getUf())
                .idPais(entidade.getIdPais())
                .build();
    }
    
    /**
     * Converte um CidadeApiResponse para entidade Cidade.
     * 
     * @param dto o CidadeApiResponse a ser convertido
     * @return Cidade (entidade) correspondente ou null se o dto for null
     */
    public Cidade toEntity(CidadeApiResponse dto) {
        if (dto == null) {
            return null;
        }
        
        return new Cidade(
                dto.getIdCidade(),
                dto.getNome(),
                dto.getCodigoIbege(),
                dto.getIdEstado(),
                dto.getUf(),
                dto.getIdPais()
        );
    }
    
    /**
     * Converte uma lista de entidades Cidade para uma lista de CidadeApiResponse.
     * 
     * @param listaDeEntidades lista de entidades Cidade
     * @return lista de CidadeApiResponse ou lista vazia se a entrada for null/vazia
     */
    public List<CidadeApiResponse> toDtoList(List<Cidade> listaDeEntidades) {
        if (listaDeEntidades == null || listaDeEntidades.isEmpty()) {
            return Collections.emptyList();
        }

        return listaDeEntidades.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Converte uma lista de CidadeApiResponse para uma lista de entidades Cidade.
     * 
     * @param listaDeDtos lista de CidadeApiResponse
     * @return lista de entidades Cidade ou lista vazia se a entrada for null/vazia
     */
    public List<Cidade> toEntityList(List<CidadeApiResponse> listaDeDtos) {
        if (listaDeDtos == null || listaDeDtos.isEmpty()) {
            return Collections.emptyList();
        }

        return listaDeDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}