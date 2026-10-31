package com.app.service.recouvre;

import java.time.LocalDate;

import com.app.repositories.recouvre.RevisionLoyerRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.BailDTO;
import com.app.dto.revision.RevisionLoyerDto;
import com.app.entities.administration.Agence;
import com.app.entities.recouvre.Appartement;
import com.app.entities.recouvre.Bail;
import com.app.entities.recouvre.RevisionLoyer;
import com.app.enums.StatutAppartement;
import com.app.enums.StatutBail;
import com.app.repositories.BailSelectProjection;
import com.app.repositories.recouvre.AppartementRepository;
import com.app.repositories.recouvre.BailRepository;
import com.app.repositories.recouvre.EncaisseRepository;
import com.app.security.UserPrincipal;
import com.app.service.base.BaseService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BailService extends BaseService<Bail> {

	private final BailRepository repo;
	private final AppartementRepository appartementRepository;
	private final EncaisseRepository encaisseRepository;
	private final RevisionLoyerRepository revisionLoyerRepository;

	@Override
	public JpaRepository<Bail, Integer> getRepository() {
		return repo;
	}

	@Override
	public void afterUpdate(Bail entity) {
		super.afterUpdate(entity);
		// clearCache(TYPE_ROLE_CACHE);
	}

	@Override
	public void afterSave(Bail entity) {
		super.afterSave(entity);
	}

	public Page<Bail> findAll(Pageable pageable) {
		return repo.findAll(pageable);
	}

	public long calculerDureeEnMois(Bail bail) {
		return bail.getDureeEnMois();
	}

	@Transactional
	public Bail creerBail(Bail bail) {
		if (bail.getLocataire() == null || bail.getLocataire().getId() == null) {
			throw new RuntimeException("Veuillez sélectionner un locataire valide");
		}

		if (bail.getAppartement() == null || bail.getAppartement().getId() == null) {
			throw new RuntimeException("Veuillez sélectionner un appartement valide");
		}

		Integer idAppart = bail.getAppartement().getId();

		Appartement appart = appartementRepository.findById(idAppart)
				.orElseThrow(() -> new RuntimeException("Appartement introuvable"));

		if (appart.getStatut() == StatutAppartement.OCCUPE) {
			throw new RuntimeException("Appartement déjà occupé");
		}

		// Passer à OCCUPE
		appart.setStatut(StatutAppartement.OCCUPE);

		bail.setAppartement(appart);
		bail.setAgence(getCurrentAgence());

		return repo.save(bail);
	}

	

	@Transactional
	public Bail modifierBail(Bail bail) {
	
	    if (bail == null || bail.getId() == null) {
	        throw new IllegalArgumentException(
	                "Identifiant du bail obligatoire.");
	    }
	
	    // 1. Charger le bail existant depuis la base
	    Bail ancienBail = repo.findById(bail.getId())
	            .orElseThrow(() ->
	                    new RuntimeException("Bail introuvable."));
	
	    // 2. Vérifier l'agence propriétaire du bail
	    Integer agenceId = getCurrentAgenceId();
	
	    if (agenceId == null
	            || ancienBail.getAgence() == null
	            || ancienBail.getAgence().getId() == null
	            || !agenceId.equals(ancienBail.getAgence().getId())) {
	
	        throw new org.springframework.security.access.AccessDeniedException(
	                "Vous n'êtes pas autorisé à modifier ce bail.");
	    }
	
	    // 3. Vérifier le locataire
	    if (bail.getLocataire() == null
	            || bail.getLocataire().getId() == null) {
	        throw new IllegalArgumentException(
	                "Veuillez sélectionner un locataire valide.");
	    }
	
	    // 4. Vérifier l'appartement
	    if (bail.getAppartement() == null
	            || bail.getAppartement().getId() == null) {
	        throw new IllegalArgumentException(
	                "Veuillez sélectionner un appartement valide.");
	    }
	
	    // 5. Charger le nouvel appartement
	    Appartement ancienAppart = ancienBail.getAppartement();
	
	    Appartement nouvelAppart = appartementRepository
	            .findById(bail.getAppartement().getId())
	            .orElseThrow(() ->
	                    new RuntimeException("Appartement introuvable."));
	
	    // 6. Gérer un éventuel changement d'appartement
	    if (!ancienAppart.getId().equals(nouvelAppart.getId())) {
	
	        if (nouvelAppart.getStatut() == StatutAppartement.OCCUPE) {
	            throw new RuntimeException(
	                    "Le nouvel appartement est déjà occupé.");
	        }
	
	        ancienAppart.setStatut(StatutAppartement.LIBRE);
	        nouvelAppart.setStatut(StatutAppartement.OCCUPE);
	    }
	
	    // 7. Mémoriser le loyer contractuel existant
	    Long loyerContractuel = ancienBail.getMontantLoyer();
	    LocalDate dateEffetLoyer = ancienBail.getDateEffetLoyer();
	
	    // 8. Mettre à jour les informations autorisées
	    ancienBail.setDateDebut(bail.getDateDebut());
	    ancienBail.setDateFin(bail.getDateFin());
	
	    ancienBail.setLocataire(bail.getLocataire());
	    ancienBail.setAppartement(nouvelAppart);
	
	    ancienBail.setMontantCharges(bail.getMontantCharges());
	    ancienBail.setCaution(bail.getCaution());
	    ancienBail.setAvance(bail.getAvance());
	    ancienBail.setHonoraire(bail.getHonoraire());
	
	    // 9. PROTECTION : conserver le loyer enregistré
	    ancienBail.setMontantLoyer(loyerContractuel);
	    ancienBail.setDateEffetLoyer(dateEffetLoyer);
	
	    // 10. Sauvegarder le bail
	    return repo.save(ancienBail);
	}

	/**
	 * Réviser le loyer contractuel d'un bail actif.
	 *
	 * La révision est réservée à l'agence propriétaire du bail.
	 * Une révision prend effet immédiatement à la date renseignée,
	 * qui ne peut pas être postérieure à la date du jour.
	 */

	
	@Transactional
	public Bail reviserLoyer(
	        Integer bailId,
	        Long nouveauMontantLoyer,
	        LocalDate dateEffetLoyer) {

	    if (bailId == null) {
	        throw new IllegalArgumentException(
	                "L'identifiant du bail est obligatoire.");
	    }

	    if (nouveauMontantLoyer == null || nouveauMontantLoyer <= 0) {
	        throw new IllegalArgumentException(
	                "Le nouveau montant du loyer doit être supérieur à zéro.");
	    }

	    if (dateEffetLoyer == null) {
	        throw new IllegalArgumentException(
	                "La date d'effet de la révision est obligatoire.");
	    }

	    // Verrou : deux révisions simultanées ne doivent pas se croiser.
	    Bail bail = repo.trouverPourMiseAJour(bailId)
	            .orElseThrow(() ->
	                    new IllegalArgumentException("Bail introuvable."));

	    Integer agenceId = getCurrentAgenceId();

	    if (agenceId == null
	            || bail.getAgence() == null
	            || bail.getAgence().getId() == null
	            || !agenceId.equals(bail.getAgence().getId())) {

	        throw new org.springframework.security.access.AccessDeniedException(
	                "Vous n'êtes pas autorisé à réviser le loyer de ce bail.");
	    }

	    // Seul un bail résilié est bloqué : un bail expiré peut encore avoir
	    // des arriérés dont le montant dépend de la révision.
	    if (bail.getStatut() == StatutBail.RESILIE) {
	        throw new IllegalStateException(
	                "Impossible de réviser le loyer d'un bail résilié.");
	    }

	    if (bail.getDateDebut() != null
	            && dateEffetLoyer.isBefore(bail.getDateDebut())) {
	        throw new IllegalArgumentException(
	                "La date d'effet ne peut pas précéder le début du bail.");
	    }

	    if (dateEffetLoyer.isAfter(LocalDate.now())) {
	        throw new IllegalArgumentException(
	                "La date d'effet ne peut pas être dans le futur.");
	    }

	    Long ancienMontant = bail.getMontantLoyer();

	    if (ancienMontant == null || ancienMontant <= 0) {
	        throw new IllegalStateException(
	                "Le loyer actuel du bail est invalide.");
	    }

	    if (ancienMontant.equals(nouveauMontantLoyer)) {
	        throw new IllegalArgumentException(
	                "Le nouveau loyer est identique au loyer actuel.");
	    }

	    // L'historique doit rester ordonné : chaque révision est postérieure
	    // à la précédente, sinon « ancien montant » devient incohérent.
	    revisionLoyerRepository.findTopByBailIdOrderByDateEffetDesc(bailId)
	            .ifPresent(derniere -> {
	                if (!dateEffetLoyer.isAfter(derniere.getDateEffet())) {
	                    throw new IllegalArgumentException(
	                            "La date d'effet doit être postérieure à la dernière révision ("
	                          + derniere.getDateEffet().format(
	                                  java.time.format.DateTimeFormatter
	                                          .ofPattern("dd/MM/yyyy"))
	                          + ").");
	                }
	            });

	    RevisionLoyer revision = new RevisionLoyer();
	    revision.setBail(bail);
	    revision.setAgence(bail.getAgence());
	    revision.setAncienMontant(ancienMontant);
	    revision.setNouveauMontant(nouveauMontantLoyer);
	    revision.setDateEffet(dateEffetLoyer);
	    revision.setMotif("Révision du loyer");

	    revisionLoyerRepository.save(revision);

	    bail.setMontantLoyer(nouveauMontantLoyer);
	    bail.setDateEffetLoyer(dateEffetLoyer);

	    return repo.save(bail);
	}

	
	
	/*@Transactional
	public Bail reviserLoyer(
	        Integer bailId,
	        Long nouveauMontantLoyer,
	        LocalDate dateEffetLoyer) {

	    // 1. Vérifier les paramètres
	    if (bailId == null) {
	        throw new IllegalArgumentException(
	                "L'identifiant du bail est obligatoire.");
	    }

	    if (nouveauMontantLoyer == null || nouveauMontantLoyer <= 0) {
	        throw new IllegalArgumentException(
	                "Le nouveau montant du loyer doit être supérieur à zéro.");
	    }

	    if (dateEffetLoyer == null) {
	        throw new IllegalArgumentException(
	                "La date d'effet de la révision est obligatoire.");
	    }

	    // 2. Charger le bail existant
	    Bail bail = repo.findById(bailId)
	            .orElseThrow(() ->
	                    new IllegalArgumentException("Bail introuvable."));

	    // 3. Vérifier l'agence propriétaire du bail
	    Integer agenceId = getCurrentAgenceId();

	    if (agenceId == null
	            || bail.getAgence() == null
	            || bail.getAgence().getId() == null
	            || !agenceId.equals(bail.getAgence().getId())) {

	        throw new org.springframework.security.access.AccessDeniedException(
	                "Vous n'êtes pas autorisé à réviser le loyer de ce bail.");
	    }

	    // 4. Vérifier que le bail est actif
	    if (bail.getStatut() != StatutBail.ACTIF) {
	        throw new IllegalStateException(
	                "Impossible de réviser le loyer d'un bail résilié.");
	    }

	    // 5. Vérifier la date d'effet
	    if (bail.getDateDebut() != null
	            && dateEffetLoyer.isBefore(bail.getDateDebut())) {
	        throw new IllegalArgumentException(
	                "La date d'effet ne peut pas précéder le début du bail.");
	    }

	    if (dateEffetLoyer.isAfter(LocalDate.now())) {
	        throw new IllegalArgumentException(
	                "La date d'effet ne peut pas être dans le futur.");
	    }

	    // 6. Récupérer le loyer actuel
	    Long ancienMontant = bail.getMontantLoyer();

	    if (ancienMontant == null || ancienMontant <= 0) {
	        throw new IllegalStateException(
	                "Le loyer actuel du bail est invalide.");
	    }

	    if (ancienMontant.equals(nouveauMontantLoyer)) {
	        throw new IllegalArgumentException(
	                "Le nouveau loyer est identique au loyer actuel.");
	    }

	    // 7. Créer l'historique de la révision
	    RevisionLoyer revision = new RevisionLoyer();
	    revision.setBail(bail);
	    revision.setAgence(bail.getAgence());
	    revision.setAncienMontant(ancienMontant);
	    revision.setNouveauMontant(nouveauMontantLoyer);
	    revision.setDateEffet(dateEffetLoyer);
	    revision.setMotif("Révision du loyer");

	    // Enregistrer la révision dans t_revision_loyer
	    revisionLoyerRepository.save(revision);

	    // 8. Mettre à jour le loyer courant dans t_bail
	    bail.setMontantLoyer(nouveauMontantLoyer);
	    bail.setDateEffetLoyer(dateEffetLoyer);

	    return repo.save(bail);
	}*/
	
	
	/**@Transactional
	public Bail reviserLoyer(
	        Integer bailId,
	        Long nouveauMontantLoyer,
	        LocalDate dateEffetLoyer) {

	    // 1. Vérifier les paramètres
	    if (bailId == null) {
	        throw new IllegalArgumentException(
	                "L'identifiant du bail est obligatoire.");
	    }

	    if (nouveauMontantLoyer == null || nouveauMontantLoyer <= 0) {
	        throw new IllegalArgumentException(
	                "Le nouveau montant du loyer doit être supérieur à zéro.");
	    }

	    if (dateEffetLoyer == null) {
	        throw new IllegalArgumentException(
	                "La date d'effet de la révision est obligatoire.");
	    }

	    // 2. Charger le bail existant
	    Bail bail = repo.findById(bailId)
	            .orElseThrow(() ->
	                    new IllegalArgumentException("Bail introuvable."));

	    // 3. Vérifier l'agence propriétaire
	    Integer agenceId = getCurrentAgenceId();

	    if (agenceId == null
	            || bail.getAgence() == null
	            || bail.getAgence().getId() == null
	            || !agenceId.equals(bail.getAgence().getId())) {

	        throw new org.springframework.security.access.AccessDeniedException(
	                "Vous n'êtes pas autorisé à réviser le loyer de ce bail.");
	    }

	    // 4. Vérifier que le bail est actif
	    if (bail.getStatut() != StatutBail.ACTIF) {
	        throw new IllegalStateException(
	                "Impossible de réviser le loyer d'un bail résilié.");
	    }

	    // 5. Vérifier la cohérence de la date d'effet
	    if (bail.getDateDebut() != null
	            && dateEffetLoyer.isBefore(bail.getDateDebut())) {

	        throw new IllegalArgumentException(
	                "La date d'effet ne peut pas précéder la date de début du bail.");
	    }

	    if (dateEffetLoyer.isAfter(LocalDate.now())) {
	        throw new IllegalArgumentException(
	                "La date d'effet ne peut pas être dans le futur. "
	                + "Une révision future nécessite une programmation.");
	    }

	    // 6. Vérifier que le nouveau loyer est différent
	    if (bail.getMontantLoyer() != null
	            && bail.getMontantLoyer().equals(nouveauMontantLoyer)) {

	        throw new IllegalArgumentException(
	                "Le nouveau loyer est identique au loyer actuel.");
	    }

	    // 7. Mettre à jour le loyer contractuel
	    bail.setMontantLoyer(nouveauMontantLoyer);
	    bail.setDateEffetLoyer(dateEffetLoyer);

	    // 8. Enregistrer la modification
	    return repo.save(bail);
	}*/
	
	/**@Transactional
	public Bail modifierBail(Bail bail) {

		Bail ancienBail = repo.findById(bail.getId()).orElseThrow(() -> new RuntimeException("Bail introuvable"));

		Appartement ancienAppart = ancienBail.getAppartement();

		Integer newAppartId = bail.getAppartement().getId();

		Appartement nouvelAppart = appartementRepository.findById(newAppartId)
				.orElseThrow(() -> new RuntimeException("Appartement introuvable"));

		if (!ancienAppart.getId().equals(nouvelAppart.getId())) {

			ancienAppart.setStatut(StatutAppartement.LIBRE);

			if (nouvelAppart.getStatut() == StatutAppartement.OCCUPE) {
				throw new RuntimeException("Le nouvel appartement est déjà occupé");
			}

			nouvelAppart.setStatut(StatutAppartement.OCCUPE);
		}

		// 🔥 Mise à jour complète
		ancienBail.setDateDebut(bail.getDateDebut());
		ancienBail.setDateFin(bail.getDateFin());
		ancienBail.setLocataire(bail.getLocataire());

		ancienBail.setMontantLoyer(bail.getMontantLoyer());
		ancienBail.setMontantCharges(bail.getMontantCharges());

		ancienBail.setCaution(bail.getCaution());
		ancienBail.setAvance(bail.getAvance()); // ✅ AJOUT
		ancienBail.setHonoraire(bail.getHonoraire()); // ✅ AJOUT
		ancienBail.setTotal(bail.getTotal()); // ✅ AJOUT

		ancienBail.setAppartement(nouvelAppart);

		return repo.save(ancienBail);
	}*/

	@Transactional
	public void resilierBail(Bail bail) {
		bail.setStatut(StatutBail.RESILIE);
		bail.getAppartement().setStatut(StatutAppartement.LIBRE);
		repo.save(bail);
	}


	@Transactional
	public void resilier(Integer id) {

		Bail bail = repo.findById(id).orElseThrow();

		if (bail.getStatut() == StatutBail.RESILIE) {
			throw new RuntimeException("Bail déjà résilié");
		}

		// 1️⃣ Résilier le bail
		bail.setStatut(StatutBail.RESILIE);
		bail.setDateResiliation(LocalDate.now());

		// 2️⃣ Libérer l'appartement
		Appartement appart = bail.getAppartement();
		appart.setStatut(StatutAppartement.LIBRE);

		repo.save(bail);
	}

	public long countBailsActifs() {
		return repo.countByStatut(StatutBail.ACTIF);
	}

	public long countBailsResilies() {
		return repo.countByStatut(StatutBail.RESILIE);
	}
	

	public Optional<Bail> findByIdWithLocataireAndAppartement(Integer id) {

		Integer agenceId = getCurrentAgenceId();

		return repo.findByIdWithLocataireAndAppartement(id, agenceId);
	}

	
	public Page<BailDTO> search(String keyword, Pageable pageable) {

		Integer agenceId = getCurrentAgenceId();

		if (keyword == null || keyword.trim().isEmpty()) {
			return repo.findAllDTO(agenceId, pageable);
		}
		return repo.search(keyword, agenceId, pageable);
	}

	
	public List<BailSelectProjection> findBailDetailsNative(UserPrincipal principal, Long agentId, String keyword,
			Integer id) {

		// MODE EDIT

		Integer agenceId = getCurrentAgenceId();

		if (id != null) {
			BailSelectProjection bail = repo.findBailById(id, agenceId);
			return bail != null ? List.of(bail) : List.of();
		}

		boolean isAgentRecouv = principal.getAuthorities().stream()
				.anyMatch(auth -> "ROLE_RECOUV".equals(auth.getAuthority()));

		// Agent recouvrement → uniquement ses contrats
		if (isAgentRecouv) {

			if (keyword != null && keyword.trim().isEmpty()) {
				keyword = null;
			}

			return repo.findBailDetailsByUtilisateur(principal.getUtilisateur().getId(), keyword, agenceId);
		}

		// Admin → tous ou filtré
		if (agentId != null && agentId <= 0) {
			agentId = null;
		}

		if (keyword != null && keyword.trim().isEmpty()) {
			keyword = null;
		}

		return repo.findBailDetailsAdmin(agentId, agenceId, keyword);
	}

	
	@Transactional
	public void supprimerBail(Integer id) {

		Bail bail = repo.findById(id).orElseThrow(() -> new RuntimeException("Bail introuvable"));

		if (encaisseRepository.existsByBailId(id)) {

			// cas critique : encaissements existants
			bail.setActif(false);
			bail.setDeleted(true);

		} else {
			// suppression physique autorisée
			repo.delete(bail);
			return;
		}

		repo.save(bail);
	}
	
	
	public List<RevisionLoyerDto> findRevisionsByBail(Integer bailId) {
	    Bail bail = repo.findById(bailId)
	            .orElseThrow(() -> new IllegalArgumentException("Bail introuvable."));

	    checkAgence(bail.getAgence(), getCurrentAgence().getId(),
	            "Ce bail n'appartient pas à votre agence.");

	    return revisionLoyerRepository.findByBailIdOrderByDateEffetAsc(bailId)
	            .stream()
	            .map(r -> new RevisionLoyerDto(
	                    r.getDateEffet(),
	                    r.getAncienMontant(),
	                    r.getNouveauMontant()))
	            .toList();
	}
	
	
	private void checkAgence(Agence agenceEntity, Integer currentAgenceId, String message) {

	    if (agenceEntity == null) {
	        throw new SecurityException(message);
	    }

	    if (!java.util.Objects.equals(agenceEntity.getId(), currentAgenceId)) {
	        throw new SecurityException(message);
	    }
	}

}
