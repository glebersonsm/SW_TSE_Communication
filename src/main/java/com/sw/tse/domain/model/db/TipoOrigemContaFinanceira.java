package com.sw.tse.domain.model.db;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipoorigemcontafinanceira")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class TipoOrigemContaFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtipoorigemcontafinanceira")
    private Integer idTipoOrigemContaFinanceira;

    @Column(name = "descricao", length = 30, nullable = false)
    private String descricao;

    @Column(name = "sysid", length = 30, nullable = false)
    private String sysId;

    @Column(name = "utilizadocontasreceber")
    private Boolean utilizadoContasReceber;

    @Column(name = "utilizadocontaspagar")
    private Boolean utilizadoContasPagar;

    @CreationTimestamp
    @Column(name = "datacadastro", insertable = true, updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao", insertable = false, updatable = true)
    private LocalDateTime dataAlteracao;

    @Column(name = "obrigabaixaparaliberarutulizacao")
    private Boolean obrigaBaixaParaLiberarUtilizacao;

    @Column(name = "codigotipoparcelaintegracao", length = 50)
    private String codigoTipoParcelaIntegracao;

    @Column(name = "passivodecorrecaomonetaria", nullable = false)
    private Boolean passivoDeCorrecaoMonetaria = false;
}
