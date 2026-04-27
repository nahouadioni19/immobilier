package com.app.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface EncaisseListDto {

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
}