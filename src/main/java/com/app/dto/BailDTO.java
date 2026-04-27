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
    private String locatairePrenom;
    private String appartementNum;
    private String appartementLibelle;

    // constructeur avec statut.name() côté service
    public BailDTO(Integer id, Long total, StatutBail statut, String locataireNom,
                   String locatairePrenom, String appartementNum, String appartementLibelle) {
        this.id = id;
        this.total = total;
        this.statut = statut != null ? statut.name() : null; // conversion enum -> String
        this.locataireNom = locataireNom;
        this.locatairePrenom = locatairePrenom;
        this.appartementNum = appartementNum;
        this.appartementLibelle = appartementLibelle;
    }
}