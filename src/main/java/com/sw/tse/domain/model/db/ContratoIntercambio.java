package com.sw.tse.domain.model.db;

import java.math.BigDecimal;
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
@Table(name = "contratointercambio")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ContratoIntercambio {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqcontratointercambio")
    @SequenceGenerator(name = "seqcontratointercambio", sequenceName = "seqcontratointercambio", allocationSize = 1)
    @Column(name = "idcontratointercambio")
    private Long id;

    @CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    @ManyToOne
    @JoinColumn(name = "idcontrato")
    private Contrato contrato;

    @Column(name = "idintercambiadora")
    private Long idIntercambiadora;

    @Column(name = "taxaassociacao", precision = 19, scale = 2)
    private BigDecimal taxaAssociacao;

    @Column(name = "reembolsotaxaassociacao", precision = 19, scale = 2)
    private BigDecimal reembolsoTaxaAssociacao;

    @Column(name = "identificadorintercambio", length = 20)
    private String identificadorIntercambio;

    @Column(name = "historico", length = 80)
    private String historico;

    @Column(name = "tipohistorico", length = 30)
    private String tipoHistorico;

    @ManyToOne
    @JoinColumn(name = "idrespcadastro")
    private OperadorSistema responsavelCadastro;

    @ManyToOne
    @JoinColumn(name = "idrespalteracao")
    private OperadorSistema responsavelAlteracao;

    @Column(name = "datacancelamento")
    private LocalDateTime dataCancelamento;

    @Column(name = "datasolicitacaocancelamento")
    private LocalDateTime dataSolicitacaoCancelamento;

    @ManyToOne
    @JoinColumn(name = "idrespcancelamento")
    private OperadorSistema responsavelCancelamento;

    @Column(name = "historicocancelamento", length = 80)
    private String historicoCancelamento;

    @ManyToOne
    @JoinColumn(name = "idtenant")
    private Empresa empresa;

    @Column(name = "dataassociacao")
    private LocalDateTime dataAssociacao;

    @Column(name = "identificadortransacao", length = 250)
    private String identificadorTransacao;

    @Column(name = "idcontratointercambioorigemadm")
    private Long idContratoIntercambioOrigemAdm;

    @Column(name = "dataterminoassocicao")
    private LocalDateTime dataTerminoAssociacao;

    // Método estático para criar novo contrato intercâmbio
    static ContratoIntercambio novoContratoIntercambio(Contrato contrato, Long idIntercambiadora,
            OperadorSistema responsavelCadastro, Empresa empresa, String tipoHistorico) {
        
        ContratoIntercambio novoContratoIntercambio = new ContratoIntercambio();
        novoContratoIntercambio.setContrato(contrato);
        novoContratoIntercambio.setIdIntercambiadora(idIntercambiadora);
        novoContratoIntercambio.setResponsavelCadastro(responsavelCadastro);
        novoContratoIntercambio.setEmpresa(empresa);
        novoContratoIntercambio.setTipoHistorico(tipoHistorico);
        
        return novoContratoIntercambio;
    }
}
