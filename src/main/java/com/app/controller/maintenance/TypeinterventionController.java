package com.app.controller.maintenance;

import com.app.controller.common.Routes;
import com.app.entities.maintenance.Typeintervention;
import com.app.security.UserPrincipal;
import com.app.service.maintenance.TypeinterventionService;
import com.app.dto.TypeinterventionDTO;

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
@RequestMapping(Routes.ROUTE_TYPEINTERVENTION)
public class TypeinterventionController {

    private final TypeinterventionService service;
    
    @Value("${app.storage.directory}")
    private String storageDirectory;  // <-- injecté depuis application.properties

    @Autowired
	private MessageSource messageSource;
    
    public TypeinterventionController(TypeinterventionService service) {
        this.service = service;
    }

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @AuthenticationPrincipal UserPrincipal principal,
            Model model) {

        //Integer agenceId = principal.getUtilisateur().getAgence().getId();

        Page<TypeinterventionDTO> typeinterventionsPage =
                service.search(keyword, PageRequest.of(page, 8));

        model.addAttribute("typeinterventionsPage", typeinterventionsPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);

        return "maintenance/typeintervention/list";
    }
   
    // FORMULAIRE DE CREATION
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("typeintervention", new Typeintervention());
        return "maintenance/typeintervention/form";
    }
      
    @PostMapping("/save")
    public String saveTypeintervention(
            @ModelAttribute("typeintervention") Typeintervention typeintervention,
            RedirectAttributes redirectAttributes) {

        boolean isNew = (typeintervention.getId() == null);

        try {
        	Typeintervention saved = service.saveIntervention(typeintervention);
        	System.out.println("Type intervention enregistré avec ID = " + saved.getId());


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

        return "redirect:" + Routes.ROUTE_TYPEINTERVENTION;
    }
    

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {

        Typeintervention typeintervention = service.findById(id);

        model.addAttribute("typeintervention", typeintervention);

        return "maintenance/typeintervention/form";
    }
    
    // SUPPRESSION
    /*@GetMapping("/delete/{id}")
    public String deleteBailleur(@PathVariable("id") int id, RedirectAttributes redirectAttrs) {
        service.delete(id);
        
        redirectAttrs.addFlashAttribute("successMessage", "Suppression effectuée avec succès !");
        
        return "redirect:" + Routes.ROUTE_BAILLEUR;
    }*/
        
    @GetMapping(value = "/api/type-interventions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> searchtypeintervention(
            @RequestParam String term,
            @AuthenticationPrincipal UserPrincipal principal) {

        Page<TypeinterventionDTO> page =
                service.search(term, PageRequest.of(0, 50));

        List<Map<String, Object>> results = page.getContent().stream()
                .map(dto -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", dto.getId());
                    m.put("text", dto.getLibelle());
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

        Page<TypeinterventionDTO> page =
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