package com.app.service;

import java.time.LocalDate;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;

import com.app.entities.administration.Agence;

@Service
public class AgenceSecurityService {

    public void checkAgenceActive(Agence agence) {

        if (agence == null) {
            throw new DisabledException("Agence invalide");
        }

        if (Boolean.TRUE.equals(agence.getBloque())) {
            throw new LockedException("Agence bloquée");
        }

        if (agence.getDateFinAbonnement() != null &&
            LocalDate.now().isAfter(agence.getDateFinAbonnement())) {

            throw new DisabledException("Abonnement expiré");
        }
    }
}