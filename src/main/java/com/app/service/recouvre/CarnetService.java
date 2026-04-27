package com.app.service.recouvre;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.CarnetDTO;
import com.app.entities.administration.Agence;
import com.app.entities.administration.Assignation;
import com.app.entities.administration.Utilisateur;
import com.app.entities.recouvre.Bailleur;
import com.app.entities.recouvre.Carnet;
import com.app.entities.recouvre.Identification;
import com.app.entities.recouvre.Locataire;
import com.app.repositories.administration.UtilisateurRepository;
import com.app.repositories.recouvre.CarnetRepository;
import com.app.repositories.recouvre.IdentificationRepository;
import com.app.repositories.recouvre.LocataireRepository;
import com.app.service.base.BaseService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CarnetService extends BaseService<Carnet>{	

    private final CarnetRepository repo;
    private final IdentificationRepository identificationRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Override
    public JpaRepository<Carnet, Integer> getRepository() {
        return repo;
    }

    public Optional<Carnet> findByActive() {
        return repo.findByActive();
    }

    public List<Carnet> findAllInactive() {
        return repo.findAllInactive();
    }

    public List<Carnet> findAll() {
        return repo.findAll();
    }

    public Optional<Carnet> findById(Integer id) {
        return repo.findById(id);
    }

    @Transactional
    public void activerCarnet(Integer id, String username) {

        // =========================
        // 🔹 1. Carnet sécurisé
        // =========================
        Carnet carnet = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carnet introuvable: " + id));

        if (carnet.getAgence() == null 
                || !carnet.getAgence().getId().equals(getCurrentAgence().getId())) {
            throw new SecurityException("Accès refusé (carnet)");
        }

        if (carnet.isCarActive()) {
            throw new IllegalStateException("Carnet déjà actif");
        }

        // =========================
        // 🔹 2. Utilisateur sécurisé
        // =========================
        Utilisateur utilisateur = utilisateurRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + username));

        if (utilisateur.getAgence() == null 
                || !utilisateur.getAgence().getId().equals(getCurrentAgence().getId())) {
            throw new SecurityException("Accès refusé (utilisateur)");
        }

        // =========================
        // 🔹 3. Validation bornes
        // =========================
        long debut = carnet.getCarNumDeb();
        long fin = carnet.getCarNumFin();

        if (debut > fin) {
            throw new IllegalArgumentException("Numéros carnet invalides");
        }

        // =========================
        // 🔹 4. Génération identifications
        // =========================
        List<Identification> identifications = new ArrayList<>();

        for (long i = debut; i <= fin; i++) {

            Identification ident = new Identification();
            ident.setIdeNumero(i);
            ident.setIdeEtat(true);
            ident.setCarnet(carnet);
            ident.setUtilisateur(utilisateur);
            ident.setAgence(getCurrentAgence());

            identifications.add(ident);
        }

        identificationRepository.saveAll(identifications);

        // =========================
        // 🔹 5. Activation carnet
        // =========================
        carnet.setCarActive(true);
        repo.save(carnet);
    }

    
   /* public void activerCarnet(Integer id, String username) {
        // 1. Récupération du carnet
        Carnet carnet = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Carnet introuvable avec ID: " + id));

        // 2. Récupération de l'utilisateur connecté
        Utilisateur utilisateur = utilisateurRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable: " + username));

        // 3. Création des identifications
        List<Identification> identifications = new ArrayList<>();
        long debut = carnet.getCarNumDeb();
        long fin = carnet.getCarNumFin();

        for (long i = debut; i <= fin; i++) {
            Identification ident = new Identification();
            ident.setIdeNumero(i);
            ident.setIdeEtat(true);
            ident.setCarnet(carnet);
            ident.setUtilisateur(utilisateur);
            ident.setAgence(getCurrentAgence());
            
            identifications.add(ident);            
        }

        // 4. Enregistrement des identifications
        identificationRepository.saveAll(identifications);

        // 5. Marquer le carnet comme actif
        carnet.setCarActive(true);
        repo.save(carnet);
    }*/
    
    public void delete(Integer id) {
    	repo.deleteById(id);
    }
    
    public void saveCarnet(CarnetDTO dto) {
        if (dto.getCarNumDeb() > dto.getCarNumFin()) {
            throw new IllegalArgumentException("Le numéro de début doit être inférieur ou égal numéro de fin");
        }

        List<Carnet> overlaps = repo.findOverlapping(dto.getCarNumDeb(), dto.getCarNumFin());
        if (!overlaps.isEmpty()) {
            throw new IllegalArgumentException("⚠️ Cette plage chevauche une plage déjà existante");
        }

        Carnet carnet = new Carnet();
        carnet.setCarNumDeb(dto.getCarNumDeb());
        carnet.setCarNumFin(dto.getCarNumFin());
        repo.save(carnet);
    }
    
    public boolean rangeOverlap(long deb, long fin) {
        return repo.existsByCarNumDebLessThanEqualAndCarNumFinGreaterThanEqual(fin, deb);
    }

    /*public void saveFromDto(CarnetDTO dto) {
        Carnet entity = new Carnet();
        if (dto.getId() != null) {
        	entity.setAgence(getCurrentAgence());
            entity = repo.findById(dto.getId()).orElse(entity);
        }
        entity.setCarNumDeb(dto.getCarNumDeb());
        entity.setCarNumFin(dto.getCarNumFin());
        entity.setUtilisateur(utilisateurRepository.findById(dto.getUtilisateurId()).orElse(null));

        repo.save(entity);
    }*/
    
    @Transactional
    public void saveFromDto(CarnetDTO dto) {

        boolean isNew = (dto.getId() == null);
        Carnet entity;

        if (isNew) {
            // ✅ création
            entity = new Carnet();
            entity.setAgence(getCurrentAgence());

        } else {
            // ✅ récupération existant
            entity = repo.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Carnet introuvable"));

            // 🔒 sécurité SaaS
            Integer agenceEntityId = entity.getAgence() != null ? entity.getAgence().getId() : null;
            Integer agenceCurrentId = getCurrentAgenceId();

            if (agenceEntityId != null && !agenceEntityId.equals(agenceCurrentId)) {
                throw new SecurityException("Accès refusé");
            }
        }

        // 🔄 mapping champs
        entity.setCarNumDeb(dto.getCarNumDeb());
        entity.setCarNumFin(dto.getCarNumFin());
        entity.setUtilisateur(
                utilisateurRepository.findById(dto.getUtilisateurId()).orElse(null)
        );

        repo.save(entity);
    }
    
    public List<Carnet> getCarnetsByUtilisateur(Utilisateur utilisateur) {
        return repo.findByUtilisateur(utilisateur);
    }

    public List<Carnet> getCarnetsActifsByUtilisateur(Utilisateur utilisateur) {
        return repo.findByUtilisateurAndCarActiveTrue(utilisateur);
    }
    
	/*
	 * public Page<Carnet> findByUtilisateur(Utilisateur utilisateur, Pageable
	 * pageable) { return repo.findByUtilisateur(utilisateur, pageable);
	 * }
	 */
    
    public Page<Carnet> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
    
    public Page<Carnet> findByUtilisateur(Utilisateur user, Pageable pageable) {
        boolean isAdmin = user.getAssignations().stream()
                              .map(Assignation::getRole)
                              .anyMatch(r -> "ADMIN".equalsIgnoreCase(r.getLibelle()));

        if (isAdmin) {
            return repo.findAll(pageable); // Admin voit tous
        } else {
            return repo.findByUtilisateur(user, pageable); // Les autres voient seulement les leurs
        }
    }
    
    private Agence getAgenceSafe() {
        return getCurrentAgence();
    }
}
