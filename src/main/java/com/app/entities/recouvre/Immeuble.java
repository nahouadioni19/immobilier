package com.app.entities.recouvre;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import com.app.entities.BaseEntity;
import com.app.entities.administration.Agence;
import com.app.entities.administration.Utilisateur;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Audited
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "t_immeuble")
@DynamicUpdate

public class Immeuble extends BaseEntity {
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_id", nullable = false)
    private Agence agence;
	
    @Column(nullable = false, length = 150)
    private String nomImmeuble;

    @Column(unique = true, nullable = false, length = 50)
    private String codeImmeuble; // code unique pour identification

    @Column(length = 255)
    private String adresse;

    @Column(length = 100)
    private String ville;

    @Column(length = 100)
    private String pays;

    @Column(nullable = false)
    private int nombreEtages = 0;

    @Column(nullable = false)
    @Min(1900) // éviter des années absurdes, 2000 si tu veux vraiment moderne
    private int anneeConstruction = 0;

    @Column(length = 50)
    private String typeImmeuble; // ex: Résidentiel, Commercial...

    @Column(length = 100)
    private String numeroTitreFoncier;

    // relation avec le bailleur
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "bailleur_id", nullable = false, referencedColumnName = "idt")
    private Bailleur bailleur;

    // relation avec les appartements
    @JsonManagedReference
    @OneToMany(mappedBy = "immeuble", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appartement> appartements = new ArrayList<>();
    
    // suivi utilisateur (créateur ou gestionnaire)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utilisateur_id", referencedColumnName = "idt")
    private Utilisateur utilisateur;

    // 👉 utilité: gérer les doublons
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Immeuble)) return false;
        Immeuble immeuble = (Immeuble) o;
        return Objects.equals(codeImmeuble, immeuble.codeImmeuble);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codeImmeuble);
    }
}