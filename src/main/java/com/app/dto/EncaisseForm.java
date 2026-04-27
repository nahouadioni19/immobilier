package com.app.dto;
import java.time.LocalDate;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EncaisseForm {

    private Integer id;

    private Integer bailId;
    private Integer identificationId;
    private Integer utilisateurId;

    private LocalDate encDate;

    private Long encMontant;
    private Long encPerdeb;
    private Long encAndeb;
    private Long encPerfin;
    private Long encAnfin;

    private Long enctotal;
    private Long encloyer;

    private boolean encvalide;

    private Long encmois;
    private Long encannee;

    private String encStatutRetour;
    private String encMode;

    private Long encArriere;

    private String encDeb;
    private String encFin;

    private Long encPenalite;
    private Long encNet;

    private String encNumChq;

    private Long encRepport;
    private Long encMontReppo;
    
    private Integer statut;
    private Integer version;
}