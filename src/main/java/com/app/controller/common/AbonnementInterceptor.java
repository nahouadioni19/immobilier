package com.app.controller.common;

import java.time.LocalDate;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.app.entities.administration.Agence;
import com.app.security.UserPrincipal;
import com.app.service.administration.AgenceService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AbonnementInterceptor implements HandlerInterceptor {

    private final AgenceService agenceService;

    public AbonnementInterceptor(AgenceService agenceService) {
        this.agenceService = agenceService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return true;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof UserPrincipal userPrincipal) {

            Agence agence = userPrincipal.getUtilisateur().getAgence();

            if (agence != null &&
                agence.getDateFinAbonnement() != null &&
                LocalDate.now().isAfter(agence.getDateFinAbonnement())) {

                response.sendRedirect("/abonnement/renouveler");
                return false;
            }
        }

        return true;
    }
}