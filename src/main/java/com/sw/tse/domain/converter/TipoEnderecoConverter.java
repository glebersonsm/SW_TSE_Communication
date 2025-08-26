package com.sw.tse.domain.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;

import com.sw.tse.controller.model.TipoEnderecoDto;
import com.sw.tse.domain.model.db.TipoEnderecoPessoa;

@Controller
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
	 
	 
	 public List<TipoEnderecoDto> toDtoList(List<TipoEnderecoPessoa> listaDeEntidades) {
	        if (listaDeEntidades == null || listaDeEntidades.isEmpty()) {
	            return Collections.emptyList();
	        }
	

	    return listaDeEntidades.stream()
	            .map(this::toDto) 
	            .collect(Collectors.toList()); 
	}
}
