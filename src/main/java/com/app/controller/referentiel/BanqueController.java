package com.app.controller.referentiel;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.entities.administration.Banque;
import com.app.service.BanqueService;
import com.app.service.common.PaginationService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("banques")
public class BanqueController {

	private final BanqueService banqueService;
	private final PaginationService paginationService;

	@Autowired
	private MessageSource messageSource;
	
	public BanqueController(BanqueService banqueService, PaginationService paginationService) {
		this.banqueService = banqueService;
		this.paginationService = paginationService;
	}

	@GetMapping
	public String listBanques(Model model, @RequestParam(defaultValue = "0") int page, HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName(); // utilisateur connecté (si besoin affichage)

		// Pagination via PaginationService
		Page<Banque> banquesPage = paginationService.getPage(banqueService::findAll, page, 8);

		model.addAttribute("currentUri", request.getRequestURI());
		model.addAttribute("banquesPage", banquesPage);
		model.addAttribute("banques", banquesPage.getContent()); // la liste pour Thymeleaf
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", banquesPage.getTotalPages());

		return "banque/list";
	}

	@GetMapping("/create")
	public String showCreateForm(Model model) {

		Banque banque = new Banque();

		model.addAttribute("banque", banque);

		return "banque/form";
	}

	@PostMapping("/save")
	public String saveBanque(@ModelAttribute Banque banque, RedirectAttributes redirectAttrs) {

		boolean isNew = (banque.getBancode() == null);

		banqueService.save(banque);

		// Récupération du message depuis messages.properties
	    String successMessage = messageSource.getMessage(
	            isNew ? "success.enregistrement" : "success.modification",
	            null,
	            LocaleContextHolder.getLocale()
	    );

		// ⚡ Flash attribute pour survivre au redirect
		redirectAttrs.addFlashAttribute("successMessage", successMessage);

		return "redirect:/banques"; // redirect vers la liste
	}

	@GetMapping("/edit/{bancode}")
	public String showEditForm(@PathVariable Long bancode, Model model) {
		// log.info("✅ showEditForm appelé avec reqId={}", bancode);

		Banque banque = banqueService.findById(bancode);
		// Banque banque = new Banque();

		model.addAttribute("banque", banque);

		return "banque/form";
	}

	@GetMapping("/delete/{bancode}")
	public String deleteBanque(@PathVariable Long bancode, RedirectAttributes redirectAttrs) {
		banqueService.deleteById(bancode);

		redirectAttrs.addFlashAttribute("successMessage", "Suppression effectuée avec succès !");

		return "redirect:/banques";
	}

}
