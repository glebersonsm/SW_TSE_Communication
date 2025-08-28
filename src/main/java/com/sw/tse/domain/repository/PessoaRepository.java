package com.sw.tse.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sw.tse.domain.model.db.Pessoa;


public interface PessoaRepository extends JpaRepository<Pessoa, Long>{

	List<Pessoa> findFirstByCpfCnpjOrderByIdPessoa(String cpfCnpj);
}
