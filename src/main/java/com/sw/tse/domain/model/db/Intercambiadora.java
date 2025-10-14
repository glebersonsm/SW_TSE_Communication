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
@Table(name = "intercambiadora")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Intercambiadora {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqintercambiadora")
    @SequenceGenerator(name = "seqintercambiadora", sequenceName = "seqintercambiadora", allocationSize = 1)
    @Column(name = "idintercambiadora")
    private Long id;

    @CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "idrespcadastro")
    private OperadorSistema responsavelCadastro;

    @ManyToOne
    @JoinColumn(name = "idrespalteracao")
    private OperadorSistema responsavelAlteracao;

    @Column(name = "cobrartaxaassociacao")
    private Boolean cobrarTaxaAssociacao;

    @Column(name = "valortaxaassociacao", precision = 19, scale = 2)
    private BigDecimal valorTaxaAssociacao;

    @Column(name = "tempoduracaoassociacao")
    private Short tempoDuracaoAssociacao;

    // Método estático para criar nova intercambiadora
    static Intercambiadora novaIntercambiadora(String descricao, OperadorSistema responsavelCadastro,
            Boolean cobrarTaxaAssociacao, BigDecimal valorTaxaAssociacao, Short tempoDuracaoAssociacao) {
        
        Intercambiadora novaIntercambiadora = new Intercambiadora();
        novaIntercambiadora.setDescricao(descricao);
        novaIntercambiadora.setResponsavelCadastro(responsavelCadastro);
        novaIntercambiadora.setCobrarTaxaAssociacao(cobrarTaxaAssociacao);
        novaIntercambiadora.setValorTaxaAssociacao(valorTaxaAssociacao);
        novaIntercambiadora.setTempoDuracaoAssociacao(tempoDuracaoAssociacao);
        
        return novaIntercambiadora;
    }
}
