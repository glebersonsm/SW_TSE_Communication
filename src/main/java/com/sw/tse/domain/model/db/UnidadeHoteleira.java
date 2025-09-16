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
@Table(name = "unidadehoteleira")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class UnidadeHoteleira {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequnidadehoteleira")
    @SequenceGenerator(name = "sequnidadehoteleira", sequenceName = "sequnidadehoteleira", allocationSize = 1)
    @Column(name = "idunidadehoteleira")
    private Long id;

    @CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    @Column(name = "blocopredio", length = 60)
    private String blocoPredio;

    @Column(name = "andarpredio", length = 60)
    private String andarPredio;

    @ManyToOne
    @JoinColumn(name = "idedificiohotel")
    private EdificioHotel edificioHotel;

    @ManyToOne
    @JoinColumn(name = "idhotelnegocio")
    private HotelNegocio hotelNegocio;

    @Column(name = "descricao", length = 60)
    private String descricao;

    @Column(name = "observacao", length = 250)
    private String observacao;

    @Column(name = "capacidade")
    private Integer capacidade;

    @Column(name = "qtdquartos")
    private Integer qtdQuartos;

    @Column(name = "areatotalmetrosquad")
    private Double areaTotalMetrosQuad;

    @Column(name = "areacomummetrosquad")
    private Double areaComumMetrosQuad;

    @Column(name = "fracaoidealporcent")
    private Double fracaoIdealPorcent;

    @Column(name = "fracaoidelmetrosquad")
    private Double fracaoIdealMetrosQuad;

    @Column(name = "areaprivativametrosquad")
    private Double areaPrivativaMetrosQuad;

    @Column(name = "percentualterreno")
    private Double percentualTerreno;

    @Column(name = "percentualcotauh")
    private Double percentualCotaUh;

    @Column(name = "qtdvagasestacionamento")
    private Integer qtdVagasEstacionamento;

    @Column(name = "areagaragemmetrosquad")
    private Double areaGaragemMetrosQuad;

    @ManyToOne
    @JoinColumn(name = "idtipounidadehoteleira")
    private TipoUnidadeHoteleira tipoUnidadeHoteleira;

    @Column(name = "areatotalconstruida")
    private Double areaTotalConstruida;

    @ManyToOne
    @JoinColumn(name = "idtenant")
    private Empresa empresa;

    @Column(name = "idsunidadehoteleiraintegracao", length = 250)
    private String idsUnidadeHoteleiraIntegracao;

    @ManyToOne
    @JoinColumn(name = "idrespcadastro")
    private OperadorSistema responsavelCadastro;

    @ManyToOne
    @JoinColumn(name = "idrespalteracao")
    private OperadorSistema responsavelAlteracao;

    @Column(name = "idimportacao", length = 100)
    private String idImportacao;

    @Column(name = "percentualrateiouh")
    private Double percentualRateioUh;

    @Column(name = "codigointegracaocapere", length = 100)
    private String codigoIntegracaoCapere;

    @Column(name = "enderecorua", columnDefinition = "TEXT")
    private String enderecoRua;

    @Column(name = "enderecocompleto", columnDefinition = "TEXT")
    private String enderecoCompleto;

    @Column(name = "confrontacaofrente", columnDefinition = "TEXT")
    private String confrontacaoFrente;

    @Column(name = "confrontacaofundos", columnDefinition = "TEXT")
    private String confrontacaoFundos;

    @Column(name = "confrontacaolddireito", columnDefinition = "TEXT")
    private String confrontacaoLdDireito;

    @Column(name = "confrontacaoldesquerdo", columnDefinition = "TEXT")
    private String confrontacaoLdEsquerdo;

    @Column(name = "confrontacaooutros", columnDefinition = "TEXT")
    private String confrontacaoOutros;

    @Column(name = "metragemfrente", precision = 19, scale = 5)
    private BigDecimal metragemFrente;

    @Column(name = "metragemfundos", precision = 19, scale = 5)
    private BigDecimal metragemFundos;

    @Column(name = "metragemlddireito", precision = 19, scale = 5)
    private BigDecimal metragemLdDireito;

    @Column(name = "metragemldesquerdo", precision = 19, scale = 5)
    private BigDecimal metragemLdEsquerdo;

    @Column(name = "metragemoutros", precision = 19, scale = 5)
    private BigDecimal metragemOutros;

    @Column(name = "iduhhits")
    private Short idUhHits;

    static UnidadeHoteleira novaUnidadeHoteleira(EdificioHotel edificioHotel, 
            HotelNegocio hotelNegocio, String descricao, TipoUnidadeHoteleira tipoUnidadeHoteleira,
            Empresa empresa, OperadorSistema responsavelCadastro) {
        
        UnidadeHoteleira novaUnidade = new UnidadeHoteleira();
        novaUnidade.setEdificioHotel(edificioHotel);
        novaUnidade.setHotelNegocio(hotelNegocio);
        novaUnidade.setDescricao(descricao);
        novaUnidade.setTipoUnidadeHoteleira(tipoUnidadeHoteleira);
        novaUnidade.setEmpresa(empresa);
        novaUnidade.setResponsavelCadastro(responsavelCadastro);
        
        return novaUnidade;
    }


    void alterarDados(String novaDescricao, String novaObservacao,
            TipoUnidadeHoteleira novoTipo, OperadorSistema responsavelAlteracao) {
        
        if (novaDescricao != null && !novaDescricao.trim().isEmpty()) {
            this.setDescricao(novaDescricao);
        }
        
        this.setObservacao(novaObservacao);
        
        if (novoTipo != null) {
            this.setTipoUnidadeHoteleira(novoTipo);
        }
        
        this.setResponsavelAlteracao(responsavelAlteracao);
    }


    public void configurarLocalizacao(String blocoPredio, String andarPredio,
            OperadorSistema responsavelAlteracao) {
        
        this.setBlocoPredio(blocoPredio);
        this.setAndarPredio(andarPredio);
        this.setResponsavelAlteracao(responsavelAlteracao);
    }


    public void configurarCapacidades(Integer capacidade, Integer qtdQuartos,
            Integer qtdVagasEstacionamento, OperadorSistema responsavelAlteracao) {
        
        this.setCapacidade(capacidade);
        this.setQtdQuartos(qtdQuartos);
        this.setQtdVagasEstacionamento(qtdVagasEstacionamento);
        this.setResponsavelAlteracao(responsavelAlteracao);
    }


    public void configurarAreas(Double areaTotalMetrosQuad, Double areaComumMetrosQuad,
            Double areaPrivativaMetrosQuad, Double areaTotalConstruida,
            Double areaGaragemMetrosQuad, OperadorSistema responsavelAlteracao) {
        
        this.setAreaTotalMetrosQuad(areaTotalMetrosQuad);
        this.setAreaComumMetrosQuad(areaComumMetrosQuad);
        this.setAreaPrivativaMetrosQuad(areaPrivativaMetrosQuad);
        this.setAreaTotalConstruida(areaTotalConstruida);
        this.setAreaGaragemMetrosQuad(areaGaragemMetrosQuad);
        this.setResponsavelAlteracao(responsavelAlteracao);
    }


    public void configurarFracoesPercentuais(Double fracaoIdealPorcent, Double fracaoIdealMetrosQuad,
            Double percentualTerreno, Double percentualCotaUh, Double percentualRateioUh,
            OperadorSistema responsavelAlteracao) {
        
        this.setFracaoIdealPorcent(fracaoIdealPorcent);
        this.setFracaoIdealMetrosQuad(fracaoIdealMetrosQuad);
        this.setPercentualTerreno(percentualTerreno);
        this.setPercentualCotaUh(percentualCotaUh);
        this.setPercentualRateioUh(percentualRateioUh);
        this.setResponsavelAlteracao(responsavelAlteracao);
    }


    public void configurarEndereco(String enderecoRua, String enderecoCompleto,
            OperadorSistema responsavelAlteracao) {
        
        this.setEnderecoRua(enderecoRua);
        this.setEnderecoCompleto(enderecoCompleto);
        this.setResponsavelAlteracao(responsavelAlteracao);
    }


    public void configurarConfrontacoes(String confrontacaoFrente, String confrontacaoFundos,
            String confrontacaoLdDireito, String confrontacaoLdEsquerdo, String confrontacaoOutros,
            OperadorSistema responsavelAlteracao) {
        
        this.setConfrontacaoFrente(confrontacaoFrente);
        this.setConfrontacaoFundos(confrontacaoFundos);
        this.setConfrontacaoLdDireito(confrontacaoLdDireito);
        this.setConfrontacaoLdEsquerdo(confrontacaoLdEsquerdo);
        this.setConfrontacaoOutros(confrontacaoOutros);
        this.setResponsavelAlteracao(responsavelAlteracao);
    }


    public void configurarMetragens(BigDecimal metragemFrente, BigDecimal metragemFundos,
            BigDecimal metragemLdDireito, BigDecimal metragemLdEsquerdo, BigDecimal metragemOutros,
            OperadorSistema responsavelAlteracao) {
        
        this.setMetragemFrente(metragemFrente);
        this.setMetragemFundos(metragemFundos);
        this.setMetragemLdDireito(metragemLdDireito);
        this.setMetragemLdEsquerdo(metragemLdEsquerdo);
        this.setMetragemOutros(metragemOutros);
        this.setResponsavelAlteracao(responsavelAlteracao);
    }

    public void configurarIntegracoes(String idsUnidadeHoteleiraIntegracao, 
            String codigoIntegracaoCapere, String idImportacao, Short idUhHits,
            OperadorSistema responsavelAlteracao) {
        
        this.setIdsUnidadeHoteleiraIntegracao(idsUnidadeHoteleiraIntegracao);
        this.setCodigoIntegracaoCapere(codigoIntegracaoCapere);
        this.setIdImportacao(idImportacao);
        this.setIdUhHits(idUhHits);
        this.setResponsavelAlteracao(responsavelAlteracao);
    }

}