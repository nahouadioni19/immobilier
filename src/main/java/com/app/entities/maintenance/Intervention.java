package com.app.entities.maintenance;

import com.app.entities.BaseEntity;
import com.app.entities.recouvre.Appartement;
import com.app.entities.recouvre.Locataire;
import com.app.enums.StatutIntervention;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

//import org.hibernate.envers.Audited;

@Entity
@Table(name = "intervention")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Intervention extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appartement_id", nullable = false)
    private Appartement appartement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locataire_id")
    private Locataire locataire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestataire_id")
    private Prestataire prestataire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_intervention_id", nullable = false)
    private TypeIntervention typeIntervention;

    @Column(nullable = false, length = 300)
    private String objet;

    @Column(length = 2000)
    private String description;

    private LocalDate dateDeclaration;

    private LocalDate datePrevue;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutIntervention statut;

    @Column(precision = 15, scale = 2)
    private BigDecimal coutEstime;

    @Column(precision = 15, scale = 2)
    private BigDecimal coutReel;

    @Column(length = 1000)
    private String observation;

    private Boolean urgente = false;
}