package com.sw.tse.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.TransacaoDebitoCredito;

@Repository
public interface TransacaoDebitoCreditoRepository extends JpaRepository<TransacaoDebitoCredito, Long> {
    
    List<TransacaoDebitoCredito> findByMerchantOrderId(String merchantOrderId);
    
    List<TransacaoDebitoCredito> findByNsu(String nsu);
    
    List<TransacaoDebitoCredito> findByPaymentId(String paymentId);
}

