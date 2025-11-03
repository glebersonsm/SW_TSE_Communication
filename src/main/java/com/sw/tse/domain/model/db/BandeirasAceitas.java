package com.sw.tse.domain.model.db;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bandeirasaceitas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BandeirasAceitas {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqbandeirasaceitas")
    @SequenceGenerator(name = "seqbandeirasaceitas", sequenceName = "seqbandeirasaceitas", allocationSize = 1)
    @Column(name = "idbandeirasaceitas")
    private Long id;
    
    @Column(name = "bandeira")
    private String bandeira;
}

