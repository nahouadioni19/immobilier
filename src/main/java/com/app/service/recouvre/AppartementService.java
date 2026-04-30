package com.app.service.recouvre;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.app.entities.recouvre.Appartement;
import com.app.entities.recouvre.Locataire;
import com.app.enums.StatutAppartement;
import com.app.repositories.recouvre.AppartementRepository;
import com.app.service.base.BaseService;

@Service
public class AppartementService extends BaseService<Appartement> {

    private final AppartementRepository repo;

    public AppartementService(AppartementRepository repo) {
        this.repo = repo;
    }

    @Override
    public JpaRepository<Appartement, Integer> getRepository() {
        return repo;
    }

    // CREATE
    public List<Appartement> getAvailableForCreate() {
    	
    	Integer agenceId = getCurrentAgenceId();
    	
        return repo.findAllAvailable(agenceId);
    }

    // EDIT
    public List<Appartement> getAvailableForEdit(Integer currentId) {
    	
    	Integer agenceId = getCurrentAgenceId();
    	
        return repo.findAllAvailableOrCurrent(currentId, agenceId);
    }
    
    public List<Appartement> getAppartementsLibres() {
    	
    	Integer agenceId = getCurrentAgenceId();
    	
        return repo.findByStatut(StatutAppartement.LIBRE);
    }
       
    public Page<Appartement> searchByLibelleOrImmeuble(String term, Pageable pageable) {
    	
    	Integer agenceId = getCurrentAgenceId();
    	
        return repo.search(term, agenceId, pageable);
    }
    
    public Page<Appartement> searchForAppartement(String term, Integer currentId, Pageable pageable) {
    	
    	Integer agenceId = getCurrentAgenceId();
    	
        return repo.searchForAppartement(term, currentId, agenceId, pageable);
    }

}
