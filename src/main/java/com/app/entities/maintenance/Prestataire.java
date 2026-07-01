package com.app.entities.maintenance;

import com.app.entities.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="prestataire")
public class Prestataire extends BaseEntity{

    private String nom;

    private String telephone;

    private String email;

    private String adresse;

}