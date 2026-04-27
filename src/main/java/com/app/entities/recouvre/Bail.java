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

import jakarta.validation.constraints.Min;
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
	
    /* ===== Dates ===== */

    @NotNull(message = "La date de début est obligatoire")
    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    @Column(nullable = false)
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    @Column(nullable = false)
    private LocalDate dateFin;

    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    private LocalDate dateResiliation;

    /* ===== Montants ===== */

    @NotNull @Min(0)
    @Column(nullable = false)
    private Long montantLoyer = 0L;

    @NotNull @Min(0)
    @Column(nullable = false)
    private Long montantCharges = 0L;

    @NotNull @Min(0)
    @Column(nullable = false)
    private Long caution = 0L;

    @NotNull @Min(0)
    @Column(nullable = false)
    private Long avance = 0L;

    @NotNull @Min(0)
    @Column(nullable = false)
    private Long honoraire = 0L;

    @Column(nullable = false)
    private Long total = 0L;

    /* ===== Statuts ===== */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutBail statut = StatutBail.ACTIF;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UsageBail utilite = UsageBail.HABITATION;

    /* ===== Relations ===== */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appartement_id", nullable = false)
    private Appartement appartement;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "locataire_id", nullable = false)
    private Locataire locataire;

    /* ===== Paiement ===== */

    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    @Column(name = "der_paye_date")
    private LocalDate derniereDatePaiement;

    /* ===== Calcul durée ===== */

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

    /* ===== Calcul automatique ===== */

    @PrePersist
    @PreUpdate
    private void calculerTotal() {

        long duree = getDureeEnMois();

        long mensualite =
                safe(montantLoyer)
              + safe(montantCharges);

        this.total = safe(caution) + safe(avance) + safe(honoraire);

        // 🔥 Synchronisation automatique statut
        if (dateResiliation != null) {
            this.statut = StatutBail.RESILIE;
        }
    }

    private long safe(Long value) {
        return value == null ? 0L : value;
    }
    
    @Column(nullable = false)
    private boolean actif = true;

    @Column(nullable = false)
    private boolean deleted = false;
}




/*public class Bail extends BaseEntity {

    @NotNull(message = "La date de début est obligatoire")
    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    @Column(nullable = false)
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    @Column(nullable = false)
    private LocalDate dateFin;

    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    @Column(nullable = true)
    private LocalDate dateResiliation;
    /* ===== Montants ===== */

    /*@NotNull
    @Min(0)
    @Column(nullable = false)
    private Long montantLoyer = 0L;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Long montantCharges = 0L;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Long caution = 0L;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Long avance = 0L;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Long honoraire = 0L;

    /** Calculé automatiquement */
 /*   @Column(nullable = false)
    private Long total = 0L;

    /* ===== Statuts ===== */

  /*  @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutBail statut = StatutBail.ACTIF;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UsageBail utilite = UsageBail.HABITATION;

    /* ===== Relations ===== */

   // @ManyToOne
  /*  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appartement_id", nullable = false)
    private Appartement appartement;

    //@ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locataire_id", nullable = false)
    private Locataire locataire;

    /* ===== Suivi paiement ===== */

   /* @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    @Column(name = "der_paye_date")
    private LocalDate derniereDatePaiement;

    /* ===== Calculs ===== */

   /* @Transient
    public long getDureeEnMois() {
        if (dateDebut != null && dateFin != null) {
            return ChronoUnit.MONTHS.between(
                    YearMonth.from(dateDebut),
                    YearMonth.from(dateFin)
            ) + 1; // ✅ inclusif
        }
        return 0;
    }

    /* ===== Calcul automatique du total ===== */

   /* @PrePersist
    @PreUpdate
    private void calculerTotal() {

        long duree = getDureeEnMois();

        long mensualite =
                (montantLoyer != null ? montantLoyer : 0)
              + (montantCharges != null ? montantCharges : 0);

        this.total =
                mensualite * duree
              + (caution != null ? caution : 0)
              + (avance != null ? avance : 0)
              + (honoraire != null ? honoraire : 0);
    }
}*/
