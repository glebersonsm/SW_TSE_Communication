package com.sw.tse.domain.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sw.tse.domain.model.api.response.TipoEnderecoDto;
import com.sw.tse.domain.model.db.TipoEnderecoPessoa;

@Component
public class TipoEnderecoConverter {


	 public TipoEnderecoDto toDto(TipoEnderecoPessoa entidade) {
        if (entidade == null) {
            return null;
        }

        return new TipoEnderecoDto(
                entidade.getId(),
                entidade.getDecricao()
        );
	 }
	 

	 public TipoEnderecoPessoa toEntity(TipoEnderecoDto dto) {
	     if (dto == null) {
	         return null;
	     }
	     
	     return new TipoEnderecoPessoa(
	             dto.id(),
	             dto.descricao(),
	             null
	     );
	 }
	 

	 public List<TipoEnderecoDto> toDtoList(List<TipoEnderecoPessoa> listaDeEntidades) {
	        if (listaDeEntidades == null || listaDeEntidades.isEmpty()) {
	            return Collections.emptyList();
	        }

	    return listaDeEntidades.stream()
	            .map(this::toDto) 
	            .collect(Collectors.toList()); 
	}
	

	 public List<TipoEnderecoPessoa> toEntityList(List<TipoEnderecoDto> listaDeDtos) {
	        if (listaDeDtos == null || listaDeDtos.isEmpty()) {
	            return Collections.emptyList();
	        }

	    return listaDeDtos.stream()
	            .map(this::toEntity) 
	            .collect(Collectors.toList()); 
	}
}
