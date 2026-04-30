package com.app.controller.recouvre;

import com.app.controller.common.Routes;
import com.app.entities.recouvre.Bailleur;
import com.app.security.UserPrincipal;
import com.app.service.common.PaginationService;
import com.app.service.recouvre.BailleurService;

import com.app.controller.common.SetupPage;
import com.app.dto.BailleurDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Controller
@RequestMapping(Routes.ROUTE_BAILLEUR)
public class BailleurController {

    private final BailleurService service;
    
    @Value("${app.storage.directory}")
    private String storageDirectory;  // <-- injecté depuis application.properties

    @Autowired
	private MessageSource messageSource;
    
    public BailleurController(BailleurService service) {
        this.service = service;
    }

    @GetMapping
    public String listBailleurs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @AuthenticationPrincipal UserPrincipal principal,
            Model model) {

        Integer agenceId = principal.getUtilisateur().getAgence().getId();

        Page<BailleurDTO> bailleursPage =
                service.search(keyword, PageRequest.of(page, 8));

        model.addAttribute("bailleursPage", bailleursPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);

        return "bailleur/enrolement/list";
    }
    
    /*@GetMapping
    public String listBailleurs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            Model model) {

        Page<Bailleur> bailleursPage = service.search(
                keyword,
                PageRequest.of(page, 8)
        );

        model.addAttribute("bailleursPage", bailleursPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);

        return "bailleur/enrolement/list";
    }*/
    
    // FORMULAIRE DE CREATION
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("bailleur", new Bailleur());
        return "bailleur/enrolement/form";
    }
      
    @PostMapping("/save")
    public String saveBailleur(
            @ModelAttribute("bailleur") Bailleur bailleur,
            @RequestParam(value = "carteIdentite", required = false) MultipartFile carteIdentite,
            @RequestParam(value = "factureCie", required = false) MultipartFile factureCie,
            RedirectAttributes redirectAttributes) {

        boolean isNew = (bailleur.getId() == null);

        try {
           // service.saveWithDocument(bailleur, carteIdentite, factureCie);
        	Bailleur saved = service.saveWithDocument(bailleur, carteIdentite, factureCie);
        	System.out.println("Bailleur enregistré avec ID = " + saved.getId());


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

        return "redirect:" + Routes.ROUTE_BAILLEUR;
    }
    

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {
    	
    	Bailleur bailleur = service.findByIdAgence(id)
                .orElseThrow(() -> new IllegalArgumentException("Bailleur introuvable : " + id));

    	model.addAttribute("bailleur", bailleur);

        if (bailleur.getCarteIdentitePath() != null && !bailleur.getCarteIdentitePath().isEmpty()) {
            String fileName = Paths.get(bailleur.getCarteIdentitePath()).getFileName().toString();
            String url = "/dossiers/bailleurs/" + id + "/" + fileName;
            model.addAttribute("carteIdentiteUrl", url);
            System.out.println("✅ carteIdentiteUrl = " + url);
        } else {
            System.out.println("⚠️ Aucun document enregistré pour ce bailleur !");
            model.addAttribute("carteIdentiteUrl", null);
        }
        
        if (bailleur.getFactureCiePath() != null && !bailleur.getFactureCiePath().isEmpty()) {
            String fileName = Paths.get(bailleur.getFactureCiePath()).getFileName().toString();
            String url = "/dossiers/bailleurs/" + id + "/" + fileName;
            model.addAttribute("factureCieUrl", url);
            System.out.println("✅ factureCieUrl = " + url);
        } else {
            System.out.println("⚠️ Aucun document enregistré pour ce bailleur !");
            model.addAttribute("factureCieUrl", null);
        }

        return "bailleur/enrolement/form"; // même template que la création
    }
    
    // SUPPRESSION
    @GetMapping("/delete/{id}")
    public String deleteBailleur(@PathVariable("id") int id, RedirectAttributes redirectAttrs) {
        service.delete(id);
        
        redirectAttrs.addFlashAttribute("successMessage", "Suppression effectuée avec succès !");
        
        return "redirect:" + Routes.ROUTE_BAILLEUR;
    }
    
    /*@GetMapping(value = "/api/bailleurs", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> searchBailleur(@RequestParam String term) {

        Page<Bailleur> page = service.search(term, PageRequest.of(0, 50));

        List<Map<String, Object>> results = page.getContent().stream()
                .map(l -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", l.getId());
                    m.put("text", l.getNom() + " " + l.getPrenom());
                    m.put("cellulaire", l.getCellulaire());   // ✅ AJOUTÉ                    
                    return m;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("results", results);

        return response;
    }*/
    
    @GetMapping(value = "/api/bailleurs", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> searchBailleur(
            @RequestParam String term,
            @AuthenticationPrincipal UserPrincipal principal) {

       // Integer agenceId = principal.getUtilisateur().getAgence().getId();

        Page<BailleurDTO> page =
                service.search(term, PageRequest.of(0, 50));

        List<Map<String, Object>> results = page.getContent().stream()
                .map(dto -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", dto.getId());
                    m.put("text", dto.getNom() + " " + dto.getPrenom());
                    m.put("cellulaire", dto.getCellulaire());
                    return m;
                })
                .collect(Collectors.toList());

        return Map.of("results", results);
    }

    /*@GetMapping(value = "/api/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> search(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 8) Pageable pageable) {

        Page<Bailleur> page = service.search(keyword, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", page.getContent());
        response.put("page", page.getNumber());
        response.put("size", page.getSize());
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("last", page.isLast());
        response.put("first", page.isFirst());

        return response;
    }*/
    
    @GetMapping(value = "/api/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> search(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 8) Pageable pageable,
            @AuthenticationPrincipal UserPrincipal principal) {

      //  Integer agenceId = principal.getUtilisateur().getAgence().getId();

        Page<BailleurDTO> page =
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