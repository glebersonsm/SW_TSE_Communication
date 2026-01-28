package com.sw.tse.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sw.tse.domain.model.db.CotaUh;

public interface CotaUhRepository extends JpaRepository<CotaUh, Long> {

    @Query("SELECT c FROM CotaUh c LEFT JOIN FETCH c.unidadeHoteleira u LEFT JOIN FETCH u.empresa ORDER BY c.identificadorUnicoCota")
    List<CotaUh> findAllWithEmpresaOrdered();
}
