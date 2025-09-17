package com.sw.tse.domain.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sw.tse.domain.model.api.response.TipoEnderecoApiResponse;
import com.sw.tse.domain.model.db.TipoEnderecoPessoa;

@Component
public class TipoEnderecoConverter {


	 public TipoEnderecoApiResponse toDto(TipoEnderecoPessoa entidade) {
        if (entidade == null) {
            return null;
        }

        return new TipoEnderecoApiResponse(
                entidade.getId(),
                entidade.getDecricao()
        );
	 }
	 

	 public TipoEnderecoPessoa toEntity(TipoEnderecoApiResponse dto) {
	     if (dto == null) {
	         return null;
	     }
	     
	     return new TipoEnderecoPessoa(
	             dto.id(),
	             dto.descricao(),
	             null
	     );
	 }
	 

	 public List<TipoEnderecoApiResponse> toDtoList(List<TipoEnderecoPessoa> listaDeEntidades) {
	        if (listaDeEntidades == null || listaDeEntidades.isEmpty()) {
	            return Collections.emptyList();
	        }

	    return listaDeEntidades.stream()
	            .map(this::toDto) 
	            .collect(Collectors.toList()); 
	}
	

	 public List<TipoEnderecoPessoa> toEntityList(List<TipoEnderecoApiResponse> listaDeDtos) {
	        if (listaDeDtos == null || listaDeDtos.isEmpty()) {
	            return Collections.emptyList();
	        }

	    return listaDeDtos.stream()
	            .map(this::toEntity) 
	            .collect(Collectors.toList()); 
	}
}
