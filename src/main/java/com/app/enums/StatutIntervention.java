package com.app.enums;
public enum StatutIntervention {

    DECLAREE("Déclarée"),
    PLANIFIEE("Planifiée"),
    EN_COURS("En cours"),
    TERMINEE("Terminée"),
    ANNULEE("Annulée");

    private final String libelle;

    StatutIntervention(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}