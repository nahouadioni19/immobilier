package com.app.dto;

import org.springframework.format.annotation.DateTimeFormat;
import com.app.utils.Constants;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BailForm {

    private Integer id;

    // =========================
    // RELATIONS (IDs seulement)
    // =========================
    @NotNull(message = "L'agence est obligatoire")
    private Integer agenceId;

    @NotNull(message = "L'appartement est obligatoire")
    private Integer appartementId;

    @NotNull(message = "Le locataire est obligatoire")
    private Integer locataireId;

    // =========================
    // DATES
    // =========================
    @NotNull(message = "La date de début du bail est obligatoire")
    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin du bail est obligatoire")
    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    private LocalDate dateFin;

    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    private LocalDate dateResiliation;

    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    private LocalDate derniereDatePaiement;

    // =========================
    // MONTANTS
    // =========================
    private Long montantLoyer = 0L;

    private Long montantCharges = 0L;

    private Long caution = 0L;

    private Long avance = 0L;

    private Long honoraire = 0L;

    // =========================
    // OPTIONS
    // =========================
    private String statut;   // ou StatutBail en String si tu veux simplifier
    private String utilite;  // ou UsageBail en String

    private boolean actif = true;
    private boolean deleted = false;

}