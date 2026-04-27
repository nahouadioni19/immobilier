package com.app.dto;

import java.util.ArrayList;
import java.util.List;

import com.app.entities.recouvre.Appartement;
import com.app.entities.recouvre.Bailleur;
import com.app.entities.recouvre.Immeuble;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImmeubForm {
    private Immeuble immeuble = new Immeuble();
    private List<Appartement> appartements = new ArrayList<>();

    public Immeuble getImmeuble() {
        // Assure qu’il y a toujours un bailleur initialisé
        if (immeuble.getBailleur() == null) {
            immeuble.setBailleur(new Bailleur());
        }
        return immeuble;
    }

    @Override
    public String toString() {
        return "ImmeubForm{immeuble=" + immeuble +
               ", appartements=" + appartements.size() + "}";
    }
}