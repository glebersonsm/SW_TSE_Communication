package com.sw.tse.domain.model.db;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.sw.tse.core.util.GenericCryptoStringConverter;
import com.sw.tse.core.util.GenericCryptoLocalDateConverter;
import jakarta.persistence.Convert;

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
	@Convert(converter = GenericCryptoStringConverter.class)
	@Column(name ="cpfcnpj")
	private String cpfCnpj;
	 @ManyToOne()
    @JoinColumn(name = "idtipodocumentoidentidade")
	private TipoDocumentoPessoa tipoDocumento;
	@Column(name = "rginscricaoestadual")
	private String numeroDocumento;
	@Convert(converter = GenericCryptoLocalDateConverter.class)
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
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pessoa")
	private List<EnderecoPessoa> enderecos = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pessoa")
    private List<ContatoTelefonico> telefones = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pessoa")
    private List<EnderecoEmail> emails = new ArrayList<>();
    
    
    
    public List<EnderecoPessoa> getEnderecos(){
    	return Collections.unmodifiableList(this.enderecos);
    }
    
    public List<ContatoTelefonico> getTelefones(){
    	return Collections.unmodifiableList(telefones);
    }
    
    public List<EnderecoEmail> getEmails(){
    	return Collections.unmodifiableList(emails);
    }

    public void adicionarEndereco(String descricaoEndereco, String logradouro, String numero, String complemento, String Bairro, String cep, Cidade cidade,
	    		boolean correspondencia, TipoEnderecoPessoa tipoEndereco, TipoLogradouro tipoLogradouro, OperadorSistema respCadastro) {
    	
    	EnderecoPessoa novoEndereco = EnderecoPessoa.novoEndereco(descricaoEndereco, logradouro, numero, complemento, Bairro, 
    			cep, cidade, correspondencia, tipoEndereco, tipoLogradouro, respCadastro,this);
    	enderecos.add(novoEndereco);
    }
    
    public void removerEndereco(Long idEndereco) {
    	enderecos.removeIf(endereco -> endereco.getId().equals(idEndereco));
    }
    
    
    public void adicionarContatoTelefonico(String descricaoContato, Integer tipoContatoTelefonico, String ddi, String ddd, String numero, String ramal, 
    		boolean whatsApp, String idImportacao, OperadorSistema responsavelCadastro) {
    	ContatoTelefonico contatoTelefonico = ContatoTelefonico.novoContatoTelefonico(descricaoContato, tipoContatoTelefonico, ddi, ddd, numero, ramal,
    			whatsApp, idImportacao, responsavelCadastro, this);
    	
    	telefones.add(contatoTelefonico);
    }
    
    public void removerContatoTelefonico(Long idContatoTelefonico){
    	telefones.removeIf(telefone -> telefone.getId().equals(idContatoTelefonico));
    }
    
    public void adicionarEmail(String Descricao, String email, String idImportacao, OperadorSistema responsavelCadastro) {
    	EnderecoEmail novoEmail = EnderecoEmail.novoEnderecoEmail(Descricao, email, idImportacao, responsavelCadastro, this);
    	emails.add(novoEmail);
    }
    
    public void removerEmail(Long idEnderecoEmail) {
    	emails.removeIf(email -> email.getId().equals(idEnderecoEmail));
    }
    
}
