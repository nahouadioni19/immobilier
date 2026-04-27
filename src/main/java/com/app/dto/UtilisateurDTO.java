package com.app.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.app.dto.AssignationDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UtilisateurDTO {

    private Integer id;
    private String username;
    private String nom;
    private String prenoms;
    private String matricule;
    private String telephone;
    private String email;
    private String titre;
    private Integer version;
    
    private List<AssignationDTO> assignations = new ArrayList<>();

}
