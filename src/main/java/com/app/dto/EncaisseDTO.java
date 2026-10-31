package com.app.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EncaisseDTO {

    private Integer id;
    private LocalDate encDate;
    private Long encMontant;
    private Long encPenalite;
    private String encMode;
    private String locataireNom;
    private String locatairePrenom;
    private String appartementNumero;
    private String utilisateurNom;
    private String utilisateurPrenoms;
    private String immeubleLocalisation;
    private String bailleurNom;
    private String bailleurPrenom;

    public EncaisseDTO(Integer id, LocalDate encDate, Long encMontant, Long encPenalite, String encMode,
                       String locataireNom, String locatairePrenom,
                       String appartementNumero,
                       String utilisateurNom, String utilisateurPrenoms, 
                       String immeubleLocalisation, String bailleurNom, String bailleurPrenom) {
        this.id = id;
        this.encDate = encDate;
        this.encMontant = encMontant;
        this.encPenalite = encPenalite;
        this.encMode = encMode;
        this.locataireNom = locataireNom;
        this.locatairePrenom = locatairePrenom;
        this.appartementNumero = appartementNumero;
        this.utilisateurNom = utilisateurNom;
        this.utilisateurPrenoms = utilisateurPrenoms;
        this.immeubleLocalisation = immeubleLocalisation;
        this.bailleurNom = bailleurNom;
        this.bailleurPrenom = bailleurPrenom;
    }

    // getters
}