package com.app.service.recouvre;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.app.dto.EncaisseDTO;
import com.app.dto.EncaisseListDto;
import com.app.entities.administration.Agence;
import com.app.entities.administration.Utilisateur;
import com.app.entities.recouvre.Bail;
import com.app.entities.recouvre.Encaisse;
import com.app.entities.recouvre.Identification;
import com.app.exceptions.PenaliteSuperieureException;
import com.app.form.EncaisseForm;
import com.app.mapper.EncaisseMapper;
import com.app.repositories.administration.UtilisateurRepository;
import com.app.repositories.recouvre.BailRepository;
import com.app.repositories.recouvre.EncaisseRepository;
import com.app.repositories.recouvre.IdentificationRepository;
import com.app.security.UserPrincipal;
import com.app.service.base.BaseService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EncaisseService extends BaseService<Encaisse>{
	
	@Autowired
	private final EncaisseRepository repo;
	private final BailRepository bailRepository;
	private final IdentificationRepository identificationRepository;
	private final UtilisateurRepository utilisateurRepository;
	private final LoyannService loyannService;
	 private final EncaisseMapper mapper;
	 
	@Override
    public JpaRepository<Encaisse, Integer> getRepository() {
        return repo;
    }
	
	public List<Encaisse> findAll() {
        return repo.findAll();
    }
		
	public void deleteById(Integer id) {
		repo.deleteById(id);
    }
	
	
	public Page<EncaisseListDto> findWithFilters(
	        UserPrincipal principal,
	        Long agentId,
	        String keyword,
	        Pageable pageable) {

	    if (keyword != null && keyword.trim().isEmpty()) {
	        keyword = null;
	    }

	    return repo.findWithFilters(agentId, keyword, pageable);
	}

	
	private Long defaultLong(Long value) {
	    return value != null ? value : 0L;
	}
	
	
	@Transactional
	public Encaisse saveEncaissement(EncaisseForm form) {

	    Agence currentAgence = getCurrentAgence();
	    	    
	    Encaisse entity;

	    boolean isNew = (form.getId() == null);


		if (isNew) {
		    entity = new Encaisse();
		    entity.setAgence(currentAgence);
		    entity.setEncvalide(false);
		} else {
		    entity = repo.findById(form.getId())
		            .orElseThrow(() ->
		                    new IllegalArgumentException(
		                            "Encaissement introuvable."));
		
		    checkAgence(
		            entity.getAgence(),
		            currentAgence.getId(),
		            "Cet encaissement n'appartient pas à votre agence.");
		
		    if (entity.isEncvalide()) {
		        throw new IllegalStateException(
		                "Un encaissement validé ne peut pas être modifié. "
		              + "Utilisez une procédure d'annulation tracée.");
		    }
		}


		if (form.getBailId() == null) {
		    throw new IllegalArgumentException(
		            "Le bail est obligatoire pour un encaissement.");
		}
		
		Bail bail = bailRepository.findById(form.getBailId())
		        .orElseThrow(() ->
		                new IllegalArgumentException("Bail introuvable."));
		
		checkAgence(
		        bail.getAgence(),
		        currentAgence.getId(),
		        "Ce bail n'appartient pas à votre agence.");
		
		entity.setBail(bail);


	    // =========================
	    // 🔗 Identification sécurisé
	    // =========================
	    if (form.getIdentificationId() != null) {

	        Identification ident = identificationRepository.findById(form.getIdentificationId())
	                .orElseThrow(() -> new IllegalArgumentException("Identification introuvable"));

	        checkAgence(ident.getAgence(), currentAgence.getId(), "Identification d’une autre agence");

	        entity.setIdentification(ident);

	    } else {
	        entity.setIdentification(null);
	    }

	    // =========================
	    // 🔗 Utilisateur sécurisé
	    // =========================
	    if (form.getUtilisateurId() != null) {

	        Utilisateur user = utilisateurRepository.findById(form.getUtilisateurId())
	                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

	        if (user.getAgence() == null 
	                || !user.getAgence().getId().equals(currentAgence.getId())) {
	            throw new SecurityException("Utilisateur d’une autre agence");
	        }

	        entity.setUtilisateur(user);
	    }

	    // =========================
	    // 🔢 Mapping simple
	    // =========================
	    entity.setEncDate(form.getEncDate());

	    entity.setEncMontant(defaultLong(form.getEncMontant()));
	    entity.setEncPerdeb(defaultLong(form.getEncPerdeb()));
	    entity.setEncAndeb(defaultLong(form.getEncAndeb()));
	    entity.setEncPerfin(defaultLong(form.getEncPerfin()));
	    entity.setEncAnfin(defaultLong(form.getEncAnfin()));
	    entity.setEnctotal(defaultLong(form.getEnctotal()));
	    entity.setEncloyer(defaultLong(form.getEncloyer()));

	    //entity.setEncvalide(form.isEncvalide());

	    entity.setEncmois(defaultLong(form.getEncmois()));
	    entity.setEncannee(defaultLong(form.getEncannee()));
	    entity.setEncArriere(defaultLong(form.getEncArriere()));
	    entity.setEncPenalite(defaultLong(form.getEncPenalite()));
	    entity.setEncNet(defaultLong(form.getEncNet()));
	    entity.setEncRepport(defaultLong(form.getEncRepport()));
	    entity.setEncMontReppo(defaultLong(form.getEncMontReppo()));

	    entity.setEncStatutRetour(form.getEncStatutRetour());
	    entity.setEncMode(form.getEncMode());
	    entity.setEncDeb(form.getEncDeb());
	    entity.setEncFin(form.getEncFin());
	    entity.setEncNumChq(form.getEncNumChq());
	    
	    entity.setFiltreAgentId(form.getFiltreAgentId());
	    // =========================
	    // 📎 CHEQUE FILE (PROPRE + SAFE)
	    // =========================
	    String existingCheque = entity.getChequePath();

	    if (form.getChequePath() != null && !form.getChequePath().isBlank()) {
	        // nouveau fichier uploadé
	        entity.setChequePath(form.getChequePath());

	    } else {
	        // aucun changement → on garde l'ancien
	        entity.setChequePath(existingCheque);
	    }

	    // =========================
	    // 🧾 statut
	    // =========================
	    if (form.getStatut() != null) {
	        entity.setStatut(form.getStatut());
	    } else if (entity.getStatut() == null) {
	        entity.setStatut(1);
	    }

	    // =========================
	    // 🏢 agence auto SaaS
	    // =========================
	    entity.setAgence(currentAgence);

	    return repo.save(entity);
	}
	

	private void checkAgence(Agence agenceEntity, Integer currentAgenceId, String message) {

	    if (agenceEntity == null) {
	        throw new SecurityException(message);
	    }

	    if (!java.util.Objects.equals(agenceEntity.getId(), currentAgenceId)) {
	        throw new SecurityException(message);
	    }
	}
	
	
	
	/*@Transactional
	public void validerPaiement(Integer id, Utilisateur user) {
	    Encaisse encaisse = repo.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Encaisse introuvable"));

	    // =========================
	    // 🔐 sécurité agence SaaS
	    // =========================
	    if (user.getAgence() == null) {
	        throw new SecurityException("Utilisateur sans agence");
	    }
	    if (encaisse.getAgence() == null
	            || !encaisse.getAgence().getId().equals(user.getAgence().getId())) {
	        throw new SecurityException("Accès refusé");
	    }

	    // =========================
	    // ⛔ éviter une double validation
	    // =========================
	    if (encaisse.isEncvalide()) {
	        throw new IllegalStateException("Ce paiement a déjà été validé");
	    }

	    Bail bail = encaisse.getBail();
	    if (bail == null) {
	        throw new IllegalArgumentException("Bail introuvable");
	    }

	    long montantCautionHonoraire = 0L;
	    if (bail.isPremierRecouvre()) {
	        montantCautionHonoraire = bail.getCaution() + bail.getHonoraire();
	    }

	    long montantEncaisse = encaisse.getEncMontant() != null
	            ? (encaisse.getEncMontant() - montantCautionHonoraire)
	            : 0L;

	    long penalite = encaisse.getEncPenalite() != null ? encaisse.getEncPenalite() : 0L;
	    long montantPaye = montantEncaisse - penalite;

	    long loyerMensuel = bail.getMontantLoyer() != null ? bail.getMontantLoyer() : 0L;

	    LocalDate dateDebut = bail.getDateFin();
	    loyannService.repartirLoyer(bail, montantPaye, loyerMensuel, dateDebut);

	    encaisse.setEncvalide(true);
	    encaisse.setStatut(2);
	    bail.setPremierRecouvre(false);

	    repo.save(encaisse);
	}*/
	
	
	
	@Transactional
	public void validerPaiement(Integer id, Utilisateur utilisateur) {

	    if (id == null) {
	        throw new IllegalArgumentException(
	                "L'identifiant de l'encaissement est obligatoire.");
	    }

	    Agence agenceCourante = getCurrentAgence();

	    // Verrouille l'encaissement pendant toute la validation.
	    Encaisse encaisse = repo.trouverPourMiseAJour(id)
	            .orElseThrow(() ->
	                    new IllegalArgumentException("Encaissement introuvable."));

	    checkAgence(
	            encaisse.getAgence(),
	            agenceCourante.getId(),
	            "Cet encaissement n'appartient pas à votre agence.");

	    if (encaisse.isEncvalide()) {
	        throw new IllegalStateException(
	                "Cet encaissement a déjà été validé.");
	    }

	    if (encaisse.getBail() == null
	            || encaisse.getBail().getId() == null) {
	        throw new IllegalStateException(
	                "Aucun bail n'est associé à cet encaissement.");
	    }

	    /*
	     * Verrouille également le bail : deux paiements simultanés
	     * ne doivent pas affecter les mêmes échéances en parallèle.
	     */
	    Integer bailId = encaisse.getBail().getId();

	    Bail bail = bailRepository.trouverPourMiseAJour(bailId)
	            .orElseThrow(() ->
	                    new IllegalArgumentException("Bail introuvable."));

	    checkAgence(
	            bail.getAgence(),
	            agenceCourante.getId(),
	            "Ce bail n'appartient pas à votre agence.");

	    if (!bail.getAgence().getId().equals(
	            encaisse.getAgence().getId())) {
	        throw new SecurityException(
	                "Le bail et l'encaissement ne relèvent pas "
	              + "de la même agence.");
	    }

	    long montantEncaisse = encaisse.getEncMontant() == null
	            ? 0L
	            : encaisse.getEncMontant();

	    long penalite = encaisse.getEncPenalite() == null
	            ? 0L
	            : encaisse.getEncPenalite();

	    if (montantEncaisse <= 0) {
	        throw new IllegalArgumentException(
	                "Le montant encaissé doit être supérieur à zéro.");
	    }

	    if (penalite < 0) {
	        throw new IllegalArgumentException(
	                "La pénalité ne peut pas être négative.");
	    }

	    /*
	     * Règle métier : au premier recouvrement, caution et honoraires
	     * sont retranchés du montant destiné au loyer.
	     */
	    long caution = bail.getCaution() == null
	            ? 0L
	            : bail.getCaution();

	    long honoraire = bail.getHonoraire() == null
	            ? 0L
	            : bail.getHonoraire();

	    long deductionsPremierRecouvrement = 0L;

	    if (bail.isPremierRecouvre()) {
	        deductionsPremierRecouvrement = caution + honoraire;
	    }

	    if (montantEncaisse < deductionsPremierRecouvrement) {
	        throw new IllegalArgumentException(
	                "Le montant encaissé (" + montantEncaisse + " FCFA) est "
	              + "inférieur aux déductions du premier recouvrement ("
	              + deductionsPremierRecouvrement + " FCFA).");
	    }

	    long montantDestineAuLoyer =
	            montantEncaisse - deductionsPremierRecouvrement;

	    /*
	     * La pénalité n'est pas affectée aux échéances de loyer.
	     * Si elle dépasse le montant à ventiler, on bloque la validation
	     * et on en informe l'utilisateur.
	     */
	    if (penalite > montantDestineAuLoyer) {
	        throw new PenaliteSuperieureException(
	                "La pénalité (" + penalite + " FCFA) est supérieure au "
	              + "montant à ventiler (" + montantDestineAuLoyer + " FCFA). "
	              + "Veuillez corriger la pénalité ou le montant encaissé.");
	    }

	    long montantAPayer = montantDestineAuLoyer - penalite;

	    LocalDate datePaiement = encaisse.getEncDate() != null
	            ? encaisse.getEncDate()
	            : LocalDate.now();

	    /*
	     * La date de début de répartition est calculée à partir de
	     * Bail.dateDebut dans LoyannService.
	     * Bail.dateFin n'est jamais modifiée ici.
	     */
	    if (montantAPayer > 0) {
	        loyannService.affecterPaiement(
	                encaisse,
	                bail,
	                montantAPayer,
	                datePaiement);
	    }

	    /*
	     * Les indicateurs de validation ne sont mis à jour
	     * qu'après la réussite de toutes les affectations.
	     */
	    encaisse.setEncvalide(true);
	    encaisse.setStatut(2);

	    if (bail.isPremierRecouvre()) {
	        bail.setPremierRecouvre(false);
	    }

	    bail.setDerniereDatePaiement(datePaiement);

	    // Le paiement solde-t-il toutes les échéances affichées ?
	    /*
	    long arrieres = encaisse.getEncArriere() == null ? 0L : encaisse.getEncArriere();

	    if (montantAPayer >= arrieres
	            && encaisse.getEncFin() != null && !encaisse.getEncFin().isBlank()) {
	        YearMonth fin = YearMonth.parse(
	                encaisse.getEncFin(),
	                DateTimeFormatter.ofPattern("MM/yyyy"));
	        bail.setDateFinRecouvre(fin.atEndOfMonth());
	    }
	    */

	    bailRepository.save(bail);
	    repo.saveAndFlush(encaisse);
	}


	/*@Transactional
	public void validerPaiement(Integer id, Utilisateur utilisateur) {
	
	    if (id == null) {
	        throw new IllegalArgumentException(
	                "L'identifiant de l'encaissement est obligatoire.");
	    }
	
	    Agence agenceCourante = getCurrentAgence();
	
	    // Verrouille l'encaissement pendant toute la validation.
	    Encaisse encaisse = repo.trouverPourMiseAJour(id)
	            .orElseThrow(() ->
	                    new IllegalArgumentException(
	                            "Encaissement introuvable."));
	
	    checkAgence(
	            encaisse.getAgence(),
	            agenceCourante.getId(),
	            "Cet encaissement n'appartient pas à votre agence.");
	
	    if (encaisse.isEncvalide()) {
	        throw new IllegalStateException(
	                "Cet encaissement a déjà été validé.");
	    }
	
	    if (encaisse.getBail() == null
	            || encaisse.getBail().getId() == null) {
	        throw new IllegalStateException(
	                "Aucun bail n'est associé à cet encaissement.");
	    }
	
	    
	    Integer bailId = encaisse.getBail().getId();
	
	    Bail bail = bailRepository.trouverPourMiseAJour(bailId)
	            .orElseThrow(() ->
	                    new IllegalArgumentException("Bail introuvable."));
	
	    checkAgence(
	            bail.getAgence(),
	            agenceCourante.getId(),
	            "Ce bail n'appartient pas à votre agence.");
	
	    if (!bail.getAgence().getId().equals(
	            encaisse.getAgence().getId())) {
	        throw new SecurityException(
	                "Le bail et l'encaissement ne relèvent pas "
	              + "de la même agence.");
	    }
	
	    long montantEncaisse = encaisse.getEncMontant() == null
	            ? 0L
	            : encaisse.getEncMontant();
	
	    long penalite = encaisse.getEncPenalite() == null
	            ? 0L
	            : encaisse.getEncPenalite();
	
	    if (montantEncaisse <= 0) {
	        throw new IllegalArgumentException(
	                "Le montant encaissé doit être supérieur à zéro.");
	    }
	
	    if (penalite < 0) {
	        throw new IllegalArgumentException(
	                "La pénalité ne peut pas être négative.");
	    }
	
	    long caution = bail.getCaution() == null
	            ? 0L
	            : bail.getCaution();
	
	    long honoraire = bail.getHonoraire() == null
	            ? 0L
	            : bail.getHonoraire();
	
	    long deductionsPremierRecouvrement = 0L;
	
	    if (bail.isPremierRecouvre()) {
	        deductionsPremierRecouvrement = caution + honoraire;
	    }
	
	    if (montantEncaisse < deductionsPremierRecouvrement) {
	        throw new IllegalArgumentException(
	                "Le montant encaissé est inférieur aux déductions "
	              + "du premier recouvrement.");
	    }
	
	    long montantDestineAuLoyer =
	            montantEncaisse - deductionsPremierRecouvrement;
	
	    
	    if (penalite > montantDestineAuLoyer) {
	        throw new IllegalArgumentException(
	                "La pénalité dépasse le montant disponible "
	              + "pour le loyer.");
	    }
	
	    long montantAPayer = montantDestineAuLoyer - penalite;
	
	    
	    if (montantAPayer > 0) {
	        loyannService.affecterPaiement(
	                encaisse,
	                bail,
	                montantAPayer,
	                encaisse.getEncDate() != null
	                        ? encaisse.getEncDate()
	                        : LocalDate.now());
	    }
	
	    
	    encaisse.setEncvalide(true);
	    encaisse.setStatut(2);
	
	    if (bail.isPremierRecouvre()) {
	        bail.setPremierRecouvre(false);
	    }
	
        bail.setDerniereDatePaiement(
                encaisse.getEncDate() != null
                        ? encaisse.getEncDate()
                        : LocalDate.now());
        
     // Le paiement solde-t-il toutes les échéances affichées ?
                
	    bailRepository.save(bail);
	    
	    repo.saveAndFlush(encaisse);
	}*/

	
	public Optional<Encaisse> findById(Integer id) {
	    return repo.findById(id);
	}
	
	public Encaisse findByIdOrThrow(Integer id) {
	    return repo.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Encaisse introuvable"));
	}
	
	public Optional<Encaisse> findByIdRelations(Integer id) {
	    return repo.findByIdWithRelations(id);
	}
	
	public Optional<Encaisse> findByChequeAndAgence(String filename, Integer agenceId) {
	    return repo.findByChequePathAndAgence(filename, agenceId);
	}
	
	
	public Page<EncaisseDTO> search(
	        String keyword,
	        LocalDate startDate,
	        LocalDate endDate,
	        Pageable pageable) {

	    Integer agenceId = getCurrentAgence().getId();

	    Utilisateur userConnected = getCurrentUser();

	    Integer agentId = null;
	    Integer statut = null;

	    // =========================
	    // SI PAS DIRECTEUR
	    // =========================
	    if (!userConnected.hasRole("DIREC")) {
	        agentId = userConnected.getId();

	        // voit tous les statuts
	        statut = null;
	    } else {
	        // DIREC voit uniquement statut = 1
	        statut = 1;
	    }

	    String search = "";

	    if (keyword != null && !keyword.isBlank()) {
	        search = "%" + keyword.toLowerCase().trim() + "%";
	    }

	    return repo.searchEncaisse(
	            search,
	            agenceId,
	            agentId,
	            statut,
	            startDate,
	            endDate,
	            pageable
	    );
	}
	
	
	public Page<EncaisseDTO> searchPeriode(
	        String keyword,
	        LocalDate startDate,
	        LocalDate endDate,
	        Pageable pageable) {

	    Integer agenceId = getCurrentAgence().getId();

	    Utilisateur userConnected = getCurrentUser();

	    Integer agentId = null;

	    // =========================
	    // SI PAS DIRECTEUR
	    // =========================

	    if (!userConnected.hasRole("DIREC")) {
	        agentId = userConnected.getId();
	    }

	    String search = "";

	    if (keyword != null && !keyword.isBlank()) {
	        search = "%" + keyword.toLowerCase().trim() + "%";
	    }

	    return repo.searchPeriode(
	            search,
	            agenceId,
	            agentId,
	            startDate,
	            endDate,
	            pageable
	    );
	}
	
}
