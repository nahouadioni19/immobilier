package com.app.entities.recouvre;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.springframework.format.annotation.DateTimeFormat;

import com.app.entities.BaseEntity;
import com.app.entities.administration.Agence;
import com.app.entities.administration.Site;
import com.app.enums.Sexe;
import com.app.utils.Constants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Audited
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "t_bailleur")
@DynamicUpdate

public class Bailleur extends BaseEntity {
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_id", nullable = false)
    private Agence agence;
    /* ===== Identité ===== */

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, length = 20)
    private String cellulaire;

    @Column(length = 20)
    private String telephone;

    @Column(length = 255)
    private String adresse;

    @Column(length = 150)
    private String email;

    /* ===== Naissance ===== */

    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "lieu_naissance", length = 150)
    private String lieuNaissance;

    /* ===== Contact secondaire ===== */

    @Column(name = "nom_contact", length = 100)
    private String nomContact;

    @Column(name = "prenom_contact", length = 100)
    private String prenomContact;

    @Column(name = "tel_contact", length = 20)
    private String telContact;

    @Column(name = "email_contact", length = 150)
    private String emailContact;

    /* ===== Documents ===== */

    @Column(name = "carte_identite_path", length = 255)
    private String carteIdentitePath;

    @Column(name = "facture_cie_path", length = 255)
    private String factureCiePath;

    /* ===== Sexe ===== */

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 10)
    private Sexe sexe;
}
