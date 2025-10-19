package com.sw.tse.domain.model.db;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.sw.tse.core.util.GenericCryptoLocalDateConverter;
import com.sw.tse.core.util.GenericCryptoStringConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "utilizacaocontratotshospede")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class UtilizacaoContratoHospede {

    // ========== IDENTIFICAÇÃO ==========
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequtilizacaocontratotshospede")
    @SequenceGenerator(name = "sequtilizacaocontratotshospede", sequenceName = "sequtilizacaocontratotshospede", allocationSize = 1)
    @Column(name = "idutilizacaocontratotshospede")
    private Long id;

    // ========== AUDITORIA ==========
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "dataalteracao", insertable = false)
    private LocalDateTime dataAlteracao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtenant")
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrespcadastro")
    private OperadorSistema responsavelCadastro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrespalteracao")
    private OperadorSistema responsavelAlteracao;

    // ========== RELACIONAMENTO ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idutilizacaocontrato")
    private UtilizacaoContrato utilizacaoContrato;

    // ========== DADOS PESSOAIS ==========
    @Column(name = "nome", length = 250)
    private String nome;

    @Column(name = "sobrenome", length = 250)
    private String sobrenome;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "cpf", length = 120)
    private String cpf;

    @Column(name = "sexo")
    private Integer sexo;

    @Convert(converter = GenericCryptoLocalDateConverter.class)
    @Column(name = "datanascimento")
    private LocalDate dataNascimento;

    @Column(name = "idnacionalidade")
    private Integer idNacionalidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idpessoa")
    private Pessoa pessoa;

    @Column(name = "isprincipal")
    private Boolean isPrincipal;

    // ========== FAIXA ETÁRIA ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idfaixaetaria")
    private FaixaEtaria faixaEtaria;

    @Column(name = "faixaetariasigla", length = 10)
    private String faixaEtariaSigla;

    // ========== TIPO HÓSPEDE ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtipohospede")
    private TipoHospede tipoHospede;

    @Column(name = "idhospedeintegracao")
    private Long idHospedeIntegracao;

    // ========== CHECK-IN/CHECK-OUT ==========
    @Column(name = "datacheckin")
    private LocalDateTime dataCheckin;

    @Column(name = "datacheckout")
    private LocalDateTime dataCheckout;

    @Column(name = "data_checkin_real")
    private LocalDateTime dataCheckinReal;

    @Column(name = "data_checkout_real")
    private LocalDateTime dataCheckoutReal;

    // ========== INTEGRAÇÃO HOTBEACH ==========
    @Column(name = "controle_alteracao_api_hotbeach")
    private Long controleAlteracaoApiHotbeach;

    @Column(name = "pessoa_incompleta_integracao_hotbeach", nullable = false)
    private Boolean pessoaIncompletaIntegracaoHotbeach = false;

    // ========== MÉTODOS DE NEGÓCIO ==========

    /**
     * Método package para definir a utilização de contrato (usado pelo Aggregate Root)
     */
    void setUtilizacaoContrato(UtilizacaoContrato utilizacaoContrato) {
        this.utilizacaoContrato = utilizacaoContrato;
    }
    
    /**
     * Define a pessoa do hóspede
     */
    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    /**
     * Método factory para criar novo hóspede da utilização de contrato
     */
    public static UtilizacaoContratoHospede novoHospede(
            UtilizacaoContrato utilizacaoContrato,
            String nome,
            String sobrenome,
            String cpf,
            Integer sexo,
            LocalDate dataNascimento,
            FaixaEtaria faixaEtaria,
            TipoHospede tipoHospede,
            Boolean isPrincipal,
            OperadorSistema responsavelCadastro) {

        UtilizacaoContratoHospede novoHospede = new UtilizacaoContratoHospede();
        
        // Relacionamento obrigatório
        novoHospede.setUtilizacaoContrato(utilizacaoContrato);
        novoHospede.setEmpresa(utilizacaoContrato.getEmpresa());
        
        // Dados pessoais
        novoHospede.setNome(nome);
        novoHospede.setSobrenome(sobrenome);
        novoHospede.setCpf(cpf);
        novoHospede.setSexo(sexo);
        novoHospede.setDataNascimento(dataNascimento);
        
        // Nacionalidade padrão (Brasil = 30, igual Pessoa)
        novoHospede.setIdNacionalidade(30);
        
        // Faixa etária e tipo
        novoHospede.setFaixaEtaria(faixaEtaria);
        if (faixaEtaria != null) {
            novoHospede.setFaixaEtariaSigla(faixaEtaria.getSigla());
        }
        novoHospede.setTipoHospede(tipoHospede);
        
        // Configurações
        novoHospede.setIsPrincipal(isPrincipal);
        novoHospede.setResponsavelCadastro(responsavelCadastro);
        novoHospede.setControleAlteracaoApiHotbeach(0L);
        novoHospede.setPessoaIncompletaIntegracaoHotbeach(false);
        
        // Datas de check-in/out herdadas da utilização
        novoHospede.setDataCheckin(utilizacaoContrato.getDataCheckin());
        novoHospede.setDataCheckout(utilizacaoContrato.getDataCheckout());
        
        // Data de cadastro sem milissegundos
        novoHospede.setDataCadastro(LocalDateTime.now().withNano(0));
        
        return novoHospede;
    }

    /**
     * Altera dados pessoais do hóspede
     */
    public void alterarDadosPessoais(String novoNome, String novoSobrenome, String novoCpf,
            Integer novoSexo, LocalDate novaDataNascimento, OperadorSistema responsavelAlteracao) {
        
        if (novoNome != null && !novoNome.trim().isEmpty()) {
            this.setNome(novoNome);
        }
        
        if (novoSobrenome != null && !novoSobrenome.trim().isEmpty()) {
            this.setSobrenome(novoSobrenome);
        }
        
        if (novoCpf != null && !novoCpf.trim().isEmpty()) {
            this.setCpf(novoCpf);
        }
        
        if (novoSexo != null) {
            this.setSexo(novoSexo);
        }
        
        if (novaDataNascimento != null) {
            this.setDataNascimento(novaDataNascimento);
        }
        
        this.setResponsavelAlteracao(responsavelAlteracao);
        this.setDataAlteracao(LocalDateTime.now().withNano(0));
    }

    /**
     * Altera faixa etária do hóspede
     */
    public void alterarFaixaEtaria(FaixaEtaria novaFaixaEtaria, OperadorSistema responsavelAlteracao) {
        this.setFaixaEtaria(novaFaixaEtaria);
        if (novaFaixaEtaria != null) {
            this.setFaixaEtariaSigla(novaFaixaEtaria.getSigla());
        } else {
            this.setFaixaEtariaSigla(null);
        }
        this.setResponsavelAlteracao(responsavelAlteracao);
        this.setDataAlteracao(LocalDateTime.now().withNano(0));
    }

    /**
     * Altera tipo de hóspede
     */
    public void alterarTipoHospede(TipoHospede novoTipoHospede, OperadorSistema responsavelAlteracao) {
        this.setTipoHospede(novoTipoHospede);
        this.setResponsavelAlteracao(responsavelAlteracao);
        this.setDataAlteracao(LocalDateTime.now().withNano(0));
    }

    /**
     * Configura datas reais de check-in/check-out
     */
    public void configurarDatasReais(LocalDateTime dataCheckinReal, LocalDateTime dataCheckoutReal,
            OperadorSistema responsavelAlteracao) {
        this.setDataCheckinReal(dataCheckinReal);
        this.setDataCheckoutReal(dataCheckoutReal);
        this.setResponsavelAlteracao(responsavelAlteracao);
        this.setDataAlteracao(LocalDateTime.now().withNano(0));
    }

    /**
     * Configura integração com HotBeach
     */
    public void configurarIntegracaoHotbeach(Long controleAlteracao, Boolean pessoaIncompleta,
            OperadorSistema responsavelAlteracao) {
        this.setControleAlteracaoApiHotbeach(controleAlteracao);
        this.setPessoaIncompletaIntegracaoHotbeach(pessoaIncompleta);
        this.setResponsavelAlteracao(responsavelAlteracao);
        this.setDataAlteracao(LocalDateTime.now().withNano(0));
    }

    /**
     * Define se o hóspede é principal
     */
    public void definirComoPrincipal(Boolean isPrincipal, OperadorSistema responsavelAlteracao) {
        this.setIsPrincipal(isPrincipal);
        this.setResponsavelAlteracao(responsavelAlteracao);
        this.setDataAlteracao(LocalDateTime.now().withNano(0));
    }

    /**
     * Verifica se o hóspede é principal
     */
    public boolean isPrincipal() {
        return Boolean.TRUE.equals(this.isPrincipal);
    }

    /**
     * Retorna o nome completo do hóspede
     */
    public String getNomeCompleto() {
        StringBuilder nomeCompleto = new StringBuilder();
        
        if (this.nome != null && !this.nome.trim().isEmpty()) {
            nomeCompleto.append(this.nome);
        }
        
        if (this.sobrenome != null && !this.sobrenome.trim().isEmpty()) {
            if (nomeCompleto.length() > 0) {
                nomeCompleto.append(" ");
            }
            nomeCompleto.append(this.sobrenome);
        }
        
        return nomeCompleto.toString();
    }
}
