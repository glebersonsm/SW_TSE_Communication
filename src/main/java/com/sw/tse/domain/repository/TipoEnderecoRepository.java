package com.sw.tse.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sw.tse.domain.model.db.TipoEnderecoPessoa;

public interface TipoEnderecoRepository extends JpaRepository<TipoEnderecoPessoa, Long> {

}
