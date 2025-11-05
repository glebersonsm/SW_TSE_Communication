package com.sw.tse.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.BandeiraCartao;

@Repository
public interface BandeiraCartaoRepository extends JpaRepository<BandeiraCartao, Integer> {
    
    /**
     * Busca bandeiras de cartão ativas para processamento de pagamento portal.
     * Nota: O campo nomeEstabelecimento é criptografado, então a comparação do nome 
     * deve ser feita em memória após descriptografar os resultados.
     * 
     * @param idTenant ID da empresa
     * @param idBandeirasAceitas ID da bandeira aceita
     * @return Lista de BandeiraCartao que atendem os critérios (exceto nome do gateway)
     */
    @Query(value = """
        SELECT bc.* 
        FROM bandeiracartao bc
        WHERE bc.idtenant = :idTenant
          AND bc.idbandeirasaceitas = :idBandeirasAceitas
          AND bc.operacao = 'CREDAV'
          AND bc.ativo = true
        ORDER BY bc.datacadastro DESC
        """, nativeQuery = true)
    java.util.List<BandeiraCartao> findBandeirasAtivasParaPagamento(
            @Param("idTenant") Long idTenant,
            @Param("idBandeirasAceitas") Integer idBandeirasAceitas);
}

