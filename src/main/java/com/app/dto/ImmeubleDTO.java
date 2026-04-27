package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ImmeubleDTO {
    private Integer id;
    private String nomImmeuble;
    private String codeImmeuble;
    
    private String bailleurNom;
    private String bailleurPrenom;
    
    private String utilisateurNom;
    private String utilisateurPrenoms;
}
