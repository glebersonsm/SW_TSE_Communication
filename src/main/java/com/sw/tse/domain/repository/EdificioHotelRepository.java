package com.sw.tse.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sw.tse.domain.model.db.EdificioHotel;

public interface EdificioHotelRepository extends JpaRepository<EdificioHotel, Long> {
    
    @Query("SELECT e FROM EdificioHotel e WHERE e.empresa.id = :idEmpresa ORDER BY e.descricao")
    List<EdificioHotel> findByIdEmpresa(@Param("idEmpresa") Long idEmpresa);
}

