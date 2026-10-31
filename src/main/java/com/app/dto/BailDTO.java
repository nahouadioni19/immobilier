package com.app.dto;

import com.app.enums.StatutBail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BailDTO {
    private Integer id;
    private Long total;
    private String statut;   // <-- en String maintenant
    private String locataireNom;
    //private String locatairePrenom;
    //private String appartementNum;
    private String appartementLibelle;
    private String immeubleLibelle;
    /*private String adresse;*/
    //private String bailleurNom;
    private String bailleurPrenom;
    
    private Long montantLoyer;

    // constructeur avec statut.name() côté service
    public BailDTO(Integer id, Long total, StatutBail statut, String locataireNom,
                   String appartementLibelle,
                    String immeubleLibelle,  String bailleurPrenom, Long montantLoyer) {
        this.id = id;
        this.total = total;
        this.statut = statut != null ? statut.name() : null; // conversion enum -> String
        this.locataireNom = locataireNom;
        this.appartementLibelle = appartementLibelle;
        this.immeubleLibelle = immeubleLibelle;
        this.bailleurPrenom = bailleurPrenom;
        this.montantLoyer = montantLoyer;
    }
}