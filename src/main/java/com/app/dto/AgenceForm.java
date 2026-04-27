package com.app.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.app.utils.Constants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AgenceForm {

    private Integer id;

    private String nom;

    private String code;

    private String telephone;

    private String email;

    private String adresse;

    private String ville;

    private Long montantAbonnement;

    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    private LocalDate dateDebutAbonnement;

    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    private LocalDate dateFinAbonnement;

    private boolean actif = true;
    
    private Boolean bloque; // ✅ AJOUTÉ
    
    private String statutReel;

    // getters & setters
}