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
@Table(name = "enderecoemail")
@Setter(value =  AccessLevel.PRIVATE)
@Getter()
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class EnderecoEmail {

 	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqcontatotelefonico")
    @SequenceGenerator(name = "seqcontatotelefonico", sequenceName = "seqcontatotelefonico", allocationSize = 1)
 	@Column(name = "idcontatotelefonico")
	private Long id;
 	
 	@CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    @Column(name = "email", length = 1024)
    private String email;

    @Column(name = "descricao", length = 30)
    private String descricao;

    @Column(name = "idimportacao", columnDefinition = "TEXT")
    private String idImportacao;

    @ManyToOne()
    @JoinColumn(name = "idpessoa")
    private Pessoa pessoa;

    @ManyToOne()
    @JoinColumn(name = "idrespcadastro")
    private OperadorSistema responsavelCadastro;

    @ManyToOne()
    @JoinColumn(name = "idrespalteracao")
    private OperadorSistema responsavelAlteracao;
    
    static EnderecoEmail novoEnderecoEmail(String Descricao, String email, String idImportacao, OperadorSistema responsavelCadastro, Pessoa pessoa) {
    	EnderecoEmail novoEnderecoEmail = new EnderecoEmail();
    	
    	novoEnderecoEmail.setDescricao(Descricao);
    	novoEnderecoEmail.setEmail(email);
    	novoEnderecoEmail.setResponsavelCadastro(responsavelCadastro);
    	novoEnderecoEmail.setPessoa(pessoa);
    	
    	return novoEnderecoEmail;
    }
    
    
}
