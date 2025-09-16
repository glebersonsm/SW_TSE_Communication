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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipounidadehoteleira")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TipoUnidadeHoteleira {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqtipounidadehoteleira")
    @SequenceGenerator(name = "seqtipounidadehoteleira", sequenceName = "seqtipounidadehoteleira", allocationSize = 1)
    @Column(name = "idtipounidadehoteleira")
    private Long id;

    @CreationTimestamp
    @Column(name = "datacadastro")
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    @Column(name = "descricao", length = 30)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "idrespcadastro")
    private OperadorSistema responsavelCadastro;

    @ManyToOne
    @JoinColumn(name = "idrespalteracao")
    private OperadorSistema responsavelAlteracao;

    @ManyToOne
    @JoinColumn(name = "idtenant")
    private Empresa empresa;

    @Column(name = "sigla", length = 15)
    private String sigla;

    @Column(name = "idstipounidadehoteleiraintegracao", length = 250)
    private String idsTipoUnidadeHoteleiraIntegracao;

    @Column(name = "idscategoriatipounidadehoteleiraintegracao", length = 250)
    private String idsCategoriaTipoUnidadeHoteleiraIntegracao;

    @Column(name = "tipouhdeposit", length = 250)
    private String tipoUhDeposit;

    @Column(name = "rateplandeposit", length = 250)
    private String ratePlanDeposit;

    @Column(name = "isconjugado")
    private Boolean isConjugado;

    @Column(name = "tipouhbrinde", length = 250)
    private String tipoUhBrinde;

    @Column(name = "rateplanbrinde", length = 250)
    private String ratePlanBrinde;

    @Column(name = "idtipouhcm", length = 250)
    private String idTipoUhCm;

    @Column(name = "idimportacao", length = 100)
    private String idImportacao;

    @Column(name = "idimportacaocondominio", columnDefinition = "TEXT")
    private String idImportacaoCondominio;

    @Column(name = "capacidadeadulto")
    private Integer capacidadeAdulto;

    @Column(name = "capacidadeidoso")
    private Integer capacidadeIdoso;

    @Column(name = "capacidadechd1")
    private Integer capacidadeChd1;

    @Column(name = "capacidadechd2")
    private Integer capacidadeChd2;

    @Column(name = "idtipouhhits")
    private Short idTipoUhHits;

    public static TipoUnidadeHoteleira novoTipoUnidadeHoteleira(String descricao, String sigla, 
            Empresa empresa, OperadorSistema responsavelCadastro) {
        
        TipoUnidadeHoteleira novoTipo = new TipoUnidadeHoteleira();
        novoTipo.setDescricao(descricao);
        novoTipo.setSigla(sigla);
        novoTipo.setEmpresa(empresa);
        novoTipo.setResponsavelCadastro(responsavelCadastro);
        
        return novoTipo;
    }


    public void alterarDados(String novaDescricao, String novaSigla, OperadorSistema responsavelAlteracao) {
        if (novaDescricao != null && !novaDescricao.trim().isEmpty()) {
            this.setDescricao(novaDescricao);
        }
        
        if (novaSigla != null && !novaSigla.trim().isEmpty()) {
            this.setSigla(novaSigla);
        }
        
        this.setResponsavelAlteracao(responsavelAlteracao);
    }

    public void definirCapacidades(Integer capacidadeAdulto, Integer capacidadeIdoso, 
            Integer capacidadeChd1, Integer capacidadeChd2, OperadorSistema responsavelAlteracao) {
        
        this.setCapacidadeAdulto(capacidadeAdulto);
        this.setCapacidadeIdoso(capacidadeIdoso);
        this.setCapacidadeChd1(capacidadeChd1);
        this.setCapacidadeChd2(capacidadeChd2);
        this.setResponsavelAlteracao(responsavelAlteracao);
    }

    public void configurarIntegracoes(String idsTipoUnidadeHoteleiraIntegracao, 
            String idsCategoriaTipoUnidadeHoteleiraIntegracao, String idTipoUhCm, 
            String idImportacao, String idImportacaoCondominio, Short idTipoUhHits,
            OperadorSistema responsavelAlteracao) {
        
        this.setIdsTipoUnidadeHoteleiraIntegracao(idsTipoUnidadeHoteleiraIntegracao);
        this.setIdsCategoriaTipoUnidadeHoteleiraIntegracao(idsCategoriaTipoUnidadeHoteleiraIntegracao);
        this.setIdTipoUhCm(idTipoUhCm);
        this.setIdImportacao(idImportacao);
        this.setIdImportacaoCondominio(idImportacaoCondominio);
        this.setIdTipoUhHits(idTipoUhHits);
        this.setResponsavelAlteracao(responsavelAlteracao);
    }

    public void configurarDepositoBrinde(String tipoUhDeposit, String ratePlanDeposit, 
            Boolean isConjugado, String tipoUhBrinde, String ratePlanBrinde,
            OperadorSistema responsavelAlteracao) {
        
        this.setTipoUhDeposit(tipoUhDeposit);
        this.setRatePlanDeposit(ratePlanDeposit);
        this.setIsConjugado(isConjugado);
        this.setTipoUhBrinde(tipoUhBrinde);
        this.setRatePlanBrinde(ratePlanBrinde);
        this.setResponsavelAlteracao(responsavelAlteracao);
    }
}