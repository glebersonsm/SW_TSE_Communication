package com.sw.tse.domain.model.db;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sw.tse.core.util.GenericCryptoStringConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
@Table(name = "enderecopessoa")	
@Setter(value =  AccessLevel.PRIVATE)
@Getter()
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class EnderecoPessoa {
	
	 	@Id
	    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqenderecopessoa")
	    @SequenceGenerator(name = "seqenderecopessoa", sequenceName = "seqenderecopessoa", allocationSize = 1)
	    @Column(name = "idenderecopessoa")
	    private Long id;

	    @CreationTimestamp
	    @Column(name = "datacadastro", updatable = false)
	    private LocalDateTime dataCadastro;

	    @UpdateTimestamp
	    @Column(name = "dataalteracao", insertable =  false)
	    private LocalDateTime dataAlteracao;

	    @Column(name = "descricaoendereco")
	    private String descricaoEndereco;

	    @Convert(converter = GenericCryptoStringConverter.class)
	    @Column(name = "logradouro")
	    private String logradouro;

	    @Convert(converter = GenericCryptoStringConverter.class)
	    @Column(name = "numero")
	    private String numero;

	    @Convert(converter = GenericCryptoStringConverter.class)
	    @Column(name = "complemento")
	    private String complemento;

	    @Convert(converter = GenericCryptoStringConverter.class)
	    @Column(name = "bairro")
	    private String bairro;

	    @Column(name = "uf")
	    private String uf;

	    @Convert(converter = GenericCryptoStringConverter.class)
	    @Column(name = "cep")
	    private String cep;

	    @Column(name = "enderecoparacorrespondencia")
	    private Boolean paraCorrespondencia;

	    @Column(name = "idimportacao", columnDefinition = "TEXT")
	    private String idImportacao;

	    @Getter(value = AccessLevel.PRIVATE)
	    @ManyToOne()
	    @JoinColumn(name = "idpessoa")
	    private Pessoa pessoa;

	    @ManyToOne()
	    @JoinColumn(name = "idtipoendereco")
	    private TipoEnderecoPessoa tipoEndereco;

	    @ManyToOne()
	    @JoinColumn(name = "idtipologradouro")
	    private TipoLogradouro tipoLogradouro;


	    @ManyToOne()
	    @JoinColumn(name = "idcidade")
	    private Cidade cidade;

	    @ManyToOne()
	    @JoinColumn(name = "idrespcadastro")
	    private OperadorSistema responsavelCadastro;

	    @ManyToOne()
	    @JoinColumn(name = "idrespalteracao")
	    private OperadorSistema responsavelAlteracao;
	    
	    static EnderecoPessoa novoEndereco(String descricaoEndereco, String logradouro, String numero, String complemento, String Bairro, String cep, Cidade cidade,
	    		boolean correspondencia, TipoEnderecoPessoa tipoEndereco, TipoLogradouro tipoLogradouro, OperadorSistema respCadastro, Pessoa pessoa) {
	    	EnderecoPessoa enderecoPessoa = new EnderecoPessoa();
	    	enderecoPessoa.setDescricaoEndereco(descricaoEndereco.toUpperCase());
	    	enderecoPessoa.setLogradouro(logradouro.toUpperCase());
	    	enderecoPessoa.setNumero(numero.toUpperCase());
	    	enderecoPessoa.setComplemento(complemento.toUpperCase());
	    	enderecoPessoa.setBairro(Bairro.toUpperCase());
	    	enderecoPessoa.setCep(cep.toUpperCase());
	    	enderecoPessoa.setCidade(cidade);
	    	enderecoPessoa.setParaCorrespondencia(correspondencia);
	    	enderecoPessoa.setTipoEndereco(tipoEndereco);
	    	enderecoPessoa.setTipoLogradouro(tipoLogradouro);
	    	enderecoPessoa.setResponsavelCadastro(respCadastro);
	    	enderecoPessoa.setPessoa(pessoa);    
	    	return enderecoPessoa;
	    }
}
