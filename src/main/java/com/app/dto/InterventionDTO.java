package com.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.app.enums.StatutIntervention;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterventionDTO {

    private Integer id;

    private Integer agenceId;
    private String agenceNom;

    private Integer appartementId;
    private String appartementLibelle;

    private Integer locataireId;
    private String locataireNomComplet;

    private Integer prestataireId;
    private String prestataireNom;

    private Integer typeInterventionId;
    private String typeInterventionLibelle;

    private String objet;

    private String description;

    private LocalDate dateDeclaration;

    private LocalDate datePrevue;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    private StatutIntervention statut;

    private BigDecimal coutEstime;

    private BigDecimal coutReel;

    private String observation;

    private Boolean urgente;
}