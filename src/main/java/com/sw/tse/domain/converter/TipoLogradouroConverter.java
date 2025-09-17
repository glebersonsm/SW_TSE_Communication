package com.sw.tse.domain.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sw.tse.domain.model.api.response.TipoLogradouroApiResponse;
import com.sw.tse.domain.model.db.TipoLogradouro;

@Component
public class TipoLogradouroConverter {

	 public TipoLogradouroApiResponse toDto(TipoLogradouro entidade) {
        if (entidade == null) {
            return null;
        }

        return new TipoLogradouroApiResponse(
                entidade.getId(),
                entidade.getDescricao()
        );
	 }
	 
	 public TipoLogradouro toEntity(TipoLogradouroApiResponse dto) {
	     if (dto == null) {
	         return null;
	     }
	     
	     return new TipoLogradouro(
	             dto.id(),
	             dto.descricao(),
	             null 
	     );
	 }
	 
	 public List<TipoLogradouroApiResponse> toDtoList(List<TipoLogradouro> listaDeEntidades) {
	        if (listaDeEntidades == null || listaDeEntidades.isEmpty()) {
	            return Collections.emptyList();
	        }

	    return listaDeEntidades.stream()
	            .map(this::toDto) 
	            .collect(Collectors.toList()); 
	}
	
	 public List<TipoLogradouro> toEntityList(List<TipoLogradouroApiResponse> listaDeDtos) {
	        if (listaDeDtos == null || listaDeDtos.isEmpty()) {
	            return Collections.emptyList();
	        }

	    return listaDeDtos.stream()
	            .map(this::toEntity) 
	            .collect(Collectors.toList()); 
	}
}
