package com.app.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PrestataireDTO {
    private Integer id;
    private String nom;
    private String adresse;
    private String telephone;
    private String email;
    private Integer agenceId;
}