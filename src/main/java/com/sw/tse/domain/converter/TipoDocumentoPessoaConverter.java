package com.sw.tse.domain.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sw.tse.domain.model.api.response.TipoDocumentoPessoaDto;
import com.sw.tse.domain.model.db.TipoDocumentoPessoa;

@Component
public class TipoDocumentoPessoaConverter {

	 public TipoDocumentoPessoaDto toDto(TipoDocumentoPessoa entidade) {
        if (entidade == null) {
            return null;
        }

        return new TipoDocumentoPessoaDto(
                entidade.getId(),
                entidade.getDecricao(),
                entidade.getSysId()
        );
	 }
	 
	 public TipoDocumentoPessoa toEntity(TipoDocumentoPessoaDto dto) {
	     if (dto == null) {
	         return null;
	     }
	     
	     return new TipoDocumentoPessoa(
	             dto.id(),
	             dto.descricao(),
	             dto.sydId() 
	     );
	 }
	 
	 public List<TipoDocumentoPessoaDto> toDtoList(List<TipoDocumentoPessoa> listaDeEntidades) {
	        if (listaDeEntidades == null || listaDeEntidades.isEmpty()) {
	            return Collections.emptyList();
	        }

	    return listaDeEntidades.stream()
	            .map(this::toDto) 
	            .collect(Collectors.toList()); 
	}
	
	 public List<TipoDocumentoPessoa> toEntityList(List<TipoDocumentoPessoaDto> listaDeDtos) {
	        if (listaDeDtos == null || listaDeDtos.isEmpty()) {
	            return Collections.emptyList();
	        }

	    return listaDeDtos.stream()
	            .map(this::toEntity) 
	            .collect(Collectors.toList()); 
	}
}
