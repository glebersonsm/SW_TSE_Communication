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
@Table(name = "meiopagamento")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class MeioPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idmeiopagamento")
    private Long idMeioPagamento;

    @Column(name = "codmeiopagamento", length = 30)
    private String codMeioPagamento;

    @Column(name = "descricao", length = 80)
    private String descricao;

    @Column(name = "utilizadorecebimento")
    private Boolean utilizadoRecebimento;

    @Column(name = "utilizandopagamento")
    private Boolean utilizandoPagamento;

    @Column(name = "idplanofinanceirocancelamento")
    private Integer idPlanoFinanceiroCancelamento;

    @Column(name = "idplanofinanceirocancelamentojuros")
    private Integer idPlanoFinanceiroCancelamentoJuros;

    @Column(name = "idplanofinanceirovenda")
    private Integer idPlanoFinanceiroVenda;

    @Column(name = "idplanofinanceirovendafinanciamento")
    private Integer idPlanoFinanceiroVendaFinanciamento;

    @CreationTimestamp
    @Column(name = "datacadastro", insertable = true, updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao", insertable = false, updatable = true)
    private LocalDateTime dataAlteracao;

    @Column(name = "utilizadoparalinkpagamento")
    private Boolean utilizadoParaLinkPagamento;

    @Column(name = "enviarlinkcadcartaorecorrente", nullable = false)
    private Boolean enviarLinkCadCartaoRecorrente = false;

    @Column(name = "meiopagamentouau", length = 100)
    private String meioPagamentoUau;

    @Column(name = "idintegracaosienge", columnDefinition = "TEXT")
    private String idIntegracaoSienge;
}
