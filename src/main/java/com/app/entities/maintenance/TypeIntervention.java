package com.app.entities.maintenance;

import com.app.entities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="type_intervention")
public class TypeIntervention extends BaseEntity{

    @Column(nullable=false, unique=true)
    private String libelle;

}