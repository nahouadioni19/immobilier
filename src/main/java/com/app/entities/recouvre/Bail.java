package com.app.entities.recouvre;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.springframework.format.annotation.DateTimeFormat;
import org.hibernate.annotations.SQLRestriction;

import com.app.entities.BaseEntity;
import com.app.entities.administration.Agence;
import com.app.enums.StatutBail;
import com.app.enums.UsageBail;
import com.app.utils.Constants;

import jakarta.validation.constraints.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Audited
@Entity
@SQLRestriction("deleted = false")
@Getter
@Setter
@NoArgsConstructor
@Table(name = "t_bail")
@DynamicUpdate
public class Bail extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_id", nullable = false)
    private Agence agence;
    
    @NotNull(message = "La date de début du bail est obligatoire")
    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    @Column(nullable = false)
    private LocalDate dateDebut;

    @NotNull(message = "La date de début de paiement est obligatoire")
    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    @Column(nullable = false)
    private LocalDate dateFin;

    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    private LocalDate dateResiliation;

    @Column(nullable = false)
    private Long montantLoyer = 0L;

    @Column(nullable = false)
    private Long montantCharges = 0L;

    @Column(nullable = false)
    private Long caution = 0L;

    @Column(nullable = false)
    private Long avance = 0L;

    @Column(nullable = false)
    private Long honoraire = 0L;

    @Column(nullable = false)
    private Long total = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutBail statut = StatutBail.ACTIF;

    @Enumerated(EnumType.STRING)
    private UsageBail utilite = UsageBail.HABITATION;

    // 🔥 IMPORTANT : PAS DE CASCADE ICI
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appartement_id", nullable = false)
    private Appartement appartement;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "locataire_id", nullable = false)
    private Locataire locataire;

    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    private LocalDate derniereDatePaiement;

    @Column(nullable = false)
    private boolean actif = true;

    @Column(nullable = false)
    private boolean deleted = false;

    // =========================
    // CALCUL TOTAL
    // =========================
    
    @Transient
    public long getDureeEnMois() {
        if (dateDebut != null && dateFin != null) {
            return ChronoUnit.MONTHS.between(
                    YearMonth.from(dateDebut),
                    YearMonth.from(dateFin)
            ) + 1;
        }
        return 0;
    }
    
    @PrePersist
    @PreUpdate
    private void calculerTotal() {
    	
    	long duree = getDureeEnMois();
    	
        this.total =
                safe(montantCharges)
              + safe(caution)
              + safe(avance)
              + safe(honoraire);

        if (dateResiliation != null) {
            this.statut = StatutBail.RESILIE;
        }
    }

    private long safe(Long v) {
        return v == null ? 0L : v;
    }
    
    private String comment;
    
    @Column(name = "premier_recouvre", nullable = false)
    private boolean premierRecouvre = true;
    
    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    @Column(name = "date_effet_loyer")
    private LocalDate dateEffetLoyer;
    
    public boolean isLoyerRenseigne() {
        return montantLoyer != null && montantLoyer > 0;
    }
    
    @Column(name = "date_fin_recouvre")
    private LocalDate dateFinRecouvre;
    // + getter / setter
    
}
