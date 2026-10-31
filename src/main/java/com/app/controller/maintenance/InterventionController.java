package com.app.controller.maintenance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.dto.InterventionDTO;
import com.app.entities.maintenance.Intervention;
import com.app.form.InterventionForm;
import com.app.mapper.InterventionMapper;
import com.app.repositories.BailSelectProjection;
import com.app.security.UserPrincipal;

import com.app.service.administration.UtilisateurService;
import com.app.service.maintenance.InterventionService;
import com.app.service.maintenance.PrestataireService;
import com.app.service.maintenance.TypeinterventionService;
import com.app.service.recouvre.AppartementService;
import com.app.service.recouvre.BailService;
import com.app.service.recouvre.LocataireService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import com.app.controller.common.Routes;

@Controller
@RequestMapping(Routes.ROUTE_INTERVENTION)
@RequiredArgsConstructor
public class InterventionController {
	
	private final InterventionService service;
	private final BailService bailService;
	private final UtilisateurService utilisateurService;
	private final PrestataireService prestataireService;
	private final TypeinterventionService typeinterventionService;	
	
	private final InterventionMapper mapper;
	
	
	@Autowired
	private MessageSource messageSource;	
	
	@GetMapping
	public String list(
	        @AuthenticationPrincipal UserPrincipal principal,
	        Model model,
	        @RequestParam(value = "page", defaultValue = "0") int page,
	        @RequestParam(value = "keyword", required = false) String keyword) {

	    int pageSize = 8;

	    // KEYWORD
	    String search = (keyword == null || keyword.isBlank())
	            ? null
	            : "%" + keyword.trim().toLowerCase() + "%";
	    
	    Page<InterventionDTO> interventionsPage = service.search(
	            search,
	            PageRequest.of(page, pageSize)
	    );

	    model.addAttribute("interventionsPage", interventionsPage);
	    model.addAttribute("keyword", keyword);

	    return "maintenance/intervention/list";
	}	
	
	
	/*@GetMapping("/create")
	public String showCreateForm(@AuthenticationPrincipal UserPrincipal principal,
	                             Model model,
	                             @RequestParam(required = false) String keyword,
	                             @RequestParam(value = "id", required = false) Integer id) {

	    Integer agenceId = principal.getUtilisateur().getAgence().getId();

	    InterventionForm form = new InterventionForm();
	    model.addAttribute("intervention", form);

	    model.addAttribute("currentUserId",
	            principal != null && principal.getUtilisateur() != null
	                    ? principal.getUtilisateur().getId()
	                    : null);

	    // ✅ Listes déroulantes du formulaire
	    //model.addAttribute("appartements", appartementService.findByAgenceId(agenceId));
	    //model.addAttribute("locataires", locataireService.findByAgenceId(agenceId));
	    model.addAttribute("listePrestataires", prestataireService.findAll());
	    model.addAttribute("listeTypes", typeinterventionService.findAll());

	    return "maintenance/intervention/form";
	}*/
	
	
	
	@GetMapping("/create") // ou une méthode dédiée /edit/{id} selon votre structure
	public String showCreateForm(@AuthenticationPrincipal UserPrincipal principal,
	                             Model model,
	                             @RequestParam(required = false) String keyword,
	                             @RequestParam(value = "id", required = false) Integer id) {

	    Integer agenceId = principal.getUtilisateur().getAgence().getId();

	    InterventionForm form;
	    if (id != null) {
	        Intervention entity = service.findByIdAgence(id)
	                .orElseThrow(() -> new EntityNotFoundException("Intervention introuvable, id=" + id));
	        form = mapper.toForm(entity);

	        // ✅ Préremplissage du libellé pour Select2
	        if (entity.getLocataire() != null) {
	            form.setLocataireNom(entity.getLocataire().getNom());
	        }
	        
	        if (entity.getBailleur() != null) {
	            form.setBailleurNom(entity.getBailleur().getNom());
	        }
	        
	        if (entity.getAppartement() != null) {
	            form.setAppartementLibelle(entity.getAppartement().getLibelle());
	            form.setAppartementNumAppart(entity.getAppartement().getNumAppart());
	        }
	    } else {
	        form = new InterventionForm();
	    }

	    model.addAttribute("intervention", form);
	    model.addAttribute("currentUserId",
	            principal != null && principal.getUtilisateur() != null
	                    ? principal.getUtilisateur().getId()
	                    : null);

	    model.addAttribute("listePrestataires", prestataireService.findAll());
	    model.addAttribute("listeTypes", typeinterventionService.findAll());

	    return "maintenance/intervention/form";
	}

	
		
	/*
	 * @GetMapping("/edit/{id}") public String showEditForm(@AuthenticationPrincipal
	 * UserPrincipal principal,
	 * 
	 * @PathVariable Integer id, Model model) {
	 * 
	 * Intervention intervention = service.findByIdRelations(id) .orElseThrow(() ->
	 * new IllegalArgumentException("Intervention introuvable"));
	 * 
	 * InterventionForm form = new InterventionForm();
	 * 
	 * model.addAttribute("encaisse", form);
	 * 
	 * 
	 * return "recouvrement/form"; }
	 */
	
		
	//
	private void chargerBaux(UserPrincipal principal, Long agentId, Model model, String keyword, Integer id) {

	    List<BailSelectProjection> bails = bailService.findBailDetailsNative(principal,agentId,keyword,id);

	    List<Map<String, Object>> bailData = bails.stream().map(b -> {
	        Map<String, Object> map = new HashMap<>();
	        map.put("id", b.getId());
	        map.put("locprenom", b.getLocprenom());
	        map.put("locnom", b.getLocnom());
	        map.put("loyer", b.getMontantloyer());
	        map.put("loctel", b.getLoctel());
	        map.put("bailibelle", b.getBailibelle());
	        map.put("usglibelle", b.getUsglibelle());
	        map.put("numero", b.getNumero());
	        map.put("lastpayment",
	                b.getDerniereDatePaiement() != null
	                        ? b.getDerniereDatePaiement()
	                        : b.getDateDebut());
	        return map;
	    }).toList();

	    model.addAttribute("bails", bailData);
	   
	}

	/*@GetMapping("/delete/{id}")
	public String deleteEncaisse(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
		service.deleteById(id);
		
		redirectAttrs.addFlashAttribute("successMessage", "Suppression effectuée avec succès !");
		
		//return "redirect:/recouvrements";
		return "redirect:/recouvrements/a-encaisser";
	}*/
	
	
	/*
	 * @PostMapping("/save") public String saveEncaisse(
	 * 
	 * @ModelAttribute EncaisseForm form,
	 * 
	 * @RequestParam(value = "chequeFile", required = false) MultipartFile
	 * chequeFile,
	 * 
	 * @RequestParam(value = "removeCheque", required = false) String removeCheque,
	 * 
	 * @AuthenticationPrincipal UserPrincipal principal, RedirectAttributes
	 * redirectAttributes) {
	 * 
	 * try {
	 * 
	 * // ========================= // 🔐 rôles // ========================= boolean
	 * isDirec = principal.getAuthorities().stream() .anyMatch(auth ->
	 * "ROLE_DIREC".equals(auth.getAuthority()));
	 * 
	 * boolean isRecouv = principal.getAuthorities().stream() .anyMatch(auth ->
	 * "ROLE_RECOUV".equals(auth.getAuthority()));
	 * 
	 * // ========================= // 🔐 utilisateur (agent connecté) //
	 * =========================
	 * form.setUtilisateurId(principal.getUtilisateur().getId());
	 * 
	 * if(isRecouv) { form.setFiltreAgentId(principal.getUtilisateur().getId()); }
	 * // ========================= // 🔐 statut automatique //
	 * ========================= form.setStatut(isDirec ? 1 : 1);
	 * 
	 * // ========================= // 📁 GESTION FICHIER CHEQUE //
	 * =========================
	 * 
	 * if ("true".equals(removeCheque)) { form.setChequePath(null); }
	 * 
	 * if (chequeFile != null && !chequeFile.isEmpty()) {
	 * 
	 * // 🔒 validation + sauvegarde String fileName =
	 * fileStorageService.saveFile(chequeFile);
	 * 
	 * // 🔥 on injecte dans le form form.setChequePath(fileName);
	 * 
	 * } else { // 🔁 CAS UPDATE : garder ancien fichier if (form.getId() != null &&
	 * (chequeFile == null || chequeFile.isEmpty())) {
	 * service.findById(form.getId()) .ifPresent(existing ->
	 * form.setChequePath(existing.getChequePath())); } }
	 * 
	 * // ========================= // 💾 sauvegarde // =========================
	 * service.saveEncaissement(form);
	 * 
	 * String message = messageSource.getMessage( form.getId() == null ?
	 * "success.enregistrement" : "success.modification", null,
	 * LocaleContextHolder.getLocale() );
	 * 
	 * redirectAttributes.addFlashAttribute("successMessage", message);
	 * 
	 * } catch (IllegalArgumentException e) {
	 * redirectAttributes.addFlashAttribute("error", e.getMessage());
	 * 
	 * } catch (SecurityException e) { redirectAttributes.addFlashAttribute("error",
	 * "Accès refusé");
	 * 
	 * } catch (Exception e) { redirectAttributes.addFlashAttribute("error",
	 * "Erreur inattendue"); e.printStackTrace(); }
	 * 
	 * System.out.println("FILE = " + chequeFile); System.out.println("IS EMPTY = "
	 * + (chequeFile == null ? "null" : chequeFile.isEmpty()));
	 * System.out.println("CHEQUE PATH AVANT SAVE = " + form.getChequePath());
	 * 
	 * // return "redirect:/recouvrements"; return
	 * "redirect:/recouvrements/a-encaisser"; }
	 */
	
	
	private void chargerListes(@AuthenticationPrincipal UserPrincipal principal, 
								@RequestParam(required = false) String keyword, Model model) {
	  
	    // Charger les listes pour le formulaire
	    model.addAttribute("bails", bailService.findBailDetailsNative(principal,null,keyword,null));

	    
	    model.addAttribute("utilisateurs", utilisateurService.findByAgentRecouvrement());
	}


	/*
	 * @PostMapping("/valider/{id}") public String validerPaiement(@PathVariable
	 * Integer id,
	 * 
	 * @AuthenticationPrincipal UserPrincipal principal, RedirectAttributes
	 * redirectAttributes) {
	 * 
	 * try {
	 * 
	 * service.validerPaiement(id, principal.getUtilisateur());
	 * 
	 * redirectAttributes.addFlashAttribute("successMessage",
	 * "Paiement validé avec succès");
	 * 
	 * } catch (SecurityException e) { redirectAttributes.addFlashAttribute("error",
	 * "Accès refusé");
	 * 
	 * } catch (IllegalArgumentException e) {
	 * redirectAttributes.addFlashAttribute("error", e.getMessage());
	 * 
	 * } catch (Exception e) { redirectAttributes.addFlashAttribute("error",
	 * "Erreur inattendue"); e.printStackTrace(); }
	 * 
	 * //return "redirect:/recouvrements"; return
	 * "redirect:/recouvrements/a-encaisser"; }
	 */

}
