package com.app.entities.maintenance;

import org.hibernate.annotations.DynamicUpdate;

import com.app.entities.BaseEntity;
import com.app.entities.administration.Agence;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="prestataire")
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate

public class Prestataire extends BaseEntity{

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_id", nullable = false)
    private Agence agence;
	
    private String nom;

    private String telephone;

    private String email;

    private String adresse;

}