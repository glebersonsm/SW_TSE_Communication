package com.sw.tse.domain.model.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "operadorsistemaempresa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperadorSistemaEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqoperadorsistemaempresa")
    @SequenceGenerator(name = "seqoperadorsistemaempresa", sequenceName = "seqoperadorsistemaempresa", allocationSize = 1)
    @Column(name = "idoperadorsistemaempresa")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "idoperadorsistema")
    private OperadorSistema operadorSistema;
    
    @ManyToOne
    @JoinColumn(name = "idempresa")
    private Empresa empresa;
    
    public static OperadorSistemaEmpresa novoOperadorSistemaEmpresa(OperadorSistema operadorSistema, Empresa empresa) {
        OperadorSistemaEmpresa operadorSistemaEmpresa = new OperadorSistemaEmpresa();
        operadorSistemaEmpresa.setOperadorSistema(operadorSistema);
        operadorSistemaEmpresa.setEmpresa(empresa);
        return operadorSistemaEmpresa;
    }
}