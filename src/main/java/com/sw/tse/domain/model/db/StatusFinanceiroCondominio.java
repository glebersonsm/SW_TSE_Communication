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
 * Entidade mapeada para a view 'statusfinanceiro_condominio'.
 * View de leitura para verificar inadimplência de condomínio.
 */
@Entity
@Table(name = "statusfinanceiro_condominio")
@Immutable
@Getter
@NoArgsConstructor
public class StatusFinanceiroCondominio {

    @Id
    @Column(name = "idcontratocondominio")
    private Long idContratoCondominio;

    @Column(name = "idcontratospe")
    private Long idContratoSpe;

    @Column(name = "numerocontrato")
    private String numeroContrato;

    @Column(name = "statuscontrato")
    private String statusContrato;

    @Column(name = "razaosocial")
    private String razaoSocial;

    @Column(name = "qtde")
    private Long qtde;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "status")
    private String status;

}
