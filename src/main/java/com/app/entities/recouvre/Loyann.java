package com.app.entities.recouvre;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import com.app.entities.BaseEntity;
import com.app.entities.administration.Agence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Audited
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "t_loyann",uniqueConstraints = {@UniqueConstraint(columnNames = {"bail_id", "mois", "annee"})})
@DynamicUpdate

public class Loyann extends BaseEntity{

	 @ManyToOne
	 @JoinColumn(name = "bail_id", nullable = true, referencedColumnName ="idt") 
	 private Bail bail;
	 
	 @ManyToOne(fetch = FetchType.LAZY)
	 @JoinColumn(name = "agence_id", nullable = false)
	 private Agence agence;
	 
	 @Column(nullable = false)
	 private long loyer = 0;
	 
	 @Column(nullable = false)
	 private int mois;
	 
	 @Column(nullable = false)
	 private int annee;	
	 
	 // 💰 Montant mensuel attendu
	 @Column(name = "montant_du")
	 private Long montantDu = 0L;

	    // 💰 Montant réellement payé
	 @Column(name = "montant_paye")
	 private Long montantPaye = 0L;

	    // 💰 Pénalité
	 @Column(name = "penalite")
	 private Long penalite = 0L;

	    // 📊 Statut
	 @Column(name = "statut", length = 20)
	 private String statut = "IMPAYE";
	 
	 //@Enumerated(EnumType.STRING)
	 //@Column(length = 20)
	 //private StatutLoyann statut = StatutLoyann.IMPAYE;
	 
	// =========================
	    // 🔹 MÉTHODES UTILES
	    // =========================

	    @Transient
	    public long getMontantDuSafe() {
	        return montantDu != null ? montantDu : loyer;
	    }

	    @Transient
	    public long getMontantPayeSafe() {
	        return montantPaye != null ? montantPaye : 0L;
	    }

	    @Transient
	    public long getReste() {
	        return getMontantDuSafe() - getMontantPayeSafe();
	    }

	    @Transient
	    public boolean estPaye() {
	        return getMontantPayeSafe() >= getMontantDuSafe();
	    }

	    @Transient
	    public boolean estPartiel() {
	        return getMontantPayeSafe() > 0 && getMontantPayeSafe() < getMontantDuSafe();
	    }

	    @Transient
	    public boolean estImpayé() {
	        return getMontantPayeSafe() == 0;
	    }

	    @Transient
	    public boolean estEnRetard(LocalDate today) {
	        return today.getDayOfMonth() > 10 && getMontantPayeSafe() < getMontantDuSafe();
	    }

	    // =========================
	    // 🔥 AUTO SYNC (IMPORTANT)
	    // =========================

	    @PrePersist
	    @PreUpdate
	    public void syncMontants() {

	        // 👉 Si montantDu non défini, on reprend ancien champ
	        if (montantDu == null || montantDu == 0) {
	            montantDu = loyer;
	        }

	        if (montantPaye == null) {
	            montantPaye = 0L;
	        }

	        if (penalite == null) {
	            penalite = 0L;
	        }

	        // 🔥 Mise à jour du statut automatiquement
	        if (montantPaye >= montantDu) {
	            statut = "PAYE";
	        } else if (montantPaye > 0) {
	            statut = "PARTIEL";
	        } else {
	            statut = "IMPAYE";
	        }
	    }
}
