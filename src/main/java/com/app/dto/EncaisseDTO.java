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
    private String encMode;
    private String locataireNom;
    private String locatairePrenom;
    private String appartementNumero;
    private String utilisateurNom;
    private String utilisateurPrenoms;

    public EncaisseDTO(Integer id, LocalDate encDate, Long encMontant, String encMode,
                       String locataireNom, String locatairePrenom,
                       String appartementNumero,
                       String utilisateurNom, String utilisateurPrenoms) {
        this.id = id;
        this.encDate = encDate;
        this.encMontant = encMontant;
        this.encMode = encMode;
        this.locataireNom = locataireNom;
        this.locatairePrenom = locatairePrenom;
        this.appartementNumero = appartementNumero;
        this.utilisateurNom = utilisateurNom;
        this.utilisateurPrenoms = utilisateurPrenoms;
    }

    // getters
}