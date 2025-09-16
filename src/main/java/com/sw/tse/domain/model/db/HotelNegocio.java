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
@Table(name = "hotelnegocio")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class HotelNegocio {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqhotelnegocio")
    @SequenceGenerator(name = "seqhotelnegocio", sequenceName = "seqhotelnegocio", allocationSize = 1)
    @Column(name = "idhotelnegocio")
    private Long id;

    @Column(name = "descricao", length = 80)
    private String descricao;

    @Column(name = "endereco", length = 250)
    private String endereco;

    @Column(name = "identificadorintercambiadora", length = 10)
    private String identificadorIntercambiadora;

    @CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    @ManyToOne
    @JoinColumn(name = "idrespcadastro")
    private OperadorSistema responsavelCadastro;

    @ManyToOne
    @JoinColumn(name = "idrespalteracao")
    private OperadorSistema responsavelAlteracao;

    @ManyToOne
    @JoinColumn(name = "idempresa")
    private Empresa empresa;

    @Column(name = "cep", length = 10)
    private String cep;

    @Column(name = "codigocidadedimob", length = 10)
    private String codigoCidadeDimob;

    @Column(name = "uf", length = 2)
    private String uf;

    @ManyToOne
    @JoinColumn(name = "idtenant")
    private Empresa empresaTenant;

    @Column(name = "idshotelintegracao", length = 250)
    private String idsHotelIntegracao;

    @Column(name = "multidestino")
    private Boolean multiDestino;

    @Column(name = "taxautilizacaocortesia", precision = 19, scale = 5)
    private BigDecimal taxaUtilizacaoCortesia;

    @Column(name = "idimportacao", length = 100)
    private String idImportacao;

    @Column(name = "utilizaallotment")
    private Boolean utilizaAllotment;

    @Column(name = "utilizataxamanutencaofixa")
    private Boolean utilizaTaxaManutencaoFixa;

    @Column(name = "taxamanutencaofixa", precision = 19, scale = 5)
    private BigDecimal taxaManutencaoFixa;

    @Column(name = "codigointegracaocapere", length = 100)
    private String codigoIntegracaoCapere;

    // Método estático para criar novo hotel de negócio
    public static HotelNegocio novoHotelNegocio(String descricao, String endereco, 
            String identificadorIntercambiadora, Empresa empresa, Empresa empresaTenant,
            OperadorSistema responsavelCadastro) {
        
        HotelNegocio novoHotel = new HotelNegocio();
        novoHotel.setDescricao(descricao);
        novoHotel.setEndereco(endereco);
        novoHotel.setIdentificadorIntercambiadora(identificadorIntercambiadora);
        novoHotel.setEmpresa(empresa);
        novoHotel.setEmpresaTenant(empresaTenant);
        novoHotel.setResponsavelCadastro(responsavelCadastro);
        
        return novoHotel;
    }

    // Método para alterar dados básicos
    public void alterarDados(String novaDescricao, String novoEndereco, 
            String novoIdentificadorIntercambiadora, OperadorSistema responsavelAlteracao) {
        
        if (novaDescricao != null && !novaDescricao.trim().isEmpty()) {
            this.setDescricao(novaDescricao);
        }
        
        if (novoEndereco != null && !novoEndereco.trim().isEmpty()) {
            this.setEndereco(novoEndereco);
        }
        
        if (novoIdentificadorIntercambiadora != null && !novoIdentificadorIntercambiadora.trim().isEmpty()) {
            this.setIdentificadorIntercambiadora(novoIdentificadorIntercambiadora);
        }
        
        this.setResponsavelAlteracao(responsavelAlteracao);
    }

    // Método para configurar localização
    public void configurarLocalizacao(String cep, String codigoCidadeDimob, String uf,
            OperadorSistema responsavelAlteracao) {
        
        this.setCep(cep);
        this.setCodigoCidadeDimob(codigoCidadeDimob);
        this.setUf(uf);
        this.setResponsavelAlteracao(responsavelAlteracao);
    }

    // Método para configurar taxas
    public void configurarTaxas(BigDecimal taxaUtilizacaoCortesia, Boolean utilizaTaxaManutencaoFixa,
            BigDecimal taxaManutencaoFixa, OperadorSistema responsavelAlteracao) {
        
        this.setTaxaUtilizacaoCortesia(taxaUtilizacaoCortesia);
        this.setUtilizaTaxaManutencaoFixa(utilizaTaxaManutencaoFixa);
        this.setTaxaManutencaoFixa(taxaManutencaoFixa);
        this.setResponsavelAlteracao(responsavelAlteracao);
    }

    // Método para configurar integrações
    public void configurarIntegracoes(String idsHotelIntegracao, String codigoIntegracaoCapere,
            String idImportacao, OperadorSistema responsavelAlteracao) {
        
        this.setIdsHotelIntegracao(idsHotelIntegracao);
        this.setCodigoIntegracaoCapere(codigoIntegracaoCapere);
        this.setIdImportacao(idImportacao);
        this.setResponsavelAlteracao(responsavelAlteracao);
    }

    // Método para configurar opções
    public void configurarOpcoes(Boolean multiDestino, Boolean utilizaAllotment,
            OperadorSistema responsavelAlteracao) {
        
        this.setMultiDestino(multiDestino);
        this.setUtilizaAllotment(utilizaAllotment);
        this.setResponsavelAlteracao(responsavelAlteracao);
    }
}