package com.sw.tse.domain.model.db;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.sw.tse.domain.expection.PeriodoModeloCotaNullException;
import com.sw.tse.domain.expection.TipoUtilizacaoContratoInvalidoException;
import com.sw.tse.domain.expection.TipoUtilizacaoContratoNullException;
import com.sw.tse.domain.expection.UsuarioResponsavelNullException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "utilizacaocontrato")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class UtilizacaoContrato {

    // ========== IDENTIFICAÇÃO ==========
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequtilizacaocontrato")
    @SequenceGenerator(name = "sequtilizacaocontrato", sequenceName = "sequtilizacaocontrato", allocationSize = 1)
    @Column(name = "idutilizacaocontrato")
    private Long id;

    @Column(name = "utilizacaocontratoguid")
    private UUID utilizacaoContratoGuid;

    @Column(name = "nroreserva")
    private Long nroReserva;

    // ========== AUDITORIA ==========
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "dataalteracao", insertable = false)
    private LocalDateTime dataAlteracao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrespcadastro")
    private OperadorSistema responsavelCadastro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrespalteracao")
    private OperadorSistema responsavelAlteracao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtenant")
    private Empresa empresa;

    @Column(name = "idimportacao", columnDefinition = "TEXT")
    private String idImportacao;

    // ========== RELACIONAMENTOS CONTRATO/HOTEL ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcontrato")
    private Contrato contrato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcontratointercambio")
    private ContratoIntercambio contratoIntercambio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idhotelreserva")
    private HotelNegocio hotelReserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idunidadehoteleira")
    private UnidadeHoteleira unidadeHoteleira;

    @Column(name = "idreservaintegracao")
    private Long idReservaIntegracao;

    @Column(name = "idreservaconjugadaintegracao")
    private Integer idReservaConjugadaIntegracao;

    // ========== SOLICITAÇÃO ==========
    @Column(name = "datasolicitacao")
    private LocalDateTime dataSolicitacao;

    @Column(name = "nomesolicitante", length = 80)
    private String nomeSolicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrespsolicitacao")
    private OperadorSistema responsavelSolicitacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idpessoasolicitante")
    private Pessoa pessoaSolicitante;

    // ========== CHECK-IN/CHECK-OUT ==========
    @Column(name = "datacheckin")
    private LocalDateTime dataCheckin;

    @Column(name = "datacheckout")
    private LocalDateTime dataCheckout;

    // ========== CONFIRMAÇÃO ==========
    @Column(name = "utilizacaoconfirmada")
    private Boolean utilizacaoConfirmada;

    @Column(name = "dataconfirmacao")
    private LocalDateTime dataConfirmacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrespconfirmacao")
    private OperadorSistema responsavelConfirmacao;

    // ========== CANCELAMENTO ==========
    @Column(name = "datacancelamento")
    private LocalDateTime dataCancelamento;

    @Column(name = "motivocancelamento", length = 250)
    private String motivoCancelamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrespcancelamento")
    private OperadorSistema responsavelCancelamento;

    // ========== TIPO E CLASSIFICAÇÃO ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtipoutilizacaocontrato")
    private TipoUtilizacaoContrato tipoUtilizacaoContrato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtipoperiodoutilizacao")
    private TipoPeriodoUtilizacao tipoPeriodoUtilizacao;

    @Column(name = "semanadoano")
    private Integer semanaDoAno;

    // ========== OCUPAÇÃO ==========
    @Column(name = "qtdpagantes")
    private Integer qtdPagantes;

    @Column(name = "qtdadultos")
    private Integer qtdAdultos;

    @Column(name = "qtdcriancas")
    private Integer qtdCriancas;

    @Column(name = "idtipouhtarifas")
    private Long idTipoUhTarifas;

    // ========== VALORES ==========
    @Column(name = "valordiariaacordo", precision = 19, scale = 2)
    private BigDecimal valorDiariaAcordo;

    @Column(name = "valortotaldiarias", precision = 19, scale = 2)
    private BigDecimal valorTotalDiarias;

    // ========== STATUS ==========
    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "observacao", length = 5000)
    private String observacao;

    // ========== MULTIPROPRIEDADE ==========
    @Column(name = "multipropriedade")
    private Boolean multipropriedade;

    // ========== PERÍODO MODELO COTA ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idperiodomodelocota")
    private PeriodoModeloCota periodoModeloCota;

    // ========== INTEGRAÇÃO CAPERE ==========
    @Column(name = "enviadocapere")
    private Boolean enviadoCapere;

    @Column(name = "confirmadocapere")
    private Boolean confirmadoCapere;

    @Column(name = "canceladocapere")
    private Boolean canceladoCapere;

    @Column(name = "datahoraintegracaocapere")
    private LocalDateTime dataHoraIntegracaoCapere;

    @Column(name = "datahoracancelamentocapere")
    private LocalDateTime dataHoraCancelamentoCapere;

    // ========== CAMPOS ADICIONAIS ==========
    @Column(name = "cortesia")
    private Boolean cortesia;

    @Column(name = "idutilizcontratotstipopensao")
    private Long idUtilizacaoContratoTsTipoPensao;

    @Column(name = "valortotalpensao", precision = 19, scale = 2)
    private BigDecimal valorTotalPensao;

    @Column(name = "precheckin")
    private Boolean preCheckin;

    @Column(name = "entroufilaespera")
    private Boolean entrouFilaEspera;

    @Column(name = "voucherenviadoautomaticamente")
    private Boolean voucherEnviadoAutomaticamente;

    // ========== AGREGADOS ==========
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "utilizacaoContrato", fetch = FetchType.LAZY)
    private List<UtilizacaoContratoHospede> hospedes = new ArrayList<>();

    // ========== MÉTODOS DE NEGÓCIO ==========

    /**
     * Método factory para criar nova utilização de contrato
     * Aplicando as regras de negócio definidas
     */
    public static UtilizacaoContrato novaUtilizacaoContrato(
            PeriodoModeloCota periodoModeloCota,
            Contrato contrato,
            PeriodoUtilizacao periodoUtilizacao,
            OperadorSistema responsavelCadastro,
            Pessoa pessoaSolicitante) {

        UtilizacaoContrato novaUtilizacao = new UtilizacaoContrato();
        
        // Parâmetros obrigatórios
        novaUtilizacao.setPeriodoModeloCota(periodoModeloCota);
        novaUtilizacao.setContrato(contrato);
        novaUtilizacao.setResponsavelCadastro(responsavelCadastro);
        novaUtilizacao.setPessoaSolicitante(pessoaSolicitante);
        
        // Campos derivados automaticamente
        novaUtilizacao.setUnidadeHoteleira(contrato.getCotaUh().getUnidadeHoteleira());
        novaUtilizacao.setEmpresa(contrato.getEmpresa());
        
        // Datas derivadas do período
        novaUtilizacao.setDataCheckin(LocalDateTime.of(
            periodoUtilizacao.getAnoInicio(),
            periodoUtilizacao.getMesInicio(),
            periodoUtilizacao.getDiaInicio(),
            14, 0  // 14:00
        ));
        
        novaUtilizacao.setDataCheckout(LocalDateTime.of(
            periodoUtilizacao.getAnoFim(),
            periodoUtilizacao.getMesFim(),
            periodoUtilizacao.getDiaFim(),
            12, 0  // 12:00
        ));
        
        // Valores iniciais padrão
        novaUtilizacao.setNroReserva(0L);
        novaUtilizacao.setUtilizacaoConfirmada(false);
        novaUtilizacao.setStatus("ATIVO");
        novaUtilizacao.setMultipropriedade(true);
        novaUtilizacao.setSemanaDoAno(0);
        novaUtilizacao.setUtilizacaoContratoGuid(UUID.randomUUID());
        
        return novaUtilizacao;
    }

    /**
     * Adiciona um hóspede à utilização do contrato
     */
    public void adicionarHospede(UtilizacaoContratoHospede hospede) {
        if (hospede != null) {
            hospede.setUtilizacaoContrato(this);
            this.hospedes.add(hospede);
        }
    }

    /**
     * Remove um hóspede da utilização do contrato
     */
    public void removerHospede(UtilizacaoContratoHospede hospede) {
        if (hospede != null) {
            this.hospedes.remove(hospede);
            hospede.setUtilizacaoContrato(null);
        }
    }

    /**
     * Retorna lista imutável de hóspedes
     */
    public List<UtilizacaoContratoHospede> getHospedes() {
        return Collections.unmodifiableList(this.hospedes != null ? this.hospedes : Collections.emptyList());
    }

    /**
     * Confirma a utilização do contrato
     */
    public void confirmarUtilizacao(OperadorSistema responsavelConfirmacao) {
        this.setUtilizacaoConfirmada(true);
        this.setDataConfirmacao(LocalDateTime.now().withNano(0));
        this.setResponsavelConfirmacao(responsavelConfirmacao);
        this.setDataAlteracao(LocalDateTime.now().withNano(0));
    }

    /**
     * Cancela a utilização do contrato
     */
    public void cancelarUtilizacao(String motivoCancelamento, OperadorSistema responsavelCancelamento) {
        this.setDataCancelamento(LocalDateTime.now().withNano(0));
        this.setMotivoCancelamento(motivoCancelamento);
        this.setResponsavelCancelamento(responsavelCancelamento);
        this.setResponsavelAlteracao(responsavelCancelamento);
        this.setStatus("CANCELADO");
        this.setDataAlteracao(LocalDateTime.now().withNano(0));
    }

    /**
     * Altera dados básicos da utilização
     */
    public void alterarDados(String novaObservacao, OperadorSistema responsavelAlteracao) {
        if (novaObservacao != null && !novaObservacao.trim().isEmpty()) {
            this.setObservacao(novaObservacao);
        }
        this.setResponsavelAlteracao(responsavelAlteracao);
        this.setDataAlteracao(LocalDateTime.now().withNano(0));
    }

    /**
     * Configura dados de ocupação
     */
    public void configurarOcupacao(Integer qtdPagantes, Integer qtdAdultos, Integer qtdCriancas, 
            OperadorSistema responsavelAlteracao) {
        this.setQtdPagantes(qtdPagantes);
        this.setQtdAdultos(qtdAdultos);
        this.setQtdCriancas(qtdCriancas);
        this.setResponsavelAlteracao(responsavelAlteracao);
        this.setDataAlteracao(LocalDateTime.now().withNano(0));
    }

    /**
     * Configura valores da utilização
     */
    public void configurarValores(BigDecimal valorDiariaAcordo, BigDecimal valorTotalDiarias,
            OperadorSistema responsavelAlteracao) {
        this.setValorDiariaAcordo(valorDiariaAcordo);
        this.setValorTotalDiarias(valorTotalDiarias);
        this.setResponsavelAlteracao(responsavelAlteracao);
        this.setDataAlteracao(LocalDateTime.now().withNano(0));
    }

    /**
     * Configura integração com Capere
     */
    public void configurarIntegracaoCapere(Boolean enviado, Boolean confirmado, Boolean cancelado,
            OperadorSistema responsavelAlteracao) {
        this.setEnviadoCapere(enviado);
        this.setConfirmadoCapere(confirmado);
        this.setCanceladoCapere(cancelado);
        this.setResponsavelAlteracao(responsavelAlteracao);
        this.setDataAlteracao(LocalDateTime.now().withNano(0));
    }

    /**
     * Verifica se a utilização está ativa
     */
    public boolean isAtiva() {
        return "ATIVO".equals(this.status);
    }

    /**
     * Verifica se a utilização está confirmada
     */
    public boolean isConfirmada() {
        return Boolean.TRUE.equals(this.utilizacaoConfirmada);
    }

    /**
     * Verifica se a utilização está cancelada
     */
    public boolean isCancelada() {
        return "CANCELADO".equals(this.status);
    }

    /**
     * Método factory simplificado para criar nova utilização de contrato do tipo RESERVA
     * Apenas cria a estrutura básica da utilização, sem processar hóspedes
     */
    public static UtilizacaoContrato criarUtilizacaoContratoReserva(
            PeriodoModeloCota periodoModeloCota,
            OperadorSistema usuarioResponsavel,
            TipoUtilizacaoContrato tipoUtilizacaoContrato) {

        // Validações obrigatórias
        if (periodoModeloCota == null) {
            throw new PeriodoModeloCotaNullException();
        }
        if (usuarioResponsavel == null) {
            throw new UsuarioResponsavelNullException();
        }
        if (tipoUtilizacaoContrato == null) {
            throw new TipoUtilizacaoContratoNullException();
        }
        if (!"RESERVA".equals(tipoUtilizacaoContrato.getSigla())) {
            throw new TipoUtilizacaoContratoInvalidoException(tipoUtilizacaoContrato.getSigla());
        }

        UtilizacaoContrato novaUtilizacao = new UtilizacaoContrato();
        
        // Parâmetros obrigatórios
        novaUtilizacao.setPeriodoModeloCota(periodoModeloCota);
        novaUtilizacao.setContrato(periodoModeloCota.getContrato());
        novaUtilizacao.setResponsavelCadastro(usuarioResponsavel);
        novaUtilizacao.setResponsavelSolicitacao(usuarioResponsavel);
        novaUtilizacao.setTipoUtilizacaoContrato(tipoUtilizacaoContrato);
        
        // Campos derivados automaticamente
        novaUtilizacao.setUnidadeHoteleira(periodoModeloCota.getUnidadeHoteleira());
        novaUtilizacao.setEmpresa(periodoModeloCota.getEmpresa());
        novaUtilizacao.setHotelReserva(periodoModeloCota.getUnidadeHoteleira().getEdificioHotel().getHotel());
        
        // Pessoa solicitante
        if (usuarioResponsavel.getPessoa() != null) {
            novaUtilizacao.setPessoaSolicitante(usuarioResponsavel.getPessoa());
            novaUtilizacao.setNomeSolicitante(usuarioResponsavel.getPessoa().getNome());
        }
        
        // Datas derivadas do período
        PeriodoUtilizacao periodoUtilizacao = periodoModeloCota.getPeriodoUtilizacao();
        if (periodoUtilizacao != null) {
            novaUtilizacao.setDataCheckin(LocalDateTime.of(
                periodoUtilizacao.getAnoInicio(),
                periodoUtilizacao.getMesInicio(),
                periodoUtilizacao.getDiaInicio(),
                0, 0  // 00:00
            ));
            
            novaUtilizacao.setDataCheckout(LocalDateTime.of(
                periodoUtilizacao.getAnoFim(),
                periodoUtilizacao.getMesFim(),
                periodoUtilizacao.getDiaFim(),
                0, 0  // 00:00
            ));
            
            novaUtilizacao.setTipoPeriodoUtilizacao(periodoUtilizacao.getTipoPeriodoUtilizacao());
        }
        
        // Valores iniciais padrão
        novaUtilizacao.setNroReserva(0L);
        novaUtilizacao.setUtilizacaoConfirmada(false);
        novaUtilizacao.setStatus("ATIVO");
        novaUtilizacao.setMultipropriedade(true);
        novaUtilizacao.setSemanaDoAno(0);
        novaUtilizacao.setUtilizacaoContratoGuid(UUID.randomUUID());
        
        // Inicializar datas sem milissegundos
        LocalDateTime agora = LocalDateTime.now().withNano(0);
        novaUtilizacao.setDataCadastro(agora);
        novaUtilizacao.setDataSolicitacao(agora);
        
        // Inicializar novos campos
        novaUtilizacao.setCortesia(false);
        novaUtilizacao.setPreCheckin(false);
        novaUtilizacao.setEntrouFilaEspera(false);
        novaUtilizacao.setVoucherEnviadoAutomaticamente(false);
        novaUtilizacao.setValorTotalPensao(BigDecimal.ZERO);
        
        return novaUtilizacao;
    }
    
    /**
     * Define os quantitativos de hóspedes por faixa etária
     */
    public void setQuantitativosHospedes(int qtdAdultos, int qtdCriancas) {
        this.setQtdAdultos(qtdAdultos);
        this.setQtdCriancas(qtdCriancas);
    }
    
    /**
     * Define a quantidade de pagantes
     */
    public void definirQtdPagantes(int qtdPagantes) {
        this.setQtdPagantes(qtdPagantes);
    }
    
    /**
     * Define o ID da pensão
     */
    public void definirIdUtilizacaoContratoTsTipoPensao(Long idPensao) {
        this.setIdUtilizacaoContratoTsTipoPensao(idPensao);
    }
    
    /**
     * Define o contrato de intercâmbio
     */
    public void setContratoIntercambio(ContratoIntercambio contratoIntercambio) {
        this.contratoIntercambio = contratoIntercambio;
    }
    
    /**
     * Método factory para criar nova utilização de contrato do tipo RCI
     * Aceita sigla DEPSEMANA (sigla do banco de dados TSE para depósito de semana RCI)
     */
    public static UtilizacaoContrato criarUtilizacaoContratoRci(
            PeriodoModeloCota periodoModeloCota,
            OperadorSistema usuarioResponsavel,
            TipoUtilizacaoContrato tipoUtilizacaoContrato) {

        // Validações obrigatórias
        if (periodoModeloCota == null) {
            throw new PeriodoModeloCotaNullException();
        }
        if (usuarioResponsavel == null) {
            throw new UsuarioResponsavelNullException();
        }
        if (tipoUtilizacaoContrato == null) {
            throw new TipoUtilizacaoContratoNullException();
        }
        if (!"DEPSEMANA".equals(tipoUtilizacaoContrato.getSigla())) {
            throw new TipoUtilizacaoContratoInvalidoException(
                String.format("TipoUtilizacaoContrato deve ter sigla 'DEPSEMANA', mas foi informado '%s'", 
                    tipoUtilizacaoContrato.getSigla())
            );
        }

        UtilizacaoContrato novaUtilizacao = new UtilizacaoContrato();
        
        // Parâmetros obrigatórios
        novaUtilizacao.setPeriodoModeloCota(periodoModeloCota);
        novaUtilizacao.setContrato(periodoModeloCota.getContrato());
        novaUtilizacao.setResponsavelCadastro(usuarioResponsavel);
        novaUtilizacao.setResponsavelSolicitacao(usuarioResponsavel);
        novaUtilizacao.setTipoUtilizacaoContrato(tipoUtilizacaoContrato);
        
        // Campos derivados automaticamente
        novaUtilizacao.setUnidadeHoteleira(periodoModeloCota.getUnidadeHoteleira());
        novaUtilizacao.setEmpresa(periodoModeloCota.getEmpresa());
        novaUtilizacao.setHotelReserva(periodoModeloCota.getUnidadeHoteleira().getEdificioHotel().getHotel());
        
        // Pessoa solicitante
        if (usuarioResponsavel.getPessoa() != null) {
            novaUtilizacao.setPessoaSolicitante(usuarioResponsavel.getPessoa());
            novaUtilizacao.setNomeSolicitante(usuarioResponsavel.getPessoa().getNome());
        }
        
        // Datas derivadas do período
        PeriodoUtilizacao periodoUtilizacao = periodoModeloCota.getPeriodoUtilizacao();
        if (periodoUtilizacao != null) {
            novaUtilizacao.setDataCheckin(LocalDateTime.of(
                periodoUtilizacao.getAnoInicio(),
                periodoUtilizacao.getMesInicio(),
                periodoUtilizacao.getDiaInicio(),
                0, 0  // 00:00
            ));
            
            novaUtilizacao.setDataCheckout(LocalDateTime.of(
                periodoUtilizacao.getAnoFim(),
                periodoUtilizacao.getMesFim(),
                periodoUtilizacao.getDiaFim(),
                0, 0  // 00:00
            ));
            
            novaUtilizacao.setTipoPeriodoUtilizacao(periodoUtilizacao.getTipoPeriodoUtilizacao());
        }
        
        // Valores iniciais padrão
        novaUtilizacao.setNroReserva(0L);
        novaUtilizacao.setUtilizacaoConfirmada(false);
        novaUtilizacao.setStatus("ATIVO");
        novaUtilizacao.setMultipropriedade(true);
        novaUtilizacao.setSemanaDoAno(0);
        novaUtilizacao.setUtilizacaoContratoGuid(UUID.randomUUID());
        
        // Inicializar datas sem milissegundos
        LocalDateTime agora = LocalDateTime.now().withNano(0);
        novaUtilizacao.setDataCadastro(agora);
        novaUtilizacao.setDataSolicitacao(agora);
        
        // Inicializar novos campos
        novaUtilizacao.setCortesia(false);
        novaUtilizacao.setPreCheckin(false);
        novaUtilizacao.setEntrouFilaEspera(false);
        novaUtilizacao.setVoucherEnviadoAutomaticamente(false);
        novaUtilizacao.setValorTotalPensao(BigDecimal.ZERO);
        
        return novaUtilizacao;
    }
    
    /**
     * Método factory para criar nova utilização de contrato do tipo POOL
     * Aceita sigla DEPPOOL (sigla do banco de dados TSE para depósito pool)
     * POOL não possui hóspedes - a utilização é disponibilizada para o hotel comercializar
     */
    public static UtilizacaoContrato criarUtilizacaoContratoPool(
            PeriodoModeloCota periodoModeloCota,
            OperadorSistema usuarioResponsavel,
            TipoUtilizacaoContrato tipoUtilizacaoContrato) {

        // Validações obrigatórias
        if (periodoModeloCota == null) {
            throw new PeriodoModeloCotaNullException();
        }
        if (usuarioResponsavel == null) {
            throw new UsuarioResponsavelNullException();
        }
        if (tipoUtilizacaoContrato == null) {
            throw new TipoUtilizacaoContratoNullException();
        }
        if (!"DEPPOOL".equals(tipoUtilizacaoContrato.getSigla())) {
            throw new TipoUtilizacaoContratoInvalidoException(
                String.format("TipoUtilizacaoContrato deve ter sigla 'DEPPOOL', mas foi informado '%s'", 
                    tipoUtilizacaoContrato.getSigla())
            );
        }

        UtilizacaoContrato novaUtilizacao = new UtilizacaoContrato();
        
        // Parâmetros obrigatórios
        novaUtilizacao.setPeriodoModeloCota(periodoModeloCota);
        novaUtilizacao.setContrato(periodoModeloCota.getContrato());
        novaUtilizacao.setResponsavelCadastro(usuarioResponsavel);
        novaUtilizacao.setResponsavelSolicitacao(usuarioResponsavel);
        novaUtilizacao.setTipoUtilizacaoContrato(tipoUtilizacaoContrato);
        
        // Campos derivados automaticamente
        novaUtilizacao.setUnidadeHoteleira(periodoModeloCota.getUnidadeHoteleira());
        novaUtilizacao.setEmpresa(periodoModeloCota.getEmpresa());
        novaUtilizacao.setHotelReserva(periodoModeloCota.getUnidadeHoteleira().getEdificioHotel().getHotel());
        
        // Pessoa solicitante
        if (usuarioResponsavel.getPessoa() != null) {
            novaUtilizacao.setPessoaSolicitante(usuarioResponsavel.getPessoa());
            novaUtilizacao.setNomeSolicitante(usuarioResponsavel.getPessoa().getNome());
        }
        
        // Datas derivadas do período
        PeriodoUtilizacao periodoUtilizacao = periodoModeloCota.getPeriodoUtilizacao();
        if (periodoUtilizacao != null) {
            novaUtilizacao.setDataCheckin(LocalDateTime.of(
                periodoUtilizacao.getAnoInicio(),
                periodoUtilizacao.getMesInicio(),
                periodoUtilizacao.getDiaInicio(),
                0, 0  // 00:00
            ));
            
            novaUtilizacao.setDataCheckout(LocalDateTime.of(
                periodoUtilizacao.getAnoFim(),
                periodoUtilizacao.getMesFim(),
                periodoUtilizacao.getDiaFim(),
                0, 0  // 00:00
            ));
            
            novaUtilizacao.setTipoPeriodoUtilizacao(periodoUtilizacao.getTipoPeriodoUtilizacao());
        }
        
        // Valores iniciais padrão
        novaUtilizacao.setNroReserva(0L);
        novaUtilizacao.setUtilizacaoConfirmada(false);
        novaUtilizacao.setStatus("ATIVO");
        novaUtilizacao.setMultipropriedade(true);
        novaUtilizacao.setSemanaDoAno(0);
        novaUtilizacao.setUtilizacaoContratoGuid(UUID.randomUUID());
        
        // Inicializar datas sem milissegundos
        LocalDateTime agora = LocalDateTime.now().withNano(0);
        novaUtilizacao.setDataCadastro(agora);
        novaUtilizacao.setDataSolicitacao(agora);
        
        // Inicializar novos campos
        novaUtilizacao.setCortesia(false);
        novaUtilizacao.setPreCheckin(false);
        novaUtilizacao.setEntrouFilaEspera(false);
        novaUtilizacao.setVoucherEnviadoAutomaticamente(false);
        novaUtilizacao.setValorTotalPensao(BigDecimal.ZERO);
        
        return novaUtilizacao;
    }
}
