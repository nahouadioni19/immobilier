package com.app.enums;

public enum ActionMenu {
    CREATE("Ajouter"),
    EDIT("Modifier"),
    DELETE("Supprimer"),
    VIEW("Consulter"),
    PRINT("Editer"),
    VALIDATE("Valider"),
    REJECT("Rejeter"),
    TRANSMIT("Transmettre"),
    RETURN("Retourner"),
    DIFFER("Différer"),
    PRINT_GLOBAL("Edition générale");

    public final String libelle;

    private ActionMenu(String libelle) {
        this.libelle = libelle;
    }
}
