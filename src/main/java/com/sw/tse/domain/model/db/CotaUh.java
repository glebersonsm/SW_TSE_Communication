package com.sw.tse.domain.model.db;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cotauh")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CotaUh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcotauh")
    private Long idCotaUh;

    @Column(name = "identificadorunicocota", length = 250)
    private String identificadorUnicoCota;
}
