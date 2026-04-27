package com.app.controller.recouvre;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;

import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.controller.common.Routes;
import com.app.controller.common.SetupPage;
import com.app.entities.recouvre.Bailleur;
import com.app.entities.recouvre.Locataire;
import com.app.service.common.PaginationService;
import com.app.service.recouvre.LocataireService;
import com.app.service.referentiel.PaysService;
import com.app.service.referentiel.ProfessionService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(Routes.ROUTE_LOCATAIRE)
public class LocataireController {
	
	private final LocataireService service;
    private final SetupPage setup;
    private final PaginationService paginationService;
    private final PaysService paysService;
    private final ProfessionService prfService;
    
    @Value("${app.storage.directory}")
    private String storageDirectory;  // <-- injecté depuis application.properties
    
    @Autowired
	private MessageSource messageSource;
    
    public LocataireController(LocataireService service, SetupPage setup, PaginationService paginationService, 
    					PaysService paysService, ProfessionService prfService) {
        this.service = service;
        this.setup = setup;
        this.paginationService = paginationService;
        this.paysService = paysService;
        this.prfService = prfService;
    }

    // LISTE
  /*  @GetMapping
    public String listLocataires(Model model, @RequestParam(defaultValue = "0") int page, HttpServletRequest request) {
    	
        Page<Locataire> locatairesPage = paginationService.getPage(service::findAll, page, 8);

        model.addAttribute("locatairesPage", locatairesPage);
        model.addAttribute("locataires", locatairesPage.getContent()); // la liste pour Thymeleaf
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", locatairesPage.getTotalPages());
        model.addAttribute("currentUri", request.getRequestURI());
        
        return "locataire/enrolement/list"; // Thymeleaf template : bailleur/list.html
    }*/
    
   /* @GetMapping
    public String listLocataires(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 8) Pageable pageable,
            Model model) {

        Page<Locataire> page = service.searchLocataire(keyword, pageable);

        model.addAttribute("locatairesPage", page);
        model.addAttribute("keyword", keyword);

        return "locataire/enrolement/list";
    }*/

    
    @GetMapping
    public String listLocataires(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            Model model) {

        Page<Locataire> locatairesPage = service.searchLocataire(
                keyword,
                PageRequest.of(page, 8)
        );

        model.addAttribute("locatairesPage", locatairesPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);

        return "locataire/enrolement/list";
    }
    
    
    
    // FORMULAIRE DE CREATION
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("locataire", new Locataire());
        model.addAttribute("listePays", paysService.findAll());
        model.addAttribute("professions", prfService.findAll());
        return "locataire/enrolement/form"; // Thymeleaf template : bailleur/create.html
    }

    
    @PostMapping("/save")
    public String saveLocataire(
            @ModelAttribute("locataire") Locataire locataire,
            @RequestParam(value = "documentIdentite", required = false) MultipartFile documentIdentite,
            RedirectAttributes redirectAttributes) {

        boolean isNew = (locataire.getId() == null);

        try {
           // service.saveWithDocument(bailleur, carteIdentite, factureCie);
        	Locataire saved = service.saveWithDocument(locataire, documentIdentite);
        	System.out.println("Locataire enregistré avec ID = " + saved.getId());


            String successMessage = messageSource.getMessage(
                    isNew ? "success.enregistrement" : "success.modification",
                    null,
                    LocaleContextHolder.getLocale()
            );

            redirectAttributes.addFlashAttribute("successMessage", successMessage);

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de l’upload ou de la suppression du fichier !");
            e.printStackTrace();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erreur inattendue : " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:" + Routes.ROUTE_LOCATAIRE;
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {
        Locataire locataire = service.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Locataire introuvable : " + id));

        model.addAttribute("locataire", locataire);
        model.addAttribute("listePays", paysService.findAll());
        model.addAttribute("professions", prfService.findAll());

        if (locataire.getDocumentPath() != null && !locataire.getDocumentPath().isEmpty()) {
            String fileName = Paths.get(locataire.getDocumentPath()).getFileName().toString();
            String url = "/dossiers/locataires/" + id + "/" + fileName;
            model.addAttribute("documentIdentiteUrl", url);
            System.out.println("✅ documentIdentiteUrl = " + url);
        } else {
            System.out.println("⚠️ Aucun document enregistré pour ce locataire !");
            model.addAttribute("documentIdentiteUrl", null);
        }

        return "locataire/enrolement/form";
    }

    // SUPPRESSION
    @GetMapping("/delete/{id}")
    public String deleteLocataire(@PathVariable("id") int id, RedirectAttributes redirectAttrs) {
        service.delete(id);
        
        redirectAttrs.addFlashAttribute("successMessage", "Suppression effectuée avec succès !");
        
        return "redirect:" + Routes.ROUTE_LOCATAIRE;
    }
    
    /*@GetMapping("/api/locataires")
    @ResponseBody
    public List<Map<String, Object>> searchLocataire(@RequestParam String term) {
        return service.search(term).stream()
                .map(l -> Map.of(
                        "id", l.getId(),
                        "text", l.getPrenoms() + " " + l.getNom()
                ))
                .toList();
    }*/
    
   /* @GetMapping(value = "/api/locataires", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, Object>> searchLocataire(@RequestParam String term) {
        Page<Locataire> page = service.search(term, PageRequest.of(0, 50));

        return page.getContent().stream()
                .map(l -> {
                    Map<String, Object> m = new java.util.HashMap<>();
                    m.put("id", l.getId());
                    m.put("text", l.getPrenom() + " " + l.getNom());
                    return m;
                })
                .collect(Collectors.toList());
        
    }*/
    
    @GetMapping(value = "/api/locataires", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> searchLocataire(@RequestParam String term) {

        Page<Locataire> page = service.search(term, PageRequest.of(0, 50));

        List<Map<String, Object>> results = page.getContent().stream()
                .map(l -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", l.getId());
                    m.put("text", l.getPrenom() + " " + l.getNom());
                    m.put("telephone", l.getTelephone());   // ✅ AJOUTÉ
                    m.put("email", l.getEmail());           // ✅ AJOUTÉ
                    return m;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("results", results);

        return response;
    }

   /* @GetMapping("/api/search")
    @ResponseBody
    public Page<Locataire> search(@RequestParam String keyword, Pageable pageable) {
        return service.searchLocataire(keyword, pageable);
    }*/

    @GetMapping(value = "/api/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<Locataire> search(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 8) Pageable pageable) {

        return service.searchLocataire(keyword, pageable);
    }
}
