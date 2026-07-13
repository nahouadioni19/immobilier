package com.app.controller.maintenance;

import com.app.controller.common.Routes;
import com.app.entities.maintenance.Prestataire;
import com.app.security.UserPrincipal;
import com.app.service.maintenance.PrestataireService;

import com.app.dto.PrestataireDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Controller
@RequestMapping(Routes.ROUTE_PRESTATAIRE)
public class PrestataireController {

    private final PrestataireService service;
    
    @Value("${app.storage.directory}")
    private String storageDirectory;  // <-- injecté depuis application.properties

    @Autowired
	private MessageSource messageSource;
    
    public PrestataireController(PrestataireService service) {
        this.service = service;
    }

    @GetMapping
    public String listPrestataires(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @AuthenticationPrincipal UserPrincipal principal,
            Model model) {

        //Integer agenceId = principal.getUtilisateur().getAgence().getId();

        Page<PrestataireDTO> prestatairesPage =
                service.search(keyword, PageRequest.of(page, 8));

        model.addAttribute("prestatairesPage", prestatairesPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);

        return "prestataire/enrolement/list";
    }
   
    // FORMULAIRE DE CREATION
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("prestataire", new Prestataire());
        return "prestataire/enrolement/form";
    }
      
    @PostMapping("/save")
    public String savePrestataire(
            @ModelAttribute("prestataire") Prestataire prestataire,
            RedirectAttributes redirectAttributes) {

        boolean isNew = (prestataire.getId() == null);

        try {
        	Prestataire saved = service.saveWithDocument(prestataire);
        	System.out.println("Prestataire enregistré avec ID = " + saved.getId());


            String successMessage = messageSource.getMessage(
                    isNew ? "success.enregistrement" : "success.modification",
                    null,
                    LocaleContextHolder.getLocale()
            );

            redirectAttributes.addFlashAttribute("successMessage", successMessage);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erreur inattendue : " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:" + Routes.ROUTE_PRESTATAIRE;
    }
    

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {
    	
    	Prestataire prestataire = service.findByIdAgence(id)
                .orElseThrow(() -> new IllegalArgumentException("Prestataire introuvable : " + id));

    	model.addAttribute("prestataire", prestataire);

        return "prestataire/enrolement/form"; // même template que la création
    }
    
    // SUPPRESSION
    /*@GetMapping("/delete/{id}")
    public String deleteBailleur(@PathVariable("id") int id, RedirectAttributes redirectAttrs) {
        service.delete(id);
        
        redirectAttrs.addFlashAttribute("successMessage", "Suppression effectuée avec succès !");
        
        return "redirect:" + Routes.ROUTE_BAILLEUR;
    }*/
        
    @GetMapping(value = "/api/prestataires", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> searchPrestataire(
            @RequestParam String term,
            @AuthenticationPrincipal UserPrincipal principal) {

        Page<PrestataireDTO> page =
                service.search(term, PageRequest.of(0, 50));

        List<Map<String, Object>> results = page.getContent().stream()
                .map(dto -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", dto.getId());
                    m.put("text", dto.getNom());
                    m.put("cellulaire", dto.getTelephone());
                    return m;
                })
                .collect(Collectors.toList());

        return Map.of("results", results);
    }
   
    @GetMapping(value = "/api/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> search(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 8) Pageable pageable,
            @AuthenticationPrincipal UserPrincipal principal) {

        Page<PrestataireDTO> page =
                service.search(keyword, pageable);
        
        System.out.println("TOTAL PAGES = " + page.getTotalPages()); // 👈 AJOUTE ÇA

        return Map.of(
                "content", page.getContent(),
                "number", page.getNumber(),
                "size", page.getSize(),
                "totalElements", page.getTotalElements(),
                "totalPages", page.getTotalPages(),
                "last", page.isLast(),
                "first", page.isFirst()
        );
    }
    
}