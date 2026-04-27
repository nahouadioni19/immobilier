package com.app.enums;

public enum Titre {
    MONSIEUR("M."), 
    MADAME("Mme"), 
    MADEMOISELLE("Mlle"), 
    DOCTEUR("Dr."), 
    PROFESSEUR("Pr.");

    private String libelle;

    Titre(String libelle) {
        this.libelle = libelle;
    }

    @Override
    public String toString() {
        return libelle;
    }
}
