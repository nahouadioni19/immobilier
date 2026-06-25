package com.app.controller;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.controller.common.Pages;
import com.app.controller.common.Routes;
import com.app.controller.common.SetupPage;
import com.app.controller.referentiel.DashboardController;
import com.app.dto.DashboardDTO;
import com.app.dto.DashboardGlobalDTO;
import com.app.enums.StatutBail;
import com.app.repositories.administration.AgenceRepository;
import com.app.repositories.recouvre.BailRepository;
import com.app.repositories.recouvre.EncaisseRepository;
import com.app.repositories.recouvre.LocataireRepository;
import com.app.security.UserPrincipal;
import com.app.service.DashboardRecouvreService;
import com.app.service.common.CredentialsService;
import com.app.service.recouvre.DashboardService;
import com.app.utils.Constants;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private static final String MESSAGE_ERREUR = "message_erreur";
    private final CredentialsService credentialsService;
    private final SetupPage setup;
    
    private final BailRepository bailRepository;
    private final LocataireRepository locataireRepository;
    private final EncaisseRepository encaisseRepository;
    
    @Autowired
    private AgenceRepository agenceRepository;
    
    private final DashboardRecouvreService dashboardRecouvreService;
    private final DashboardService dashboardService;

    private static final String BACK_URL = "BACK_URL";
    private static final String AGRO = "AGRO";

    private static final Logger log =
            LoggerFactory.getLogger(DashboardController.class);
    
    @Value("${server.servlet.context-path}")
    private String appContext;

    @PostMapping(Routes.ROUTE_SUCCES_LOGIN)
    public String succesForwardLogin(Model model, HttpServletRequest request,
                                     @RequestHeader(value = "referer", required = false) final String referer) {
        return credentialsService.loggedUserRoleHandler(request);
    }
    
    @GetMapping(value = Routes.ROUTE_HOME)
    public String accueil(Model model,
                          @RequestParam Map<String, String> params,
                          @AuthenticationPrincipal UserPrincipal principal) {

    	boolean adminSansAgence =
    	        principal != null
    	        && principal.getUtilisateur() != null
    	        && principal.getUtilisateur().getAgence() == null;

    	if (adminSansAgence) {

    	    model.addAttribute(
    	            "agences",
    	            agenceRepository.findAll()
    	    );

    	    model.addAttribute(
    	            "selectedAgenceId",
    	            params.get("agenceId")
    	    );
    	}
    	
        Integer agenceId = null;

        if (principal != null
                && principal.getUtilisateur() != null
                && principal.getUtilisateur().getAgence() != null) {

            // Agent ou directeur
            agenceId = principal.getUtilisateur()
                                .getAgence()
                                .getId();

        } else {

            // Admin global
            String agenceParam = params.get("agenceId");

            if (agenceParam != null && !agenceParam.isBlank()) {
                agenceId = Integer.valueOf(agenceParam);
            }
        }

        log.info("agenceId = {}", agenceId);

        setup.getPages().doStack(
                setup.getPages().getData(),
                AGRO,
                BACK_URL,
                Routes.ROUTE_HOME);

        model.addAttribute(Constants.CURR_PAGE, "home");
        model.addAttribute("context", appContext);

        // Statistiques générales
        long totalBaux = bailRepository.count();
        long totalLocataires = locataireRepository.count();
        long totalRecouvrements = encaisseRepository.count();
        long totalArrieres = 0L;

        model.addAttribute("totalBaux", totalBaux);
        model.addAttribute("totalLocataires", totalLocataires);
        model.addAttribute("totalRecouvrements", totalRecouvrements);
        model.addAttribute("totalArrieres", totalArrieres);

        // Données exemple graphique
        model.addAttribute(
                "mois",
                new String[]{"Jan", "Fév", "Mar", "Avr", "Mai", "Juin"});

        model.addAttribute(
                "loyers",
                new int[]{200000, 180000, 220000, 250000, 210000, 230000});

        DashboardDTO stats = dashboardRecouvreService.getDashboard();
        model.addAttribute("stats", stats);
        
        DashboardGlobalDTO dashboard =
                (agenceId != null)
                        ? dashboardService.getDashboardGlobal(agenceId)
                        : DashboardGlobalDTO.empty();

        model.addAttribute("dashboard", dashboard);
        
        
        if (agenceId != null) {

            Map<String, Object> graphData =
                    dashboardService.getEncaissementsMensuels(agenceId);

            model.addAttribute(
                    "moisLabels",
                    graphData.get("labels"));

            model.addAttribute(
                    "encaissements",
                    graphData.get("montants"));

        } else {

            model.addAttribute(
                    "moisLabels",
                    List.of(
                            "Jan","Fév","Mar","Avr",
                            "Mai","Juin","Juil","Août",
                            "Sep","Oct","Nov","Déc"));

            model.addAttribute(
                    "encaissements",
                    List.of(
                            0,0,0,0,0,0,
                            0,0,0,0,0,0));
        }
        
        
        setup.allCommon(model);

        return Pages.PAGE_HOME;
    }

    @GetMapping("/accueil")
    public String accueilAlias(Model model, @RequestParam Map<String, String> params,
    		@AuthenticationPrincipal UserPrincipal principal) {
        return accueil(model, params, principal);
    }
    
    @ModelAttribute("currentUserFullName")
    public String currentUserFullName(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return userPrincipal != null ? userPrincipal.getFullName() : "Utilisateur";
    }
}
