package com.app.entities.administration;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.springframework.format.annotation.DateTimeFormat;

import com.app.entities.BaseEntity;
import com.app.entities.recouvre.Appartement;
import com.app.entities.recouvre.Bail;
import com.app.entities.recouvre.Locataire;
import com.app.enums.StatutAbonnement;
import com.app.utils.Constants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_agence")
@Audited
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agence extends BaseEntity {

    /* ===== IDENTITÉ ===== */

    @NotBlank
    @Column(nullable = false, unique = true)
    private String nom;

    @Column(unique = true)
    private String code; // ex: AG001

    /* ===== CONTACT ===== */

    private String telephone;
    private String email;

    @Column(length = 500)
    private String adresse;

    private String ville;

    /* ===== BUSINESS ===== */

    @Column(nullable = false)
    private boolean actif = true;

    @Column(nullable = false)
    private boolean deleted = false;

    private Boolean bloque = false;
    /* ===== ABONNEMENT SaaS ===== */

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatutAbonnement statutAbonnement;

    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    private LocalDate dateDebutAbonnement;
    
    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    private LocalDate dateFinAbonnement;

    private Long montantAbonnement; // ex: 10000 FCFA

    /* ===== RELATIONS ===== */

    @OneToMany(mappedBy = "agence")
    private List<Utilisateur> utilisateurs;

    @OneToMany(mappedBy = "agence")
    private List<Appartement> appartements;

    @OneToMany(mappedBy = "agence")
    private List<Locataire> locataires;

    @OneToMany(mappedBy = "agence")
    private List<Bail> baux;

    /* ===== LOGIQUE ===== */

    /*@Transient
    public boolean isAbonnementActif() {
        return dateFinAbonnement != null &&
               dateFinAbonnement.isAfter(LocalDate.now());
    }*/
    
    @Transient
    public boolean isAccessible() {
        return getStatutReel() == StatutAbonnement.ACTIF;
    }
    
    @Transient
    public StatutAbonnement getStatutReel() {

        // 🔴 PRIORITÉ 1 : BLOQUÉ
        if (Boolean.TRUE.equals(bloque)) {
            return StatutAbonnement.SUSPENDU;
        }

        // 🔴 PRIORITÉ 2 : INACTIF
        if (!actif) {
            return StatutAbonnement.SUSPENDU;
        }

        // 🔴 PRIORITÉ 3 : EXPIRE
        if (dateFinAbonnement == null || dateFinAbonnement.isBefore(LocalDate.now())) {
            return StatutAbonnement.EXPIRE;
        }

        // 🟢 OK
        return StatutAbonnement.ACTIF;
    }
    
    @Transient
    public boolean isExpired() {
        return dateFinAbonnement != null
            && dateFinAbonnement.isBefore(LocalDate.now());
    }
}
