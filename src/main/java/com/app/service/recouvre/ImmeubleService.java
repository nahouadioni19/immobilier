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
import com.app.repositories.recouvre.AppartementRepository;
import com.app.repositories.recouvre.BailleurRepository;
import com.app.repositories.recouvre.ImmeubleRepository;
import com.app.service.base.BaseService;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class ImmeubleService extends BaseService<Immeuble> {

	private final ImmeubleRepository repo;
	private final BailleurRepository bailleurRepository;
	private final UtilisateurRepository utilisateurRepository;
	private final AppartementRepository appartementRepository;
	
	@Override
    public JpaRepository<Immeuble, Integer> getRepository() {
        return repo;
    }
	
	public Optional<Immeuble> findByIdAppartements(Integer id) {
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
        List<Appartement> formList = form.getAppartements();

        if (immeuble == null) {
            throw new IllegalArgumentException("Immeuble obligatoire");
        }

        Immeuble entity = (immeuble.getId() == null)
                ? new Immeuble()
                : repo.findById(immeuble.getId())
                .orElseThrow(() -> new IllegalArgumentException("Immeuble introuvable"));

        if (immeuble.getId() == null) {
            entity.setAgence(getCurrentAgence());
        }

        // ===== IMMEUBLE =====
        entity.setNomImmeuble(immeuble.getNomImmeuble());
        entity.setAdresse(immeuble.getAdresse());
        entity.setVille(immeuble.getVille());
        entity.setCodeImmeuble(immeuble.getCodeImmeuble());
        entity.setAnneeConstruction(immeuble.getAnneeConstruction());
        entity.setNombreEtages(immeuble.getNombreEtages());
        entity.setNumeroTitreFoncier(immeuble.getNumeroTitreFoncier());

        // ===== BAILLEUR =====
        Bailleur bailleur = bailleurRepository.findById(
                immeuble.getBailleur().getId()
        ).orElseThrow(() ->
                new IllegalArgumentException("Bailleur introuvable"));

        entity.setBailleur(bailleur);

        // ===== UTILISATEUR =====
        if (immeuble.getUtilisateur() != null
                && immeuble.getUtilisateur().getId() != null) {

            Utilisateur user = utilisateurRepository.findById(
                    immeuble.getUtilisateur().getId()
            ).orElseThrow(() ->
                    new IllegalArgumentException("Utilisateur introuvable"));

            entity.setUtilisateur(user);
        }

        // =========================
        // MAP EXISTANTS
        // =========================
        Map<Integer, Appartement> existing =
                entity.getAppartements()
                        .stream()
                        .filter(a -> a.getId() != null)
                        .collect(Collectors.toMap(
                                Appartement::getId,
                                Function.identity()
                        ));

        List<Appartement> finalList = new ArrayList<>();

        // =========================
        // CREATE / UPDATE
        // =========================
        if (formList != null) {

            for (Appartement aForm : formList) {

                if (aForm.getNumAppart() == null
                        || aForm.getNumAppart().isBlank()) {
                    continue;
                }

                Appartement app;

                // UPDATE
                if (aForm.getId() != null
                        && existing.containsKey(aForm.getId())) {

                    app = existing.get(aForm.getId());

                } else {

                    // CREATE
                    app = new Appartement();

                    app.setAgence(getCurrentAgence());
                    app.setImmeuble(entity);
                }

                app.setNumAppart(aForm.getNumAppart());
                app.setLibelle(aForm.getLibelle());
                app.setLoyerMensuel(aForm.getLoyerMensuel());
                app.setCaution(aForm.getCaution());
                app.setChargesMensuelles(aForm.getChargesMensuelles());
                app.setStatut(aForm.getStatut());

                finalList.add(app);
            }
        }

        // =========================
        // SYNCHRO COLLECTION
        // =========================
        entity.getAppartements().clear();

        for (Appartement app : finalList) {

            app.setImmeuble(entity);

            entity.getAppartements().add(app);
        }

        return repo.save(entity);
    }
    
    
   /* @Transactional
    public Immeuble saveImmeubleWithAppartement(ImmeubForm form) {

        Immeuble immeuble = form.getImmeuble();
        List<Appartement> formList = form.getAppartements();

        if (immeuble == null) {
            throw new IllegalArgumentException("Immeuble obligatoire");
        }

        Immeuble entity = (immeuble.getId() == null)
                ? new Immeuble()
                : repo.findById(immeuble.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Immeuble introuvable"));

        if (immeuble.getId() == null) {
            entity.setAgence(getCurrentAgence());
        } else if (!entity.getAgence().getId().equals(getCurrentAgence().getId())) {
            throw new SecurityException("Accès refusé");
        }

        // ===== IMMEUBLE =====
        entity.setNomImmeuble(immeuble.getNomImmeuble());
        entity.setAdresse(immeuble.getAdresse());
        entity.setVille(immeuble.getVille());
        entity.setCodeImmeuble(immeuble.getCodeImmeuble());
        entity.setAnneeConstruction(immeuble.getAnneeConstruction());
        entity.setNombreEtages(immeuble.getNombreEtages());
        entity.setNumeroTitreFoncier(immeuble.getNumeroTitreFoncier());

        // ===== BAILLEUR =====
        Bailleur bailleur = bailleurRepository.findById(immeuble.getBailleur().getId())
                .orElseThrow(() -> new IllegalArgumentException("Bailleur introuvable"));
        entity.setBailleur(bailleur);

        // ===== UTILISATEUR =====
        if (immeuble.getUtilisateur() != null && immeuble.getUtilisateur().getId() != null) {
            Utilisateur user = utilisateurRepository.findById(immeuble.getUtilisateur().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
            entity.setUtilisateur(user);
        }

        // =========================
        // MAP EXISTANTS (IMPORTANT : snapshot)
        // =========================
        Map<Integer, Appartement> existing = entity.getAppartements()
                .stream()
                .filter(a -> a.getId() != null)
                .collect(Collectors.toMap(Appartement::getId, Function.identity()));

        Set<Integer> incomingIds = new HashSet<>();

        if (formList != null) {

            for (Appartement aForm : formList) {

                if (aForm.getNumAppart() == null || aForm.getNumAppart().isBlank()) {
                    continue;
                }

                Appartement app;

                if (aForm.getId() != null && existing.containsKey(aForm.getId())) {

                    // ✅ UPDATE DIRECT SUR ENTITÉ MANAGÉE
                    app = existing.get(aForm.getId());

                } else {
                    // ✅ CREATE
                    app = new Appartement();
                    app.setAgence(getCurrentAgence());
                    app.setImmeuble(entity);
                    entity.getAppartements().add(app);
                    app.setStatut(aForm.getStatut());
                }

                app.setNumAppart(aForm.getNumAppart());
                app.setLibelle(aForm.getLibelle());
                app.setLoyerMensuel(aForm.getLoyerMensuel());
                app.setCaution(aForm.getCaution());
                app.setChargesMensuelles(aForm.getChargesMensuelles());

                if (app.getId() != null) {
                    incomingIds.add(app.getId());
                }
            }
        }

        // =========================
        // DELETE SAFE (optionnel)
        // =========================
        entity.getAppartements().removeIf(app ->
                app.getId() != null && !incomingIds.contains(app.getId())
        );

        return repo.save(entity);
    }*/
    
    /*@Transactional
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
    }*/
    
    
    //
   public Page<Immeuble> searchPatrimoine(String keyword, Pageable pageable) {

	   Integer agenceId = getCurrentAgenceId();
	   
	    if (keyword == null || keyword.trim().isEmpty()) {
	        return repo.findByAgenceId(agenceId, pageable);
	    }

	    return repo.search(keyword.trim(), agenceId, pageable);
	}
   
   public Page<ImmeubleDTO> searchDTO(String keyword, Pageable pageable) {
	   
	   Integer agenceId = getCurrentAgenceId();
	   
	    if (keyword == null || keyword.trim().isEmpty()) {
	        return repo.findAllDTO(agenceId, pageable);
	    }

	    return repo.searchDTO(keyword.toLowerCase(), agenceId, pageable);
	}
}
