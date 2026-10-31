package com.app.entities.recouvre;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.springframework.format.annotation.DateTimeFormat;

import com.app.entities.BaseEntity;
import com.app.entities.administration.Agence;
import com.app.entities.administration.Utilisateur;
import com.app.utils.Constants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "t_encaisse")
@DynamicUpdate
public class Encaisse extends BaseEntity {

    // =========================
    // UTILISATEUR
    // =========================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    // =========================
    // BAIL (IMPORTANT FIX)
    // =========================
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bail_id", nullable = false)
    private Bail bail;

    // =========================
    // IDENTIFICATION
    // =========================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identification_id")
    private Identification identification;

    // =========================
    // AGENCE
    // =========================
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agence_id", nullable = false)
    private Agence agence;

    // =========================
    // DATE
    // =========================
    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    @Column(name = "enc_date", nullable = false)
    private LocalDate encDate;

    // =========================
    // MONTANTS
    // =========================
    private Long encMontant = 0L;
    
    private Long encPerdeb = 0L;
    
    private Long encAndeb = 0L;
    
    private Long encPerfin = 0L;
    
    private Long encAnfin = 0L;
    
    private Long enctotal = 0L;
    
    private Long encloyer = 0L;

    private boolean encvalide;

    private Long encmois = 0L;
    
    private Long encannee = 0L;

    private String encStatutRetour;
    
    private String encMode;

    private Long encArriere = 0L;
    
    private String encDeb;
    
    private String encFin;

    private Long encPenalite = 0L;
    
    private Long encNet = 0L;

    private String encNumChq;

    private Long encRepport = 0L;
    
    private Long encMontReppo = 0L;

    // =========================
    // STATUT
    // =========================
    @Column(nullable = false)
    private Integer statut = 0; // 0=attente, 1=transmis

    private Integer filtreAgentId;

    @Column(length = 255)
    private String chequePath;
    
    private Boolean bloque = false; // qui permet d'annuler la pénalité sur un encaissement
}