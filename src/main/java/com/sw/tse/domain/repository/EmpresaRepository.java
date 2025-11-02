package com.sw.tse.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sw.tse.domain.model.db.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    
    @Query("SELECT e FROM Empresa e ORDER BY e.sigla")
    List<Empresa> findAllOrdered();
    
    // Buscar empresas que têm determinada empresa como administradora
    List<Empresa> findByEmpresaAdministracaoCondominioId(Long idEmpresaAdministradora);
    
    // Verificar se existe alguma empresa que tem esta como administradora
    boolean existsByEmpresaAdministracaoCondominioId(Long idEmpresaAdministradora);
}