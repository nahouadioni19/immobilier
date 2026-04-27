package com.app.controller.common;

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
        if ("admpg".equalsIgnoreCase(username)) return "Administrateur";

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
        if ("admpg".equalsIgnoreCase(username)) return Set.of("ADMIN");

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

}