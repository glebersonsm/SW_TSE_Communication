package com.sw.tse.domain.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;

import com.sw.tse.domain.model.api.response.TipoEnderecoDto;
import com.sw.tse.domain.model.api.response.TipoLogradouroDto;
import com.sw.tse.domain.model.db.TipoEnderecoPessoa;
import com.sw.tse.domain.model.db.TipoLogradouro;

@Controller
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
	 
	 
	 public List<TipoLogradouroDto> toDtoList(List<TipoLogradouro> listaDeEntidades) {
	        if (listaDeEntidades == null || listaDeEntidades.isEmpty()) {
	            return Collections.emptyList();
	        }
	

	    return listaDeEntidades.stream()
	            .map(this::toDto) 
	            .collect(Collectors.toList()); 
	}
}
