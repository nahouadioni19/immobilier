package com.app.service.recouvre;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.ImmeubleDTO;
import com.app.entities.recouvre.Appartement;
import com.app.entities.recouvre.Bailleur;
import com.app.entities.recouvre.Immeuble;
import com.app.enums.StatutAppartement;
import com.app.form.ImmeubForm;
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
import java.util.Objects;
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

        if (form == null || form.getImmeuble() == null) {
            throw new IllegalArgumentException("Immeuble obligatoire");
        }

        Immeuble immeubleForm = form.getImmeuble();
        List<Appartement> formList = form.getAppartements();

        Integer agenceId = getCurrentAgence().getId();

        // =====================================================
        // CHARGER OU CRÉER L'IMMEUBLE
        // =====================================================
        Immeuble entity;

        if (immeubleForm.getId() == null) {

            entity = new Immeuble();
            entity.setAgence(getCurrentAgence());

        } else {

            entity = repo.findById(immeubleForm.getId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Immeuble introuvable"));

            // Vérifier l'appartenance à l'agence courante.
            if (entity.getAgence() == null
                    || !agenceId.equals(entity.getAgence().getId())) {
                throw new IllegalArgumentException(
                        "Vous n'êtes pas autorisé à modifier cet immeuble."
                );
            }
        }

        // =====================================================
        // INFORMATIONS DE L'IMMEUBLE
        // =====================================================
        entity.setNomImmeuble(immeubleForm.getNomImmeuble());
        entity.setAdresse(immeubleForm.getAdresse());
        entity.setVille(immeubleForm.getVille());
        entity.setCodeImmeuble(immeubleForm.getCodeImmeuble());
        entity.setAnneeConstruction(immeubleForm.getAnneeConstruction());
        entity.setNombreEtages(immeubleForm.getNombreEtages());
        entity.setNumeroTitreFoncier(immeubleForm.getNumeroTitreFoncier());

        // =====================================================
        // BAILLEUR
        // =====================================================
        if (immeubleForm.getBailleur() == null
                || immeubleForm.getBailleur().getId() == null) {
            throw new IllegalArgumentException("Bailleur obligatoire");
        }

        Bailleur bailleur = bailleurRepository
                .findById(immeubleForm.getBailleur().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Bailleur introuvable"));

        // Si Bailleur possède une agence, vérifier également
        // qu'il appartient à l'agence courante.
        entity.setBailleur(bailleur);

        // =====================================================
        // AGENT DE RECOUVREMENT
        // =====================================================
        if (immeubleForm.getUtilisateur() != null
                && immeubleForm.getUtilisateur().getId() != null) {

            Utilisateur utilisateur = utilisateurRepository
                    .findById(immeubleForm.getUtilisateur().getId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Utilisateur introuvable"));

            entity.setUtilisateur(utilisateur);
        }

        // =====================================================
        // INDEXER LES APPARTEMENTS EXISTANTS DE CET IMMEUBLE
        // =====================================================
        Map<Integer, Appartement> existing = entity.getAppartements()
                .stream()
                .filter(a -> a.getId() != null)
                .collect(Collectors.toMap(
                        Appartement::getId,
                        Function.identity()
                ));

        List<Appartement> finalList = new ArrayList<>();
        Set<Integer> idsTraites = new HashSet<>();

        // =====================================================
        // CRÉATION / MODIFICATION DES APPARTEMENTS
        // =====================================================
        if (formList != null) {

            for (Appartement aForm : formList) {

                if (aForm == null) {
                    continue;
                }

                if (aForm.getNumAppart() == null
                        || aForm.getNumAppart().isBlank()) {
                    continue;
                }

                if (aForm.getLibelle() == null
                        || aForm.getLibelle().isBlank()) {
                    throw new IllegalArgumentException(
                            "Le libellé est obligatoire pour l'appartement "
                                    + aForm.getNumAppart()
                    );
                }

                // Valider le loyer de référence.
                if (aForm.getLoyerMensuel() != null
                        && aForm.getLoyerMensuel() < 0) {
                    throw new IllegalArgumentException(
                            "Le loyer de référence ne peut pas être négatif."
                    );
                }

                Appartement app;

                if (aForm.getId() != null) {

                    // Un ID soumis doit correspondre à un appartement
                    // appartenant réellement à cet immeuble.
                    app = existing.get(aForm.getId());

                    if (app == null) {
                        throw new IllegalArgumentException(
                                "Appartement introuvable dans cet immeuble : "
                                        + aForm.getId()
                        );
                    }

                    if (!idsTraites.add(aForm.getId())) {
                        throw new IllegalArgumentException(
                                "Appartement envoyé plusieurs fois : "
                                        + aForm.getId()
                        );
                    }

                } else {

                    // Nouvel appartement.
                    app = new Appartement();
                    app.setAgence(getCurrentAgence());
                    app.setImmeuble(entity);

                    // Le statut initial est défini côté serveur.
                    app.setStatut(StatutAppartement.LIBRE);
                }

                // Informations de l'appartement.
                app.setNumAppart(aForm.getNumAppart().trim());
                app.setLibelle(aForm.getLibelle().trim());

                // LOYER DE RÉFÉRENCE MODIFIABLE.
                // Cette modification ne change pas les baux existants.
                app.setLoyerMensuel(aForm.getLoyerMensuel());

                app.setCaution(aForm.getCaution());
                app.setChargesMensuelles(aForm.getChargesMensuelles());

                // Ne pas permettre au formulaire de libérer
                // artificiellement un appartement déjà occupé.
                if (aForm.getId() != null) {
                    // Conserver le statut réel de l'appartement existant.
                    // Si le statut doit être modifiable, traiter cette
                    // modification dans une opération métier dédiée.
                }

                finalList.add(app);
            }
        }

        // =====================================================
        // SYNCHRONISER LA COLLECTION
        // =====================================================
        // Attention : les appartements existants omis du formulaire
        // ne doivent pas être supprimés automatiquement s'ils sont
        // occupés ou liés à des baux.
        //
        // Cette version conserve les appartements existants omis.
        for (Appartement app : existing.values()) {

            if (!idsTraites.contains(app.getId())) {

                boolean dejaPresent = finalList.stream()
                        .anyMatch(a -> Objects.equals(a.getId(), app.getId()));

                if (!dejaPresent) {
                    finalList.add(app);
                }
            }
        }

        entity.getAppartements().clear();

        for (Appartement app : finalList) {
            app.setImmeuble(entity);
            entity.getAppartements().add(app);
        }

        return repo.save(entity);
    }
    
    
    
    /*@Transactional
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
    */    
    
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
