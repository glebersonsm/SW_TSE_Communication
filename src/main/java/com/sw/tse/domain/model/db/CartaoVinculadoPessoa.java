package com.sw.tse.domain.model.db;

import com.sw.tse.core.util.GenericCryptoStringConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cartaovinculadopessoa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartaoVinculadoPessoa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqcartaovinculadopessoa")
    @SequenceGenerator(name = "seqcartaovinculadopessoa", sequenceName = "seqcartaovinculadopessoa", allocationSize = 1)
    @Column(name = "idcartaovinculadopessoa")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idpessoa")
    private Pessoa pessoa;
    
    @CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrespcadastro")
    private OperadorSistema responsavelCadastro;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idbandeirasaceitas")
    private BandeirasAceitas bandeira;
    
    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "numero")
    private String numeroCartao;
    
    @Column(name = "numeromascarado")
    private String numeroMascarado;
    
    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "codseguranca")
    private String codigoSeguranca;
    
    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "mesvalidade")
    private String mesValidade;
    
    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "anovalidade")
    private String anoValidade;
    
    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "nomenocartao")
    private String nomeNoCartao;
    
    @Column(name = "pertenceterceiro")
    private Boolean pertenceTerceiro = false;
    
    @Column(name = "nometerceiro")
    private String nomeTerceiro;
    
    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "cpfterceiro")
    private String cpfTerceiro;
    
    @Column(name = "tipooperacaocartao")
    private String tipoOperacao = "CREDAV";
    
    @Column(name = "ativo")
    private Boolean ativo = true;
}

