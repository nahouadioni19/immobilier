package com.app.entities.administration;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.springframework.format.annotation.DateTimeFormat;

import com.app.entities.BaseEntity;
import com.app.utils.Constants;
import com.app.utils.JUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Audited
@Entity
@Table(name = "t_assignation", indexes = {
        @Index(name = "idx_utilisateur", columnList = "utilisateur_id"),
        @Index(name = "idx_assign_role", columnList = "role_id") })
@DynamicUpdate
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Assignation extends BaseEntity {

    private static final long serialVersionUID = 1L;
    
    @Transient
    private String libelle;

    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false, referencedColumnName = "idt")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "utilisateur_id", nullable = false, referencedColumnName = "idt")
    private Utilisateur utilisateur;
    
    @Column(nullable = false)
    private boolean courant = true;

    // Constructeur pratique
    public Assignation(Role role) {
        this.role = role;
    }

    public Assignation(Role role, Utilisateur utilisateur, boolean courant) {
        this.role = role;
        this.utilisateur = utilisateur;
        this.courant = courant;
    }

    // -----------------------
    // Hooks
    // -----------------------
    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (dateDebut == null) {
            dateDebut = LocalDate.now();
        }
        if (dateFin == null) {
            // valeur sentinelle : 31/12/9999
            dateFin = LocalDate.of(9999, 12, 31);
        }
    }

    // -----------------------
    // Méthodes utilitaires
    // -----------------------
    public String getDateDebutStr() {
        return (dateDebut != null) ? JUtils.dateShortToString(dateDebut.atStartOfDay()) : "";
    }

    public String getDateFinStr() {
        return (dateFin != null) ? JUtils.dateShortToString(dateFin.atStartOfDay()) : "";
    }

    public String getLibelle() {
        if (role != null) {
            return "Rôle: " + role.getLibelle();
        }
        return "";
    }
}
