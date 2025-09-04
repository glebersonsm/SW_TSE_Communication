package com.sw.tse.domain.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sw.tse.domain.model.api.response.OperadorSistemaDto;
import com.sw.tse.domain.model.db.OperadorSistema;

@Component
public class OperadorSistemaConverter {

	 public OperadorSistemaDto toDto(OperadorSistema entidade) {
        if (entidade == null) {
            return null;
        }

        return new OperadorSistemaDto(
                entidade.getId(),
                entidade.getLogin(),
                entidade.getNome(),
                entidade.isHabilitado()
        );
	 }
	 
	 public OperadorSistema toEntity(OperadorSistemaDto dto) {
	     if (dto == null) {
	         return null;
	     }
	     
	     return new OperadorSistema(
	             dto.id(),
	             dto.login(),
	             dto.nome(),
	             dto.habilitado()
	     );
	 }
	 
	 public List<OperadorSistemaDto> toDtoList(List<OperadorSistema> listaDeEntidades) {
	        if (listaDeEntidades == null || listaDeEntidades.isEmpty()) {
	            return Collections.emptyList();
	        }

	    return listaDeEntidades.stream()
	            .map(this::toDto) 
	            .collect(Collectors.toList()); 
	}
	
	 public List<OperadorSistema> toEntityList(List<OperadorSistemaDto> listaDeDtos) {
	        if (listaDeDtos == null || listaDeDtos.isEmpty()) {
	            return Collections.emptyList();
	        }

	    return listaDeDtos.stream()
	            .map(this::toEntity) 
	            .collect(Collectors.toList()); 
	}
}
