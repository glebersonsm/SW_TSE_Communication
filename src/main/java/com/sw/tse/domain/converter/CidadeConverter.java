package com.sw.tse.domain.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sw.tse.domain.model.api.response.CidadeDto;
import com.sw.tse.domain.model.db.Cidade;

/**
 * Conversor responsável por transformar objetos entre CidadeDto e Cidade (entidade).
 * Suporta conversões bidirecionais para objetos individuais e listas.
 */
@Component
public class CidadeConverter {

    /**
     * Converte uma entidade Cidade para CidadeDto.
     * 
     * @param entidade a entidade Cidade a ser convertida
     * @return CidadeDto correspondente ou null se a entidade for null
     */
    public CidadeDto toDto(Cidade entidade) {
        if (entidade == null) {
            return null;
        }

        return CidadeDto.builder()
                .idCidade(entidade.getId())
                .nome(entidade.getNome())
                .codigoIbege(entidade.getCodigoIbge())
                .idEstado(entidade.getIdEstado())
                .uf(entidade.getUf())
                .idPais(entidade.getIdPais())
                .build();
    }
    
    /**
     * Converte um CidadeDto para entidade Cidade.
     * 
     * @param dto o CidadeDto a ser convertido
     * @return Cidade (entidade) correspondente ou null se o dto for null
     */
    public Cidade toEntity(CidadeDto dto) {
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
     * Converte uma lista de entidades Cidade para uma lista de CidadeDto.
     * 
     * @param listaDeEntidades lista de entidades Cidade
     * @return lista de CidadeDto ou lista vazia se a entrada for null/vazia
     */
    public List<CidadeDto> toDtoList(List<Cidade> listaDeEntidades) {
        if (listaDeEntidades == null || listaDeEntidades.isEmpty()) {
            return Collections.emptyList();
        }

        return listaDeEntidades.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Converte uma lista de CidadeDto para uma lista de entidades Cidade.
     * 
     * @param listaDeDtos lista de CidadeDto
     * @return lista de entidades Cidade ou lista vazia se a entrada for null/vazia
     */
    public List<Cidade> toEntityList(List<CidadeDto> listaDeDtos) {
        if (listaDeDtos == null || listaDeDtos.isEmpty()) {
            return Collections.emptyList();
        }

        return listaDeDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}