package com.sw.tse.domain.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sw.tse.domain.model.api.response.TipoDocumentoPessoaApiResponse;
import com.sw.tse.domain.model.db.TipoDocumentoPessoa;

@Component
public class TipoDocumentoPessoaConverter {

	 public TipoDocumentoPessoaApiResponse toDto(TipoDocumentoPessoa entidade) {
        if (entidade == null) {
            return null;
        }

        return new TipoDocumentoPessoaApiResponse(
                entidade.getId(),
                entidade.getDecricao(),
                entidade.getSysId()
        );
	 }
	 
	 public TipoDocumentoPessoa toEntity(TipoDocumentoPessoaApiResponse dto) {
	     if (dto == null) {
	         return null;
	     }
	     
	     return new TipoDocumentoPessoa(
	             dto.id(),
	             dto.descricao(),
	             dto.sydId() 
	     );
	 }
	 
	 public List<TipoDocumentoPessoaApiResponse> toDtoList(List<TipoDocumentoPessoa> listaDeEntidades) {
	        if (listaDeEntidades == null || listaDeEntidades.isEmpty()) {
	            return Collections.emptyList();
	        }

	    return listaDeEntidades.stream()
	            .map(this::toDto) 
	            .collect(Collectors.toList()); 
	}
	
	 public List<TipoDocumentoPessoa> toEntityList(List<TipoDocumentoPessoaApiResponse> listaDeDtos) {
	        if (listaDeDtos == null || listaDeDtos.isEmpty()) {
	            return Collections.emptyList();
	        }

	    return listaDeDtos.stream()
	            .map(this::toEntity) 
	            .collect(Collectors.toList()); 
	}
}
