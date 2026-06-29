package com.app.controller.common;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.app.entities.administration.Assignation;
import com.app.entities.administration.Utilisateur;
import com.app.security.UserPrincipal;
import com.app.service.administration.UtilisateurService;

@ControllerAdvice
public class GlobalModelAttributes {

    private final UtilisateurService utilisateurService;

    public GlobalModelAttributes(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    // =========================
    // URI courante
    // =========================
    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    // =========================
    // Nom utilisateur
    // =========================
    @ModelAttribute("currentUserFullName")
    public String currentUserFullName(Authentication authentication) {

        if (authentication == null
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return "Invité";
        }

        Utilisateur user = principal.getUtilisateur();

        String prenom = user.getPrenoms() != null ? user.getPrenoms() : "";
        String nom = user.getNom() != null ? user.getNom() : "";

        String fullName = (prenom + " " + nom).trim();

        return fullName.isEmpty() ? user.getUsername() : fullName;
    }
    
   /* @ModelAttribute("currentUserFullName")
    public String currentUserFullName(Principal principal) {

        if (principal == null) return "Invité";

        String username = principal.getName();

        if ("admin".equalsIgnoreCase(username)) {
            return "Super Administrateur";
        }

        List<Utilisateur> users = utilisateurService.findByUsername(username);
        if (users.isEmpty()) return username;

        Utilisateur user = users.get(0);

        String prenom = user.getPrenoms() != null ? user.getPrenoms() : "";
        String nom = user.getNom() != null ? user.getNom() : "";

        return ((prenom + " " + nom).trim());
    }*/

    // =========================
    // Rôles (basé BDD)
    // =========================
    @ModelAttribute("userRoles")
    public Set<String> userRoles(Authentication authentication) {

        if (authentication == null
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return Set.of();
        }

        return principal.getUtilisateur()
                .getAssignations()
                .stream()
                .map(Assignation::getRole)
                .filter(r -> r != null && r.getLibelle() != null)
                .map(r -> mapRoleToMenu(r.getLibelle()))
                .collect(Collectors.toSet());
    }
    
    /*@ModelAttribute("userRoles")
    public Set<String> userRoles(Principal principal) {

        if (principal == null) return Set.of();

        String username = principal.getName();

        if ("admin".equalsIgnoreCase(username)) {
            return Set.of("SUPER_ADMIN");
        }

        List<Utilisateur> users = utilisateurService.findByUsername(username);
        if (users.isEmpty()) return Set.of();

        return users.get(0)
                .getAssignations()
                .stream()
                .map(Assignation::getRole)
                .filter(r -> r != null && r.getLibelle() != null)
                .map(r -> mapRoleToMenu(r.getLibelle()))
                .collect(Collectors.toSet());
    }*/

    // =========================
    // Assignations courantes (Spring Security)
    // =========================
    @ModelAttribute("userAssignations")
    public List<Assignation> userAssignations(Authentication authentication) {

        if (authentication == null
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return Collections.emptyList();
        }

        return principal.getUtilisateur()
                .getAssignations()
                .stream()
               // .filter(Assignation::isCourant)
                .toList();
    }

    
    @ModelAttribute("currentAssignationId")
    public Long currentAssignationId(HttpSession session) {

        Object value = session.getAttribute("CURRENT_ASSIGNATION_ID");

        if (value == null) return null;

        if (value instanceof Integer i) {
            return i.longValue();
        }

        if (value instanceof Long l) {
            return l;
        }

        return null;
    }
    
    @ModelAttribute("currentAssignation")
    public Assignation currentAssignation(Authentication authentication) {

        if (authentication == null) {
            return null;
        }

        if (!(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return null;
        }

        return principal.getAssignationCourant();
    }

    /*@ModelAttribute("currentRole")
    public String currentRole(HttpSession session) {
        return (String) session.getAttribute("CURRENT_ROLE");
    }*/

    @ModelAttribute("currentRole")
    public String currentRole(Authentication authentication) {

        if (authentication == null) {
            return null;
        }

        if (!(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return null;
        }

        Assignation a = principal.getAssignationCourant();

        return (a != null && a.getRole() != null)
                ? a.getRole().getLibelle()
                : null;
    }
    
    // =========================
    // Connexion
    // =========================
    @ModelAttribute("isConnected")
    public boolean isConnected(Principal principal) {
        return principal != null;
    }

    // =========================
    // Mapping rôle
    // =========================
    private String mapRoleToMenu(String libelleBdd) {

        if (libelleBdd == null) return "";

        return switch (libelleBdd.trim().toUpperCase()) {
            case "SUPER_ADMIN" -> "SUPER_ADMIN";
            case "ADMIN" -> "ADMIN";
            case "RECOUV" -> "RECOUV";
            case "SECRET" -> "SECRET";
            case "DIREC" -> "DIREC";
            case "CONTRO" -> "CONTRO";
            case "AGENT" -> "AGENT";
            default -> libelleBdd.trim().toUpperCase();
        };
    }
    
    @ModelAttribute("currentUser")
    public UserPrincipal currentUser(Authentication authentication) {

        if (authentication == null) {
            return null;
        }

        if (!(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return null;
        }

        return principal;
    }
}

/*package com.app.controller.common;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.app.entities.administration.Utilisateur;
import com.app.entities.administration.Assignation;
import com.app.service.administration.UtilisateurService;

@ControllerAdvice
public class GlobalModelAttributes {

    private final UtilisateurService utilisateurService;

    public GlobalModelAttributes(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    // 🔹 URI courante (utile pour surligner le menu actif)
    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    // 🔹 Nom complet de l'utilisateur connecté
    @ModelAttribute("currentUserFullName")
    public String currentUserFullName(Principal principal) {
        if (principal == null) return "Invité";

        String username = principal.getName();
       // if ("admpg".equalsIgnoreCase(username)) return "Administrateur";
        if ("admin".equalsIgnoreCase(username)) return "Super Administrateur";

        List<Utilisateur> users = utilisateurService.findByUsername(username);
        if (users.isEmpty()) return username;

        Utilisateur user = users.get(0);
        String prenom = user.getPrenoms() != null ? user.getPrenoms() : "";
        String nom = user.getNom() != null ? user.getNom() : "";
        String fullName = (prenom + " " + nom).trim();
        return fullName.isEmpty() ? username : fullName;
    }

    // 🔹 Rôles de l'utilisateur (pour Thymeleaf)
    @ModelAttribute("userRoles")
    public Set<String> userRoles(Principal principal) {
        if (principal == null) return Set.of();

        String username = principal.getName();
      //  if ("admpg".equalsIgnoreCase(username)) return Set.of("ADMIN");
        if ("admin".equalsIgnoreCase(username)) return Set.of("SUPER_ADMIN");

        List<Utilisateur> users = utilisateurService.findByUsername(username);
        if (users.isEmpty()) return Set.of();

        Utilisateur user = users.get(0);

        return user.getAssignations().stream()
                .map(Assignation::getRole)
                .filter(r -> r != null && r.getLibelle() != null)
                .map(r -> mapRoleToMenu(r.getLibelle()))
                .collect(Collectors.toSet());
    }

    // 🔹 Convertit le rôle BDD vers celui attendu par l’UI
    private String mapRoleToMenu(String libelleBdd) {
        switch (libelleBdd.trim().toUpperCase()) {
        	case "SUPER_ADMIN": return "SUPER_ADMIN";
            case "ADMIN": return "ADMIN";
            case "RECOUV": return "RECOUV";
            case "SECRET": return "SECRET";
            case "DIREC": return "DIREC";
            case "CONTRO": return "CONTRO";
            case "AGENT": return "AGENT";
            default: return libelleBdd.trim().toUpperCase();
        }
    }

    // 🔹 Statut connecté
    @ModelAttribute("isConnected")
    public boolean isConnected(Principal principal) {
        return principal != null;
    }

}*/