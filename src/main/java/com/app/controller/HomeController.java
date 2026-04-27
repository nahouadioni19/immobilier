package com.app.controller;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
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
import com.app.dto.DashboardDTO;
import com.app.enums.StatutBail;
import com.app.repositories.recouvre.BailRepository;
import com.app.repositories.recouvre.EncaisseRepository;
import com.app.repositories.recouvre.LocataireRepository;
import com.app.security.UserPrincipal;
import com.app.service.DashboardRecouvreService;
import com.app.service.common.CredentialsService;
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
    
    private final DashboardRecouvreService dashboardRecouvreService;

    private static final String BACK_URL = "BACK_URL";
    private static final String AGRO = "AGRO";

    @Value("${server.servlet.context-path}")
    private String appContext;

    @PostMapping(Routes.ROUTE_SUCCES_LOGIN)
    public String succesForwardLogin(Model model, HttpServletRequest request,
                                     @RequestHeader(value = "referer", required = false) final String referer) {
        return credentialsService.loggedUserRoleHandler(request);
    }

    @GetMapping(value = Routes.ROUTE_HOME)
    public String accueil(Model model, @RequestParam Map<String, String> params) {
        setup.getPages().doStack(setup.getPages().getData(), AGRO, BACK_URL, Routes.ROUTE_HOME);

        model.addAttribute(Constants.CURR_PAGE, "home");
        model.addAttribute("context", appContext);

        // Chargement des stats tableau de bord
        long totalBaux = bailRepository.count();
        long totalLocataires = locataireRepository.count();
        long totalRecouvrements = encaisseRepository.count();
        long totalArrieres = bailRepository.countByStatut(StatutBail.EN_RETARD); // exemple

        model.addAttribute("totalBaux", totalBaux);
        model.addAttribute("totalLocataires", totalLocataires);
        model.addAttribute("totalRecouvrements", totalRecouvrements);
        model.addAttribute("totalArrieres", totalArrieres);

        // Exemple données pour le graphique
        model.addAttribute("mois", new String[]{"Jan", "Fév", "Mar", "Avr", "Mai", "Juin"});
        model.addAttribute("loyers", new int[]{200000, 180000, 220000, 250000, 210000, 230000});
        
        DashboardDTO stats = dashboardRecouvreService.getDashboard();

        model.addAttribute("stats", stats);

        setup.allCommon(model);

        return Pages.PAGE_HOME; // correspond à home.html via ton Pages
    }

    @GetMapping("/accueil")
    public String accueilAlias(Model model, @RequestParam Map<String, String> params) {
        return accueil(model, params);
    }
    
    @ModelAttribute("currentUserFullName")
    public String currentUserFullName(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return userPrincipal != null ? userPrincipal.getFullName() : "Utilisateur";
    }
}
