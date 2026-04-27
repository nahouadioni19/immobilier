package com.app.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.envers.Audited;

import lombok.Getter;
import lombok.Setter;

@Audited
@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntityWithLibelle extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private String libelle;

    
    protected BaseEntityWithLibelle() {
        super();
    }
    
    protected BaseEntityWithLibelle(String libelle) {
        this();
        this.libelle = libelle;
    }
}
