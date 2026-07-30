package com.app.form;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.app.enums.StatutIntervention;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterventionForm {

    private Integer id;

    @NotNull
    private Integer appartementId;

    private Integer locataireId;

    private Integer prestataireId;

    @NotNull
    private Integer typeInterventionId;

    @NotBlank
    private String objet;

    private String description;

    @NotNull
    private LocalDate dateDeclaration;

    private LocalDate datePrevue;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    @NotNull
    private StatutIntervention statut;

    private BigDecimal coutEstime;

    private BigDecimal coutReel;

    private String observation;

    private Boolean urgente = false;
}