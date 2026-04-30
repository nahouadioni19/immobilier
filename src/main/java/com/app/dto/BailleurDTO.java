package com.app.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BailleurDTO {
    private Integer id;
    private String nom;
    private String prenom;
    private String cellulaire;
    private Integer agenceId;
}