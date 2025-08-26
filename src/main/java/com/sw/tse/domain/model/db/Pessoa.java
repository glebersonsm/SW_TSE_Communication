package com.sw.tse.domain.model.db;

import java.time.LocalDate;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pessoa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pessoa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idpessoa")
	private Long idPessoa;
	@Column(name = "datacadastro", insertable = true, updatable = false)
	@CreationTimestamp
	private LocalDateTime dataCadastro;
	@UpdateTimestamp
	@Column(name ="dataalteracao")
	private LocalDateTime dataAlteracao;
	@Column(name = "razaosocial")
	private String nome;
	//@Convert(converter = GenericCryptoStringConverter.class)
	@Column(name ="cpfcnpj")
	private String cpfCnpj;
	//@Convert(converter = GenericCryptoLocalDateConverter.class)
	 @ManyToOne()
    @JoinColumn(name = "idtipodocumentoidentidade")
	private TipoDocumentoPessoa tipoDocumento;
	@Column(name = "rginscricaoestadual")
	private String numeroDocumento;
	@Column(name ="datanascimento")
	private LocalDate dataNascimento;
	@Column(name = "sexo")
	private Integer sexo;
	@Column(name ="idnacionalidade")
	private Integer nacionalidade = 30;
	@Column(name = "tipopessoa")
	private Long tipoPessoa = 0L;	
    @ManyToOne()
    @JoinColumn(name = "idrespcadastro", insertable = true, updatable = false)
	private OperadorSistema operadorCadastro;
    @ManyToOne()
    @JoinColumn(name = "idrespalteracao", insertable = false, updatable = true)
	private OperadorSistema operadorAlteracao;
}
