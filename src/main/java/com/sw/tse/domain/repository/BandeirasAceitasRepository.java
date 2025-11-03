package com.sw.tse.domain.repository;

import com.sw.tse.domain.model.db.BandeirasAceitas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BandeirasAceitasRepository extends JpaRepository<BandeirasAceitas, Long> {
    
    Optional<BandeirasAceitas> findByBandeiraIgnoreCase(String bandeira);
}

