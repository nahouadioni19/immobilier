package com.app.controller.recouvre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
import com.app.dto.ImmeubForm;
import com.app.dto.ImmeubleDTO;
import com.app.entities.recouvre.Appartement;
import com.app.entities.recouvre.Immeuble;
import com.app.service.administration.UtilisateurService;
import com.app.service.common.PaginationService;
import com.app.service.recouvre.AppartementService;
import com.app.service.recouvre.BailleurService;
import com.app.service.recouvre.ImmeubleService;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping(Routes.ROUTE_IMMEUB)
public class ImmeubleController{

	private final ImmeubleService service;
	private final BailleurService bailleurService;
	private final UtilisateurService utilisateurService;
	private final AppartementService appartementService;
	private final PaginationService paginationService;
    private final SetupPage setup;

    @Autowired
	private MessageSource messageSource;
    
    public ImmeubleController(ImmeubleService service, SetupPage setup, BailleurService bailleurService, 
    			AppartementService appartementService, PaginationService paginationService, UtilisateurService utilisateurService) {
    	
        this.service = service;
        this.setup = setup;
        this.bailleurService = bailleurService;
        this.utilisateurService = utilisateurService;
        this.appartementService = appartementService;
        this.paginationService = paginationService;
    }

    @GetMapping
    public String listImmeubles(            
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            Model model) {

        Pageable pageable = PageRequest.of(page, 8, Sort.by("id").descending());

        Page<Immeuble> immeublesPage = service.searchPatrimoine(keyword, pageable);

        model.addAttribute("immeublesPage", immeublesPage);
        model.addAttribute("keyword", keyword);

        return "bailleur/patrimoine/list";
    }
    
    
 // FORMULAIRE CREATION
    @GetMapping("/create")
    public String showCreateForm(Model model, String code) {
        ImmeubForm form = new ImmeubForm();
        form.setImmeuble(new Immeuble()); // 🔹 Toujours initialiser pour éviter NPE
        form.getAppartements().add(new Appartement());

        code = "RECOUV";
        
        model.addAttribute("immeubForm", form);
        model.addAttribute("utilisateurs", utilisateurService.findByAgentRecouvrement(code));

        return "bailleur/patrimoine/form";
    }
    
    // FORMULAIRE EDIT
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes, String code) {
        Immeuble immeuble = service.findByIdWithAppartements(id).orElse(null);
        
        code = "RECOUV";
        
        if (immeuble == null) {
            redirectAttributes.addFlashAttribute("error", "Immeuble introuvable");
            return "redirect:" + Routes.ROUTE_IMMEUB;
        }

        // ⚡ Préparer le formulaire
        ImmeubForm form = new ImmeubForm();
        form.setImmeuble(immeuble);

        // Copier les appartements existants
        form.setAppartements(immeuble.getAppartements() != null
                ? new ArrayList<>(immeuble.getAppartements())
                : new ArrayList<>());

        model.addAttribute("immeubForm", form);
        model.addAttribute("utilisateurs", utilisateurService.findByAgentRecouvrement(code));

        // 🔹 Préparer le bailleur pour Select2
        if (immeuble.getBailleur() != null) {
            model.addAttribute("currentBailleur", immeuble.getBailleur());
        }

        return "bailleur/patrimoine/form";
    }
    
    
    @PostMapping("/save")
    public String saveImmeuble(@ModelAttribute ImmeubForm form,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {

        try {

            // 🔒 Vérification basique
            if (form == null || form.getImmeuble() == null) {
                redirectAttributes.addFlashAttribute("error", "Formulaire invalide");
                return "redirect:" + Routes.ROUTE_IMMEUB + "/create";
            }

            boolean isNew = (form.getImmeuble().getId() == null);

            // 🔥 Toute la logique est dans le service
            service.saveImmeubleWithAppartement(form);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    isNew ? "Enregistrement réussi" : "Modification réussie"
            );

        } catch (SecurityException e) {
            // 🔒 sécurité SaaS
            redirectAttributes.addFlashAttribute("error", "Accès refusé");

        } catch (IllegalArgumentException e) {
            // ⚠️ erreurs métier (ex: bailleur manquant)
            redirectAttributes.addFlashAttribute("error", e.getMessage());

        } catch (Exception e) {
            // 💥 erreur inattendue
            redirectAttributes.addFlashAttribute("error", "Erreur inattendue");
            e.printStackTrace();
        }

        return "redirect:" + Routes.ROUTE_IMMEUB;
    }
    
    /*@PostMapping("/save")
    @Transactional
    public String saveImmeuble(@ModelAttribute ImmeubForm immeubForm,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {

        Immeuble formImmeuble = immeubForm.getImmeuble();
        if (formImmeuble == null) {
            redirectAttributes.addFlashAttribute("error", "Erreur formulaire : immeuble manquant");
            return "redirect:" + Routes.ROUTE_IMMEUB + "/create";
        }

        boolean isNew = (formImmeuble.getId() == null);

        // 🔹 Charger l'entité existante si update
        Immeuble immeuble = isNew
                ? formImmeuble
                : service.findById(formImmeuble.getId())
                         .orElseThrow(() -> new RuntimeException("Immeuble introuvable"));

        // =========================
        // 🔹 Champs simples
        // =========================
        immeuble.setNomImmeuble(formImmeuble.getNomImmeuble());
        immeuble.setAdresse(formImmeuble.getAdresse());
        immeuble.setAnneeConstruction(formImmeuble.getAnneeConstruction());
        immeuble.setNombreEtages(formImmeuble.getNombreEtages());
        immeuble.setCodeImmeuble(formImmeuble.getCodeImmeuble());
        immeuble.setNumeroTitreFoncier(formImmeuble.getNumeroTitreFoncier());
        // TODO: ajouter les autres champs simples

        // =========================
        // 🔹 Bailleur et Utilisateur (ManyToOne)
        // =========================
        if (formImmeuble.getBailleur() != null && formImmeuble.getBailleur().getId() != null) {
            immeuble.setBailleur(
                bailleurService.findById(formImmeuble.getBailleur().getId())
                        .orElseThrow(() -> new RuntimeException("Bailleur introuvable"))
            );
        }

        if (formImmeuble.getUtilisateur() != null && formImmeuble.getUtilisateur().getId() != null) {
            immeuble.setUtilisateur(
                utilisateurService.findById(formImmeuble.getUtilisateur().getId())
                        .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"))
            );
        }

        // =========================
        // 🔹 Gestion des appartements (robuste)
        // =========================
        List<Appartement> submittedApps = immeubForm.getAppartements() != null
                ? immeubForm.getAppartements()
                : new ArrayList<>();

        // 1️⃣ Supprimer les appartements retirés
        if (!isNew) {
            List<Appartement> toRemove = new ArrayList<>();
            for (Appartement existingApp : immeuble.getAppartements()) {
                boolean stillExists = submittedApps.stream()
                        .anyMatch(a -> a.getId() != null && a.getId().equals(existingApp.getId()));
                if (!stillExists) {
                    toRemove.add(existingApp);
                }
            }
            toRemove.forEach(immeuble.getAppartements()::remove);
        }

        // 2️⃣ Ajouter ou mettre à jour les appartements soumis
        for (Appartement app : submittedApps) {
            app.setImmeuble(immeuble);

            if (app.getId() != null) {
                // update existant
                immeuble.getAppartements().stream()
                    .filter(existing -> existing.getId().equals(app.getId()))
                    .findFirst()
                    .ifPresent(existing -> {
                        existing.setNumAppart(app.getNumAppart());
                        existing.setLoyerMensuel(app.getLoyerMensuel());
                        existing.setLibelle(app.getLibelle());
                    });

            } else {
                // nouveau
                immeuble.getAppartements().add(app);
            }
        }
        
        
        service.saveImmeubleWithAppartement(immeuble);

        redirectAttributes.addFlashAttribute("successMessage",
                isNew ? "Enregistrement réussi" : "Modification réussie");

        return "redirect:" + Routes.ROUTE_IMMEUB;
    }    */
    
    
    
    
   /* @PostMapping("/save")
    public String saveImmeuble(@ModelAttribute ImmeubForm immeubForm,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {

        Immeuble immeuble = immeubForm.getImmeuble();
        if (immeuble == null) {
            redirectAttributes.addFlashAttribute("error", "Erreur formulaire : immeuble manquant");
            return "redirect:" + Routes.ROUTE_IMMEUB + "/create";
        }

        boolean isNew = (immeuble.getId() == null);

        // 🔹 Charger Bailleur complet si ID sélectionné
        if (immeuble.getBailleur() != null && immeuble.getBailleur().getId() != null) {
            immeuble.setBailleur(
                    bailleurService.findById(immeuble.getBailleur().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Bailleur introuvable"))
            );
        }

        // 🔹 Charger Utilisateur complet si ID sélectionné
        if (immeuble.getUtilisateur() != null && immeuble.getUtilisateur().getId() != null) {
            immeuble.setUtilisateur(
                    utilisateurService.findById(immeuble.getUtilisateur().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"))
            );
        }

        // 🔹 Lier les appartements
        List<Appartement> appartements = immeubForm.getAppartements();
        if (appartements != null) {
            appartements.forEach(app -> app.setImmeuble(immeuble));
            immeuble.setAppartements(appartements);
        }

        // 🔹 Sauvegarde
        service.save(immeuble);

        String successMessage = messageSource.getMessage(
                isNew ? "success.enregistrement" : "success.modification",
                null,
                LocaleContextHolder.getLocale()
        );
        redirectAttributes.addFlashAttribute("successMessage", successMessage);

        return "redirect:" + Routes.ROUTE_IMMEUB;
    }*/
   
    
    // SUPPRESSION
    @GetMapping("/delete/{id}")
    public String deleteImmeub(@PathVariable("id") int id, RedirectAttributes redirectAttrs) {
        service.delete(id);
        
        redirectAttrs.addFlashAttribute("successMessage", "Suppression effectuée avec succès !");
        
        return "redirect:" + Routes.ROUTE_IMMEUB;
    }
    
       
    @GetMapping(value = "/api/appartements", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> searchAppartement(
            @RequestParam String term,
            @RequestParam(required = false) Integer currentId) {

    	if (term == null || term.trim().isEmpty()) {
            return Map.of("results", List.of());
        }

        Page<Appartement> page =
                appartementService.searchForAppartement(
                		term, 
                		currentId, 
                		PageRequest.of(0, 50)
                );

        List<Map<String, Object>> results = page.getContent().stream()
                .map(a -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", a.getId());
                    m.put("text",
                            a.getLibelle() +" - " +
                            a.getImmeuble().getNomImmeuble() +" - "+ 
                            a.getImmeuble().getBailleur().getNom()+" - "+
                            a.getImmeuble().getBailleur().getPrenom()+
                            " (" + a.getLoyerMensuel() + " F CFA)");
                    m.put("libelle", a.getLibelle());   // ✅ AJOUTÉ
                    m.put("loyerMensuel", a.getLoyerMensuel());
                    return m;
                })
                .toList();

        return Map.of("results", results);
    }
    
    @GetMapping(value = "/api/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<ImmeubleDTO> search(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 8) Pageable pageable) {

        return service.searchDTO(keyword, pageable);
    }
    
    
}
