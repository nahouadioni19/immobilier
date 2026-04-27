package com.app.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntityWithCodeLibelle extends BaseEntity {

    @Column(unique = true, length = 100, nullable = false)
    protected String code;

    @Column(nullable = false)
    protected String libelle;

    protected BaseEntityWithCodeLibelle() {}

    protected BaseEntityWithCodeLibelle(String code, String libelle) {
        this.code = code;
        this.libelle = libelle;
    }
}
