package com.sw.tse.domain.model.db;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sw.tse.core.util.GenericCryptoStringConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "carteiraboleto")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CarteiraBoleto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // SERIAL implies identity
    @Column(name = "idcarteiraboleto")
    private Long idCarteiraBoleto;

    // Campos criptografados
    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "descricao", length = 1024)
    private String descricao;

    @Column(name = "datacadastro")
    private LocalDateTime dataCadastro;

    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    @Column(name = "idcontamovbancaria")
    private Integer idContaMovBancaria;

    @Column(name = "tipoarquivoretorno", length = 10)
    private String tipoArquivoRetorno;

    @Column(name = "tipoarquivoremessa", length = 10)
    private String tipoArquivoRemessa;

    @Column(name = "minimonossonumero")
    private Long minimoNossoNumero;

    @Column(name = "maximonossonumero")
    private Long maximoNossoNumero;

    @Column(name = "nossonumeroatual")
    private Integer nossoNumeroAtual;

    @Column(name = "tipocomposicaonossonumero", length = 30)
    private String tipoComposicaoNossoNumero;

    @Column(name = "qtddigitosidentificadornossonumero")
    private Integer qtdDigitosIdentificadorNossoNumero;

    @Column(name = "codigocedente")
    private Integer codigoCedente;

    @Column(name = "digitocedente")
    private Integer digitoCedente;

    @Column(name = "numerocarteira", length = 15)
    private String numeroCarteira;

    @Column(name = "taxaemissao", precision = 19, scale = 5)
    private BigDecimal taxaEmissao;

    // Campos criptografados do cedente
    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "descricaocedente", length = 80)
    private String descricaoCedente;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "cpfoucnpjcedente", length = 120)
    private String cpfOuCnpjCedente;

    // Campos criptografados do endereço
    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "enderecocedente", length = 512)
    private String enderecoCedente;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "numeroenderecocedente", length = 128)
    private String numeroEnderecoCedente;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "complementoenderecocedente", length = 512)
    private String complementoEnderecoCedente;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "bairroenderecocedente", length = 512)
    private String bairroEnderecoCedente;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "cependerecocedente", length = 128)
    private String cepEnderecoCedente;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "cidadeenderecocedente", length = 120)
    private String cidadeEnderecoCedente;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "ufenderecocedente", length = 128)
    private String ufEnderecoCedente;

    @Column(name = "carteiraativa")
    private Boolean carteiraAtiva;

    @Column(name = "numeroconvenio", length = 64)
    private String numeroConvenio;

    @Column(name = "modalidade", length = 15)
    private String modalidade;

    // Campos importantes para cálculo de juros e multa
    @Column(name = "valorjurosdemora", precision = 15, scale = 4)
    private BigDecimal valorJurosDeMora;

    @Column(name = "valormulta", precision = 15, scale = 2)
    private BigDecimal valorMulta;

    @Column(name = "qtddiasdevolveraposvencimento")
    private Integer qtdDiasDevolverAposVencimento;

    @Column(name = "qtddiasnaoreceberaposvencimento")
    private Integer qtdDiasNaoReceberAposVencimento;

    @Column(name = "qtddiasprotestaraposvencimento")
    private Integer qtdDiasProtestarAposVencimento;

    // Mensagens
    @Column(name = "mensagem1", length = 250)
    private String mensagem1;

    @Column(name = "mensagem2", length = 250)
    private String mensagem2;

    @Column(name = "mensagem3", length = 250)
    private String mensagem3;

    @Column(name = "mensagem4", length = 250)
    private String mensagem4;

    @Column(name = "mensagem5", length = 250)
    private String mensagem5;

    @Column(name = "mensagem6", length = 250)
    private String mensagem6;

    @Column(name = "exibeparaemissaonoback")
    private Boolean exibeParaEmissaoNoBack;

    @Column(name = "exibeparaemissaonofront")
    private Boolean exibeParaEmissaoNoFront;

    @Column(name = "modeloboleto", length = 100)
    private String modeloBoleto;

    @Column(name = "codigotransmissao", length = 100)
    private String codigoTransmissao;

    @Column(name = "sequenciaremessa")
    private Integer sequenciaRemessa;

    @Column(name = "bancoemiteboleto")
    private Boolean bancoEmiteBoleto;

    @Column(name = "selocorreios")
    private byte[] seloCorreios;

    @Column(name = "validarenderecoremessa")
    private Boolean validarEnderecoRemessa;

    @Column(name = "quantidadediascredito")
    private Integer quantidadeDiasCredito;

    @Column(name = "valordescontoinstrucao1", precision = 15, scale = 2)
    private BigDecimal valorDescontoInstrucao1;

    @Column(name = "qtddiasdescontoinstrucao1")
    private Integer qtdDiasDescontoInstrucao1;

    @Column(name = "tipodescontoinstrucao1", length = 1)
    private String tipoDescontoInstrucao1;

    @Column(name = "redasset")
    private Boolean redasSet;

    @Column(name = "idrespcadastro")
    private Integer idRespCadastro;

    @Column(name = "idrespalteracao")
    private Integer idRespAlteracao;

    @Column(name = "tipodescricaonossonumero")
    private String tipoDescricaoNossoNumero;

    @Column(name = "nossonumeroatualpagamento")
    private Integer nossoNumeroAtualPagamento;

    @Column(name = "maximonossonumeropagamento")
    private Integer maximoNossoNumeroPagamento;

    @Column(name = "minimonossonumeropagamento")
    private Integer minimoNossoNumeroPagamento;

    @Column(name = "pagamentohabilitado")
    private Boolean pagamentoHabilitado;

    @Column(name = "codigofinalidadeted")
    private String codigoFinalidadeTed;

    @Column(name = "codigofinalidadedoc")
    private String codigoFinalidadeDoc;

    @Column(name = "codigocamaracentralizadora")
    private String codigoCamaraCentralizadora;

    @Column(name = "tiposervico")
    private String tipoServico;

    @Column(name = "teste")
    private Boolean teste;

    @Column(name = "formalancamento")
    private String formaLancamento;

    @Column(name = "formapagamento")
    private String formaPagamento;

    @Column(name = "operacao")
    private String operacao;

    @Column(name = "numeroconveniopagamento", length = 64)
    private String numeroConvenioPagamento;

    // Campos criptografados do avalista
    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "sacadoavalista", length = 512)
    private String sacadoAvalista;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "cpfoucnpjsacadoavalista", length = 512)
    private String cpfOuCnpjSacadoAvalista;

    @Column(name = "gerarboletopix")
    private Boolean gerarBoletoPix;

    // Campos de integração
    @Column(name = "isativobarramentoaviva")
    private Boolean isAtivoBarramentoAviva;

    @Column(name = "ishomologacaobarramentoaviva")
    private Boolean isHomologacaoBarramentoAviva;

    @Column(name = "urlproducaobarramentoaviva")
    private String urlProducaoBarramentoAviva;

    @Column(name = "urlhomologacaobarramentoaviva")
    private String urlHomologacaoBarramentoAviva;

    @Column(name = "clientkeybarramentoaviva")
    private String clientKeyBarramentoAviva;

    @Column(name = "clientsecretbarramentoaviva")
    private String clientSecretBarramentoAviva;

    @Column(name = "isativoimobanco")
    private Boolean isAtivoImobanco;

    @Column(name = "ishomologacaoimobanco")
    private Boolean isHomologacaoImobanco;

    @Column(name = "urlapiimobancohomologacao")
    private String urlApiImobancoHomologacao;

    @Column(name = "urlapiimobancoproducao")
    private String urlApiImobancoProducao;

    @Column(name = "loginapiimobanco")
    private String loginApiImobanco;

    @Column(name = "senhaapiimobanco")
    private String senhaApiImobanco;

    @Column(name = "clientimobanco")
    private String clientImobanco;

    @Column(name = "tokenimobanco")
    private String tokenImobanco;

    @Column(name = "walletimobanco")
    private String walletImobanco;

    @Column(name = "configuracaoapisicredijson")
    private String configuracaoApiSicrediJson;

    /*@Column(name = "configuracaoapiitaujson")
    private String configuracaoApiItauJson;*/

    // Relacionamentos
    @ManyToOne()
    @JoinColumn(name = "idtenant")
    private Empresa tenant;
}
