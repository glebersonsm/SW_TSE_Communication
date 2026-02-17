package com.sw.tse.domain.model.db;

import java.math.BigDecimal;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Entidade mapeada para a view 'statusfinanceirocontrato'.
 * View de leitura para verificar inadimplência de contratos.
 */
@Entity
@Table(name = "statusfinanceirocontrato")
@Immutable
@Getter
@NoArgsConstructor
public class StatusFinanceiroContrato {

    @Id
    @Column(name = "idcontrato")
    private Long idContrato;

    @Column(name = "numerocontrato")
    private String numeroContrato;

    @Column(name = "idgrupocota")
    private Long idGrupoCota;

    @Column(name = "grupocota")
    private String grupoCota;

    @Column(name = "idmodelocota")
    private Long idModeloCota;

    @Column(name = "modelocota")
    private String modeloCota;

    @Column(name = "qtdeparcelainadimplente")
    private Long qtdeParcelaInadimplente;

    @Column(name = "valorinadimplente")
    private BigDecimal valorInadimplente;

    @Column(name = "status")
    private String status;

}
