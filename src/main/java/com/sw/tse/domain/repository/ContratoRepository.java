package com.sw.tse.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sw.tse.domain.model.db.Contrato;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    @Query("SELECT c FROM Contrato c " +
           "LEFT JOIN c.pessoaCessionario pc " +
           "LEFT JOIN c.pessaoCocessionario pco " +
           "WHERE pc.cpfCnpj = :cpf " +
           "OR pco.cpfCnpj = :cpf " +
           "ORDER BY c.id DESC")
    List<Contrato> findByPessoaCpf(String cpf);

}
