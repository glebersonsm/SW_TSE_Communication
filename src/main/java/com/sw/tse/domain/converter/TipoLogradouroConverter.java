package com.sw.tse.domain.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sw.tse.domain.model.api.response.TipoLogradouroDto;
import com.sw.tse.domain.model.db.TipoLogradouro;

@Component
public class TipoLogradouroConverter {

	 public TipoLogradouroDto toDto(TipoLogradouro entidade) {
        if (entidade == null) {
            return null;
        }

        return new TipoLogradouroDto(
                entidade.getId(),
                entidade.getDescricao()
        );
	 }
	 
	 public TipoLogradouro toEntity(TipoLogradouroDto dto) {
	     if (dto == null) {
	         return null;
	     }
	     
	     return new TipoLogradouro(
	             dto.id(),
	             dto.descricao(),
	             null 
	     );
	 }
	 
	 public List<TipoLogradouroDto> toDtoList(List<TipoLogradouro> listaDeEntidades) {
	        if (listaDeEntidades == null || listaDeEntidades.isEmpty()) {
	            return Collections.emptyList();
	        }

	    return listaDeEntidades.stream()
	            .map(this::toDto) 
	            .collect(Collectors.toList()); 
	}
	
	 public List<TipoLogradouro> toEntityList(List<TipoLogradouroDto> listaDeDtos) {
	        if (listaDeDtos == null || listaDeDtos.isEmpty()) {
	            return Collections.emptyList();
	        }

	    return listaDeDtos.stream()
	            .map(this::toEntity) 
	            .collect(Collectors.toList()); 
	}
}
