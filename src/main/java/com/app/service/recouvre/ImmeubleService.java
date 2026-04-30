package com.app.service.recouvre;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.ImmeubForm;
import com.app.dto.ImmeubleDTO;
import com.app.entities.recouvre.Appartement;
import com.app.entities.recouvre.Bailleur;
import com.app.entities.recouvre.Immeuble;
import com.app.entities.administration.Utilisateur;
import com.app.repositories.administration.UtilisateurRepository;
import com.app.repositories.recouvre.BailleurRepository;
import com.app.repositories.recouvre.ImmeubleRepository;
import com.app.service.base.BaseService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ImmeubleService extends BaseService<Immeuble> {

	private final ImmeubleRepository repo;
	private final BailleurRepository bailleurRepository;
	private final UtilisateurRepository utilisateurRepository;
	
	@Override
    public JpaRepository<Immeuble, Integer> getRepository() {
        return repo;
    }
	
	public Optional<Immeuble> findByIdWithAppartements(Integer id) {
		Integer agenceId = getCurrentAgenceId();
		
        return repo.findByIdWithAppartements(id, agenceId);
    }
    
    @Override
    public void afterUpdate(Immeuble entity) {
        super.afterUpdate(entity);
    }

    @Override
    public void afterSave(Immeuble entity) {
        super.afterSave(entity);
    }
    
    public Page<Immeuble> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Transactional
    public Immeuble saveWithAppartements(ImmeubForm immeubForm) {
        
    	Immeuble formImmeub = immeubForm.getImmeuble();

        Immeuble immeuble = repo.findById(formImmeub.getId())
            .orElse(new Immeuble());

        // mettre à jour les champs de l’immeuble
        immeuble.setNomImmeuble(formImmeub.getNomImmeuble());
        immeuble.setAdresse(formImmeub.getAdresse());
        immeuble.setPays(formImmeub.getPays());
        // etc...

        // vider les anciens appartements si besoin
        immeuble.getAppartements().clear();

        // Associer les appartements du form
        for (Appartement app : immeubForm.getAppartements()) {
            app.setImmeuble(immeuble);
            immeuble.getAppartements().add(app);
        }

        // Enregistrer en cascade (grâce à CascadeType.ALL sur la relation)
        return repo.save(immeuble);
    }
    
    @Transactional
    public Immeuble saveWithAppartements(Immeuble immeuble) {
        return repo.save(immeuble);
    }
    
    //
    @Transactional
    public Immeuble saveImmeubleWithAppartement(ImmeubForm form) {

        Immeuble immeuble = form.getImmeuble();
        List<Appartement> appartements = form.getAppartements();

        if (immeuble == null) {
            throw new IllegalArgumentException("Immeuble obligatoire");
        }

        boolean isNew = (immeuble.getId() == null);
        Immeuble entity;

        if (isNew) {
            // ✅ création
            entity = new Immeuble();
            entity.setAgence(getCurrentAgence());

        } else {
            // ✅ récupération existant
            entity = repo.findById(immeuble.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Immeuble introuvable"));

            // 🔒 sécurité SaaS
            Integer agenceEntityId = entity.getAgence() != null ? entity.getAgence().getId() : null;
            Integer agenceCurrentId = getCurrentAgence().getId();

            if (agenceEntityId == null || !agenceEntityId.equals(agenceCurrentId)) {
                throw new SecurityException("Accès refusé");
            }
        }

        // =========================
        // 🔄 mapping des champs simples
        // =========================
        entity.setNomImmeuble(immeuble.getNomImmeuble());
        entity.setAdresse(immeuble.getAdresse());
        entity.setVille(immeuble.getVille());
        entity.setCodeImmeuble(immeuble.getCodeImmeuble());
        entity.setAnneeConstruction(immeuble.getAnneeConstruction());
        entity.setNombreEtages(immeuble.getNombreEtages());
        entity.setNumeroTitreFoncier(immeuble.getNumeroTitreFoncier());

        // =========================
        // 🔒 sécurisation Bailleur
        // =========================
        if (immeuble.getBailleur() == null || immeuble.getBailleur().getId() == null) {
            throw new IllegalArgumentException("Bailleur obligatoire");
        }

        Bailleur bailleur = bailleurRepository.findById(immeuble.getBailleur().getId())
                .orElseThrow(() -> new IllegalArgumentException("Bailleur introuvable"));

        if (bailleur.getAgence() == null 
                || !bailleur.getAgence().getId().equals(getCurrentAgence().getId())) {
            throw new SecurityException("Bailleur d’une autre agence");
        }

        entity.setBailleur(bailleur);

        // =========================
        // 🔒 sécurisation Utilisateur
        // =========================
        if (immeuble.getUtilisateur() == null || immeuble.getUtilisateur().getId() == null) {
            throw new IllegalArgumentException("Utilisateur obligatoire");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(immeuble.getUtilisateur().getId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        if (utilisateur.getAgence() == null 
                || !utilisateur.getAgence().getId().equals(getCurrentAgence().getId())) {
            throw new SecurityException("Utilisateur d’une autre agence");
        }

        entity.setUtilisateur(utilisateur);

        // =========================
        // 🔄 gestion des appartements (SAFE)
        // =========================
        entity.getAppartements().clear();

        if (appartements != null) {
            for (Appartement app : appartements) {

                if (app.getNumAppart() == null || app.getNumAppart().trim().isEmpty()) {
                    continue; // ignore lignes vides
                }

                app.setImmeuble(entity);
                // 🔥 IMPORTANT : propagation de l'agence
                app.setAgence(entity.getAgence());

                entity.getAppartements().add(app);
            }
        }

        return repo.save(entity);
    }
    //
   public Page<Immeuble> searchPatrimoine(String keyword, Pageable pageable) {

	   Integer agenceId = getCurrentAgenceId();
	   
	    if (keyword == null || keyword.trim().isEmpty()) {
	        return repo.findByAgenceId(agenceId, pageable);
	    }

	    return repo.search(keyword.toLowerCase(), agenceId, pageable);
	}
   
   public Page<ImmeubleDTO> searchDTO(String keyword, Pageable pageable) {
	   
	   Integer agenceId = getCurrentAgenceId();
	   
	    if (keyword == null || keyword.trim().isEmpty()) {
	        return repo.findAllDTO(agenceId, pageable);
	    }

	    return repo.searchDTO(keyword.toLowerCase(), agenceId, pageable);
	}
}
