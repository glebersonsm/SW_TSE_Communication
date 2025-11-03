package com.sw.tse.domain.repository;

import com.sw.tse.domain.model.db.CartaoVinculadoPessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartaoVinculadoPessoaRepository extends JpaRepository<CartaoVinculadoPessoa, Long> {
    
    @Query("SELECT c FROM CartaoVinculadoPessoa c WHERE c.pessoa.idPessoa = :pessoaId AND c.ativo = true ORDER BY c.id DESC")
    List<CartaoVinculadoPessoa> findAtivosByPessoaId(@Param("pessoaId") Long pessoaId);
    
    @Query("SELECT c FROM CartaoVinculadoPessoa c WHERE c.id = :id AND c.pessoa.idPessoa = :pessoaId AND c.ativo = true")
    Optional<CartaoVinculadoPessoa> findByIdAndPessoaIdAndAtivoTrue(@Param("id") Long id, @Param("pessoaId") Long pessoaId);
}

