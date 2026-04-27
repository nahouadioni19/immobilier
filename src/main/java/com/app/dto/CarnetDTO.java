package com.app.dto;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.AssertTrue;

public class CarnetDTO {
    private Integer id;
    private Integer utilisateurId; // l’ID de l’agent choisi
    private long carNumDeb;
    private long carNumFin;

    // getters / setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Integer utilisateurId) { this.utilisateurId = utilisateurId; }

    public long getCarNumDeb() { return carNumDeb; }
    public void setCarNumDeb(long carNumDeb) { this.carNumDeb = carNumDeb; }

    public long getCarNumFin() { return carNumFin; }
    public void setCarNumFin(long carNumFin) { this.carNumFin = carNumFin; }
}
