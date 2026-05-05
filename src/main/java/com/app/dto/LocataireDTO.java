package com.app.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LocataireDTO {
    private Integer id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private Integer agenceId;
}