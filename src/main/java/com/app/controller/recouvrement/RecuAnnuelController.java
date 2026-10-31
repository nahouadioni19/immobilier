package com.app.controller.recouvrement;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.controller.common.Routes;
import com.app.entities.recouvre.Encaisse;
import com.app.entities.recouvre.Locataire;
import com.app.repositories.recouvre.EncaisseRepository;
import com.app.repositories.recouvre.LocataireRepository;
import com.app.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(Routes.ROUTE_RECOUVRE)
@RequiredArgsConstructor
public class RecuAnnuelController {

    private final EncaisseRepository encaisseRepository;
    private final LocataireRepository locataireRepository;

    @GetMapping("/recus-annuels")
    public String afficherRecusAnnuels(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Integer locataireId,
            @RequestParam(required = false) Integer annee,
            Model model) {

        // 1. Agence de l'utilisateur connecté
        if (principal == null
                || principal.getUtilisateur() == null
                || principal.getUtilisateur().getAgence() == null) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Aucune agence associée à l'utilisateur connecté."
            );
        }

        Integer agenceId =
                principal.getUtilisateur().getAgence().getId();

        // 2. Année sélectionnée
        if (annee == null) {
            annee = LocalDate.now().getYear();
        }

        if (annee < 2000 || annee > LocalDate.now().getYear()) {
            throw new IllegalArgumentException(
                    "L'année sélectionnée est invalide."
            );
        }

        // 3. Liste des locataires de l'agence
        List<Locataire> locataires =
                encaisseRepository.findLocatairesAvecEncaissements(
                        agenceId
                );

        // 4. Initialisation des résultats
        List<Encaisse> encaisses = List.of();
        long totalAnnuel = 0L;

        // 5. Recherche des reçus du locataire
        if (locataireId != null) {

            encaisses =
                    encaisseRepository.findRecusAnnuelsParLocataire(
                            agenceId,
                            locataireId,
                            LocalDate.of(annee, 1, 1),
                            LocalDate.of(annee, 12, 31)
                    );

            totalAnnuel = encaisses.stream()
                    .map(Encaisse::getEncMontant)
                    .filter(montant -> montant != null)
                    .mapToLong(Long::longValue)
                    .sum();
        }

        if (locataireId != null) {
            locataireRepository.findById(locataireId).ifPresent(l ->
                model.addAttribute("locataireNom", l.getPrenom() + " " + l.getNom())
            );
        }
        
        model.addAttribute("locataires", locataires);
        model.addAttribute("encaisses", encaisses);
        model.addAttribute("locataireId", locataireId);
        model.addAttribute("annee", annee);
        model.addAttribute("totalAnnuel", totalAnnuel);

        return "recouvrement/recus-annuels";
    }
}