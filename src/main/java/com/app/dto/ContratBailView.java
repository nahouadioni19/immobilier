package com.app.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Données déjà formatées pour le gabarit locataire/bail/contrat-bail.html.
 * Classe Lombok (et non record) pour que Thymeleaf/SpEL lise les getters sans surprise.
 */
@Getter
@Builder
public class ContratBailView {

    private final String numero;
    private final String lieuSignature;
    private final String dateSignature;

    // Agence (mandataire du bailleur)
    private final String agenceNom;
    private final String agenceAdresse;
    private final String agenceTelephone;
    private final String agenceEmail;

    // Bailleur (propriétaire de l'immeuble)
    private final String bailleurNom;

    // Preneur
    private final String locataireNom;
    private final String locataireTelephone;
    private final String locataireEmail;
    private final String locatairePiece;

    // Bien
    private final String bienLibelle;
    private final String bienComposition;
    private final String immeubleNom;
    private final String immeubleAdresse;
    private final String usage;

    // Durée
    private final String dateDebut;
    private final String dateFin;
    private final long dureeMois;

    // Montants (FCFA)
    private final String loyer;              // loyer initial du contrat
    private final String loyerEnLettres;
    private final boolean revise;            // true si au moins une révision existe
    private final String loyerActuel;
    private final String loyerActuelEnLettres;
    private final List<RevisionLigne> revisions;
    private final String charges;
    private final String avance;
    private final String caution;
    private final String honoraire;
    private final String total;
    private final String totalEnLettres;
    private final int avanceNombreMois;
    private final int cautionNombreMois;

    // Règles de paiement
    private final int jourLimitePaiement;
    private final int penaliteTaux;
    private final int preavisMois;
    private final int delaiResolutoireJours;
    private final String tribunal;

    @Getter
    @AllArgsConstructor
    public static class RevisionLigne {
        private final String dateEffet;
        private final String ancienMontant;
        private final String nouveauMontant;
    }
}