package com.sw.tse.domain.model.db;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "periodogrupocota")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PeriodoGrupoCota {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqperiodogrupocota")
    @SequenceGenerator(name = "seqperiodogrupocota", sequenceName = "seqperiodogrupocota", allocationSize = 1)
    @Column(name = "idperiodogrupocota")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idperiodoutilizacao")
    private PeriodoUtilizacao periodoUtilizacao;

    @ManyToOne
    @JoinColumn(name = "idgrupocota")
    private GrupoCota grupoCota;

    @CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    // Método estático para criar novo período grupo cota
    static PeriodoGrupoCota novoPeriodoGrupoCota(PeriodoUtilizacao periodoUtilizacao, GrupoCota grupoCota) {
        
        PeriodoGrupoCota novoPeriodoGrupo = new PeriodoGrupoCota();
        novoPeriodoGrupo.setPeriodoUtilizacao(periodoUtilizacao);
        novoPeriodoGrupo.setGrupoCota(grupoCota);
        
        return novoPeriodoGrupo;
    }
}
