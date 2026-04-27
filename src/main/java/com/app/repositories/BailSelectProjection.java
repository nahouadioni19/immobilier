package com.app.repositories;

import java.time.LocalDate;

import com.app.enums.StatutBail;

public interface BailSelectProjection {
    Integer getId();
    String getLocnom();
    String getLocprenom();
    String getLoctel();
    String getLocemail();
    String getUsglibelle();
    Long getMontantloyer();
    String getBailibelle();
    String getNumero();
    LocalDate getDerniereDatePaiement();
    LocalDate getDateDebut();
    StatutBail getStatut(); // ✅ AJOUT IMPORTANT
}

