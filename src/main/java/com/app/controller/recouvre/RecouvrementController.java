package com.app.controller.recouvre;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.app.dto.RecouvrementDTO;
import com.app.service.recouvre.RecouvrementService;
import com.app.utils.Constants;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("encaissements")
public class RecouvrementController {

    private final RecouvrementService recouvrementService;

    public RecouvrementController(RecouvrementService recouvrementService) {
        this.recouvrementService = recouvrementService;
    }
    
    @GetMapping
    public String listeRecouvrements(
            @RequestParam(value = "mois", required = false) Integer mois,
            @RequestParam(value = "annee", required = false) Integer annee,
            @RequestParam(value = "locataireId", required = false) Long locataireId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model,
            HttpServletRequest request
    ) {
        int pageSize = 8; // nombre de lignes par page

        // Valeurs par défaut : mois et année actuels
        LocalDate today = LocalDate.now();
        int currentMois = (mois != null) ? mois : today.getMonthValue();
        int currentAnnee = (annee != null) ? annee : today.getYear();

        // 🔹 Appel du service avec pagination
        Page<RecouvrementDTO> recouvrementsPage =
                recouvrementService.getRecouvrementsPage(currentMois, currentAnnee, PageRequest.of(page, pageSize));

        // Ajouter les données pour le template
        model.addAttribute("recouvrementsPage", recouvrementsPage);
        model.addAttribute("recouvrements", recouvrementsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", recouvrementsPage.getTotalPages());
        model.addAttribute("currentUri", request.getRequestURI());

        model.addAttribute("mois", currentMois);
        model.addAttribute("annee", currentAnnee);

        // Totaux pour l'ensemble de la page affichée
        long totalLoyer = recouvrementsPage.getContent().stream().mapToLong(r -> r.getLoyerMensuel()).sum();
        long totalPaye = recouvrementsPage.getContent().stream().mapToLong(r -> r.getMontantPaye()).sum();
        long totalReste = recouvrementsPage.getContent().stream().mapToLong(r -> r.getReste()).sum();

        model.addAttribute("totalLoyer", totalLoyer);
        model.addAttribute("totalPaye", totalPaye);
        model.addAttribute("totalReste", totalReste);
        
        model.addAttribute("moisList", Constants.MOIS);
        model.addAttribute("moisIndex", mois != null ? mois : LocalDate.now().getMonthValue());
        model.addAttribute("selectedLocataire", locataireId);
        model.addAttribute("currentYear", LocalDate.now().getYear());

        return "encaissement/list";
    }
    
}