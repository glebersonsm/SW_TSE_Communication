package com.sw.tse.domain.model.db;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.StringUtils;

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
@Table(name = "contatotelefonico")
@Setter(value =  AccessLevel.PRIVATE)
@Getter()
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ContatoTelefonico {

 	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqcontatotelefonico")
    @SequenceGenerator(name = "seqcontatotelefonico", sequenceName = "seqcontatotelefonico", allocationSize = 1)
 	@Column(name = "idcontatotelefonico")
	private Long id;
 	
    @Column(name = "descricaocontato", length = 30)
    private String descricaoContato;

    @CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao", insertable =  false)
    private LocalDateTime dataAlteracao;

    @Column(name = "tipocontatotelefonico")
    private Integer tipoContatoTelefonico;

    @Column(name = "ddi", length = 128)
    private String ddi;

    @Column(name = "ddd", length = 128)
    private String ddd;

    @Column(name = "numero", length = 128)
    private String numero;

    @Column(name = "ramal", length = 128)
    private String ramal;

    @Column(name = "contemautorizawhatsapp")
    private boolean whatsApp;

    @Column(name = "idimportacao", columnDefinition = "TEXT")
    private String idImportacao;

    @Column(name = "foifeitoprimeirocontatowhatsapp")
    private boolean foiFeitoPrimeiroContatoWhatsapp;

    @ManyToOne()
    @JoinColumn(name = "idpessoa")
    private Pessoa pessoa;

    @ManyToOne()
    @JoinColumn(name = "idrespcadastro")
    private OperadorSistema responsavelCadastro;

    @ManyToOne()
    @JoinColumn(name = "idrespalteracao")
    private OperadorSistema responsavelAlteracao;
    
    
    static ContatoTelefonico novoContatoTelefonico(String descricaoContato, Integer tipoContatoTelefonico, String ddi, String ddd, String numero, String ramal, 
    		boolean whatsApp, String idImportacao, OperadorSistema responsavelCadastro, Pessoa pessoa) {
    	
    	ContatoTelefonico novoContatoTelefonico = new ContatoTelefonico();
    	novoContatoTelefonico.setDescricaoContato(descricaoContato);
    	novoContatoTelefonico.setTipoContatoTelefonico(tipoContatoTelefonico);
    	if(StringUtils.hasText(ddi)) {
    		novoContatoTelefonico.setDdi(ddi);
    	}
    	novoContatoTelefonico.setDdd(ddd);
    	novoContatoTelefonico.setNumero(numero);
    	if(StringUtils.hasText(ramal)){
    		novoContatoTelefonico.setRamal(ramal);
    	}
    	novoContatoTelefonico.setWhatsApp(whatsApp);
    	if(StringUtils.hasText(idImportacao)) {
    		novoContatoTelefonico.setIdImportacao(idImportacao);
    	}
    	novoContatoTelefonico.setResponsavelCadastro(responsavelCadastro);
    	novoContatoTelefonico.setPessoa(pessoa);
    
    	return novoContatoTelefonico;
    	
    }
    
}
