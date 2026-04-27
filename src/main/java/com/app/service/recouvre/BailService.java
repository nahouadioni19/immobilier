package com.app.service.recouvre;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.BailDTO;
import com.app.entities.recouvre.Appartement;
import com.app.entities.recouvre.Bail;
import com.app.entities.recouvre.Immeuble;
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
public class BailService extends BaseService<Bail>{

	private final BailRepository repo;
	private final AppartementRepository appartementRepository;
	private final EncaisseRepository encaisseRepository;
	
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

        Appartement appart = appartementRepository
                .findById(idAppart)
                .orElseThrow(() ->
                    new RuntimeException("Appartement introuvable")
                );

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

        Bail ancienBail = repo.findById(bail.getId())
                .orElseThrow(() ->
                    new RuntimeException("Bail introuvable")
                );

        Appartement ancienAppart = ancienBail.getAppartement();

        Integer newAppartId = bail.getAppartement().getId();

        Appartement nouvelAppart = appartementRepository
                .findById(newAppartId)
                .orElseThrow(() ->
                    new RuntimeException("Appartement introuvable")
                );

        if (!ancienAppart.getId().equals(nouvelAppart.getId())) {

            ancienAppart.setStatut(StatutAppartement.LIBRE);

            if (nouvelAppart.getStatut() == StatutAppartement.OCCUPE) {
                throw new RuntimeException(
                    "Le nouvel appartement est déjà occupé"
                );
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
        ancienBail.setAvance(bail.getAvance());           // ✅ AJOUT
        ancienBail.setHonoraire(bail.getHonoraire());     // ✅ AJOUT
        ancienBail.setTotal(bail.getTotal());             // ✅ AJOUT

        ancienBail.setAppartement(nouvelAppart);

        return repo.save(ancienBail);
    }

    @Transactional
    public void resilierBail(Bail bail) {
        bail.setStatut(StatutBail.RESILIE);
        bail.getAppartement().setStatut(StatutAppartement.LIBRE);
        repo.save(bail);
    }
    
   /* public List<BailSelectProjection> findBailDetailsNative(UserPrincipal principal, Long agentId, String keyword) {

        boolean isAgentRecouv = principal.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_RECOUV".equals(auth.getAuthority()));

        // Agent recouvrement → uniquement ses contrats
        if (isAgentRecouv) {
        	
        	if (keyword != null && keyword.trim().isEmpty()) {
                keyword = null;
            }
        	
            return repo.findBailDetailsByUtilisateur(principal.getUtilisateur().getId(),keyword);
        }

        // Admin → tous ou filtré
        if (agentId != null && agentId <= 0) {
            agentId = null;
        }

        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }
        
        return repo.findBailDetailsAdmin(agentId,keyword);
    }*/
    
	public Page<BailSelectProjection> findBailDetails(UserPrincipal principal, Pageable pageable) {
	    boolean isAdmin = principal.getAuthorities().stream()
	            .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));

	   // if (isAdmin) {
	        return repo.findBailDetailsAdmin(pageable);
	    /*} else {
	        return repo.findBailDetailsByUtilisateur(principal.getUsername(), pageable);
	    }*/
	}


	@Transactional
	public void resilier(Integer id) {

	    Bail bail = repo.findById(id)
	        .orElseThrow();

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
    
    public Optional<Bail> findByIdWithLocataire(int id) {
        return repo.findByIdWithLocataire(id);
    }
    
    public Optional<Bail> findByIdWithLocataireAndAppartement(int id){
    	return repo.findByIdWithLocataireAndAppartement(id);
    }
    
    public Page<BailDTO> search(String keyword, Pageable pageable) {
	    if (keyword == null || keyword.trim().isEmpty()) {
	        return repo.findAllDTO(pageable);
	    }
	    return repo.search(keyword, pageable);
	}
    
    public List<BailSelectProjection> findBailDetailsNative(
            UserPrincipal principal,
            Long agentId,
            String keyword,
            Integer id) {

        // MODE EDIT
        if (id != null) {
            BailSelectProjection bail = repo.findBailById(id);
            return bail != null ? List.of(bail) : List.of();
        }

        boolean isAgentRecouv = principal.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_RECOUV".equals(auth.getAuthority()));

        // Agent recouvrement → uniquement ses contrats
        if (isAgentRecouv) {

            if (keyword != null && keyword.trim().isEmpty()) {
                keyword = null;
            }

            return repo.findBailDetailsByUtilisateur(
                    principal.getUtilisateur().getId(),
                    keyword
            );
        }

        // Admin → tous ou filtré
        if (agentId != null && agentId <= 0) {
            agentId = null;
        }

        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        return repo.findBailDetailsAdmin(agentId, keyword);
    }
    
    @Transactional
    public void supprimerBail(Integer id) {

        Bail bail = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Bail introuvable"));

        if (encaisseRepository.existsByBailId(id)) {

            // 🔴 cas critique : encaissements existants
            bail.setActif(false);
            bail.setDeleted(true);

        } else {
            // 🟢 suppression physique autorisée
            repo.delete(bail);
            return;
        }

        repo.save(bail);
    }
    
}
