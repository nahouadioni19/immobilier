package com.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EncaisseListDto {

    private Integer id;
    private LocalDate encDate;
    private Long encMontant;
    private String encMode;

    private String locataireNom;
    private String locatairePrenom;

    private String appartementNumero;

    private Integer utilisateurId;
    private String utilisateurNom;
    private String utilisateurPrenoms;

    public EncaisseListDto(
            Integer id,
            LocalDate encDate,
            Long encMontant,
            String encMode,
            String locataireNom,
            String locatairePrenom,
            String appartementNumero,
            Integer utilisateurId,
            String utilisateurNom,
            String utilisateurPrenoms
    ) {
        this.id = id;
        this.encDate = encDate;
        this.encMontant = encMontant;
        this.encMode = encMode;
        this.locataireNom = locataireNom;
        this.locatairePrenom = locatairePrenom;
        this.appartementNumero = appartementNumero;
        this.utilisateurId = utilisateurId;
        this.utilisateurNom = utilisateurNom;
        this.utilisateurPrenoms = utilisateurPrenoms;
    }
}




/*package com.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EncaisseListDto {
	
	private Integer id;
    private LocalDate encDate;
    private Long encMontant;
    private String encMode;

    private String locataireNom;
    private String locatairePrenom;

    private String appartementNumero;

    private Integer utilisateurId;
    private String utilisateurNom;
    private String utilisateurPrenoms;
    
    Integer getId();
    
    LocalDate getEncDate();

    BigDecimal getEncMontant();

    String getEncMode();

    String getLocataireNom();

    String getLocatairePrenom();

    String getAppartementNumero();
    
    String getUtilisateurNom();

    String getUtilisateurPrenoms();
    
    Integer getUtilisateurId();
}*/