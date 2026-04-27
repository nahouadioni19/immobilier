package com.app.controller.common;

import java.time.LocalDate;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.app.entities.administration.Agence;
import com.app.entities.administration.Utilisateur;
import com.app.security.UserPrincipal;
import com.app.service.AgenceSecurityService;
import com.app.utils.Constants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AgenceInterceptor implements HandlerInterceptor {

    private final AgenceSecurityService agenceSecurityService;

    public AgenceInterceptor(AgenceSecurityService agenceSecurityService) {
        this.agenceSecurityService = agenceSecurityService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            return true;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof UserPrincipal userPrincipal) {

            Utilisateur user = userPrincipal.getUtilisateur();

            // =========================
            // 🔥 ADMIN TECHNIQUE (BY PASS TOTAL)
            // =========================
            if (user != null &&
                Constants.DEFAULT_USER_NAME.equalsIgnoreCase(user.getUsername())) {
                return true;
            }

            Agence agence = user.getAgence();

            if (agence == null) {
                return true;
            }

            // =========================
            // ❌ AGENCE BLOQUÉE
            // =========================
            if (Boolean.TRUE.equals(agence.getBloque())) {
                response.sendRedirect(request.getContextPath() + "/login?error=blocked");
                return false;
            }

            // =========================
            // ❌ ABONNEMENT EXPIRÉ
            // =========================
            if (agence.getDateFinAbonnement() != null &&
                LocalDate.now().isAfter(agence.getDateFinAbonnement())) {

                response.sendRedirect(request.getContextPath() + "/paiement/expired");
                return false;
            }
        }

        return true;
    }
}

/*@Component
public class AgenceInterceptor implements HandlerInterceptor {

    private final AgenceSecurityService agenceSecurityService;
    
    public AgenceInterceptor(AgenceSecurityService agenceSecurityService) {
        this.agenceSecurityService = agenceSecurityService;
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

            Utilisateur user = userPrincipal.getUtilisateur();

            // =========================
            // 🔥 1. ADMIN TECHNIQUE (hors BDD)
            // =========================
            if (user != null && Constants.DEFAULT_USER_NAME.equalsIgnoreCase(user.getUsername())) {
                return true; // 🚀 bypass total
            }

            Agence agence = user.getAgence();

            if (agence != null) {

                // =========================
                // ❌ 2. AGENCE BLOQUÉE
                // =========================
                if (Boolean.TRUE.equals(agence.getBloque())) {
                    response.sendRedirect("/login?error=blocked");
                    return false;
                }

                // =========================
                // ❌ 3. ABONNEMENT EXPIRÉ
                // =========================
                if (agence.getDateFinAbonnement() != null &&
                    LocalDate.now().isAfter(agence.getDateFinAbonnement())) {

                    response.sendRedirect(request.getContextPath() + "/paiement/expired");
                    return false;
                }
            }
        }

        return true;
    }*/

    /*@Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return true;
        }

        Object principal = auth.getPrincipal();

        // ✅ CAS ADMIN TECHNIQUE (hors BDD)
        if (principal instanceof UserPrincipal userPrincipal) {

            Utilisateur user = userPrincipal.getUtilisateur();

            if (user != null && Constants.DEFAULT_USER_NAME.equalsIgnoreCase(user.getUsername())) {
                return true; // 🔥 ON LAISSE PASSER
            }

            // 🔐 CHECK NORMAL POUR LES AUTRES
          //  checkAgenceActive(user.getAgence());
            agenceSecurityService.checkAgenceActive(user.getAgence());
        }

        return true;
    }*/
    
    /*@Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof UserPrincipal principal) {

            Agence agence = principal.getUtilisateur().getAgence();

            try {
                agenceSecurityService.checkAgenceActive(agence);

            } catch (Exception e) {

                // 🔥 Déconnexion immédiate
                SecurityContextHolder.clearContext();
                request.getSession().invalidate();

                // 🔥 Redirection login avec message
                response.sendRedirect("/login?expired=true");
                return false;
            }
        }

        return true;
    }*/
//}