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
@Table(name = "edificiohotel")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class EdificioHotel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqedificiohotel")
    @SequenceGenerator(name = "seqedificiohotel", sequenceName = "seqedificiohotel", allocationSize = 1)
    @Column(name = "idedificiohotel")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idhotelnegocio")
    private HotelNegocio hotel;

    @Column(name = "descricao", length = 80)
    private String descricao;

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
    @JoinColumn(name = "idtenant")
    private Empresa empresa;

    @Column(name = "idimportacao", length = 100)
    private String idImportacao;

    @Column(name = "codigoobrauau", length = 250)
    private String codigoObraUau;

    @Column(name = "obraconcluida")
    private Boolean obraConcluida;

    @Column(name = "codigoobrasienge", columnDefinition = "TEXT")
    private String codigoObraSienge;

    @Column(name = "idempreendimento")
    private Integer idEmpreendimento;

   static EdificioHotel novoEdificioHotel(HotelNegocio hotelNegocio, String descricao,
            Empresa empresa, OperadorSistema responsavelCadastro) {
        
        EdificioHotel novoEdificio = new EdificioHotel();
        novoEdificio.setHotel(hotelNegocio);
        novoEdificio.setDescricao(descricao);
        novoEdificio.setEmpresa(empresa);
        novoEdificio.setResponsavelCadastro(responsavelCadastro);
        
        return novoEdificio;
    }

    public void alterarDados(String novaDescricao, HotelNegocio novoHotelNegocio,
            OperadorSistema responsavelAlteracao) {
        
        if (novaDescricao != null && !novaDescricao.trim().isEmpty()) {
            this.setDescricao(novaDescricao);
        }
        
        if (novoHotelNegocio != null) {
            this.setHotel(novoHotelNegocio);
        }
        
        this.setResponsavelAlteracao(responsavelAlteracao);
    }


    public void configurarObra(String codigoObraUau, Boolean obraConcluida, 
            String codigoObraSienge, Integer idEmpreendimento,
            OperadorSistema responsavelAlteracao) {
        
        this.setCodigoObraUau(codigoObraUau);
        this.setObraConcluida(obraConcluida);
        this.setCodigoObraSienge(codigoObraSienge);
        this.setIdEmpreendimento(idEmpreendimento);
        this.setResponsavelAlteracao(responsavelAlteracao);
    }


    public void configurarImportacao(String idImportacao, OperadorSistema responsavelAlteracao) {
        this.setIdImportacao(idImportacao);
        this.setResponsavelAlteracao(responsavelAlteracao);
    }


    public void concluirObra(OperadorSistema responsavelAlteracao) {
        this.setObraConcluida(true);
        this.setResponsavelAlteracao(responsavelAlteracao);
    }

    public void marcarObraEmAndamento(OperadorSistema responsavelAlteracao) {
        this.setObraConcluida(false);
        this.setResponsavelAlteracao(responsavelAlteracao);
    }
}