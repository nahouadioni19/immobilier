package com.app.controller.recouvre;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.app.security.UserPrincipal; // à adapter selon le package où tu as défini UserPrincipal

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.controller.common.Routes;
import com.app.controller.common.SetupPage;
import com.app.dto.BailDTO;
import com.app.dto.ImmeubleDTO;
import com.app.entities.recouvre.Appartement;
import com.app.entities.recouvre.Bail;
import com.app.entities.recouvre.Locataire;
import com.app.repositories.BailSelectProjection;
import com.app.service.common.PaginationService;
import com.app.service.recouvre.AppartementService;
import com.app.service.recouvre.BailService;
import com.app.service.recouvre.LocataireService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(Routes.ROUTE_BAIL)
public class BailController {

    private final BailService service;
    private final LocataireService locataireService;
    private final AppartementService appartementService;
    private final PaginationService paginationService;
    private final SetupPage setup;
    
    @Autowired
	private MessageSource messageSource;
    
    public BailController(SetupPage setup, PaginationService paginationService, BailService service, 
    			LocataireService locataireService, AppartementService appartementService) {
        this.service = service;
        this.locataireService = locataireService;
        this.appartementService = appartementService;
        this.setup = setup;
        this.paginationService = paginationService;
    }
    
    // Liste initiale + pagination
    @GetMapping
    public String listBails(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<BailDTO> bailsPage = service.search("", pageable);

        model.addAttribute("bailsPage", bailsPage);

        return "locataire/bail/list";
    }
    /*@GetMapping
    public String listImmeubs(
            Model model,
            @RequestParam(defaultValue = "0") int page,   // page en base 0
            @RequestParam(defaultValue = "8") int size,  // taille de page
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request) {

     // Création de l'objet Pageable avec tri descendant sur l'id
        Pageable pageable = PageRequest.of(page, size, Sort.by("idt").descending());
        
        // On récupère une Page de BailSelectProjection via le service
        Page<BailSelectProjection> bailsPage = service.findBailDetails(principal, pageable);

        // Attributs pour Thymeleaf
        model.addAttribute("bailsPage", bailsPage);
        model.addAttribute("bails", bailsPage.getContent());           // la liste pour le template
        model.addAttribute("currentPage", page + 1);                  // affichage base 1
        model.addAttribute("totalPages", bailsPage.getTotalPages());  // nombre total de pages
        model.addAttribute("currentUri", request.getRequestURI());

        return "locataire/bail/list"; // template Thymeleaf
    }*/

    @GetMapping("/create")
    public String showBailForm(Model model) {
        Bail bail = new Bail();
        bail.setLocataire(new Locataire()); // 🔥 important
        bail.setAppartement(new Appartement());

        model.addAttribute("bail", bail);
        return "locataire/bail/form";
    }
    

    @PostMapping("/save")
    public String saveBail(
            @ModelAttribute Bail bail,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        boolean isNew = (bail.getId() == null);

        try {
            // 🔹 Validation côté serveur
            if (bail.getLocataire() == null || bail.getLocataire().getId() == null) {
                throw new RuntimeException("Veuillez sélectionner un locataire");
            }

            if (bail.getAppartement() == null || bail.getAppartement().getId() == null) {
                throw new RuntimeException("Veuillez sélectionner un appartement");
            }

            // 🔹 Récupérer les entités complètes à partir des IDs
            bail.setLocataire(
                locataireService.findById(bail.getLocataire().getId())
                    .orElseThrow(() -> new RuntimeException("Locataire introuvable"))
            );

            bail.setAppartement(
                appartementService.findById(bail.getAppartement().getId())
                    .orElseThrow(() -> new RuntimeException("Appartement introuvable"))
            );

            // 👉 Laisser le service gérer la logique métier
            if (isNew) {
                service.creerBail(bail);
            } else {
                service.modifierBail(bail);
            }

            // 🔹 Message succès i18n
            String successMessage = messageSource.getMessage(
                    isNew ? "success.enregistrement" : "success.modification",
                    null,
                    LocaleContextHolder.getLocale()
            );
            redirectAttributes.addFlashAttribute("successMessage", successMessage);

            return "redirect:" + Routes.ROUTE_BAIL;

        } catch (Exception e) {
            // 🔹 Préparer le formulaire avec les données actuelles et message d'erreur
            model.addAttribute("bail", bail);
            model.addAttribute("errorMessage", e.getMessage());

            // 🔹 Injecter currentLocataire/currentAppartement pour Select2
            if (bail.getLocataire() != null && bail.getLocataire().getId() != null) {
                model.addAttribute("currentLocataire",
                        locataireService.findById(bail.getLocataire().getId()).orElse(null));
            }

            if (bail.getAppartement() != null && bail.getAppartement().getId() != null) {
                model.addAttribute("currentAppartement",
                        appartementService.findById(bail.getAppartement().getId()).orElse(null));
            }

            return "locataire/bail/form";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {

        // 🔹 Charger le bail avec le locataire et l'appartement courant (JOIN FETCH)
        Bail bail = service.findByIdWithLocataireAndAppartement(id)
            .orElseThrow(() -> new IllegalArgumentException("Bail introuvable : " + id));

        // ⚡ Préparer le bail dans le modèle
        model.addAttribute("bail", bail);

        // 🔹 Préparer les options pour le select (uniquement l’élément courant)
        if (bail.getLocataire() != null) {
            model.addAttribute("currentLocataire", bail.getLocataire());
        }
        if (bail.getAppartement() != null) {
            model.addAttribute("currentAppartement", bail.getAppartement());
        }

        return "locataire/bail/form";
    }

    // SUPPRESSION
    @GetMapping("/delete/{id}")
    public String deleteBail(@PathVariable("id") int id, RedirectAttributes redirectAttrs) {
        service.delete(id);
        
        redirectAttrs.addFlashAttribute("successMessage", "Suppression effectuée avec succès !");
        
        return "redirect:" + Routes.ROUTE_BAIL;
    }
    
    //RESILIER
    @GetMapping("/resilier/{id}")
    public String resilierBail(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes
    ) {

        service.resilier(id);

        redirectAttributes.addFlashAttribute(
            "successMessage",
            "Bail résilié avec succès"
        );

        return "redirect:" + Routes.ROUTE_BAIL;
    }
    
    @GetMapping(value = "/api/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<BailDTO> search(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 8) Pageable pageable) {

        return service.search(keyword, pageable);
    }
    
    @GetMapping("/bail/{bailId}/arretes")
    @ResponseBody
    public Map<String, Object> getBailArretes(@PathVariable Integer bailId) {
        Bail bail = service.findById(bailId)
                .orElseThrow(() -> new RuntimeException("Bail introuvable"));

        Map<String, Object> result = new HashMap<>();
        
        LocalDate debut = bail.getDateDebut(); // 05-12-2025
        LocalDate today = LocalDate.now();
        
        long monthsArrieres = 0;
        if (today.isAfter(debut)) {
            monthsArrieres = ChronoUnit.MONTHS.between(
                debut.withDayOfMonth(1), today.withDayOfMonth(1)
            );
        }

        Long loyerMensuel = bail.getMontantLoyer();
        Long totalArriere = monthsArrieres * loyerMensuel;

        result.put("loyer", loyerMensuel);
        result.put("monthsArrieres", monthsArrieres);
        result.put("totalArriere", totalArriere);

        return result;
    }
    
    /*@GetMapping(value = "/api/bails", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<BailSelectProjection> getBails(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Long agentId,
            @RequestParam(required = false) String keyword) {

        return service.findBailDetailsNative(principal, agentId,keyword);
    }*/
    
    @GetMapping(value = "/api/bails", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<BailSelectProjection> getBails(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Long agentId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer id) {

        return service.findBailDetailsNative(principal, agentId, keyword, id);
    }
    
}

