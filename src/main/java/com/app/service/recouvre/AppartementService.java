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
        return repo.findAllAvailable();
    }

    // EDIT
    public List<Appartement> getAvailableForEdit(Integer currentId) {
        return repo.findAllAvailableOrCurrent(currentId);
    }
    
    public List<Appartement> getAppartementsLibres() {
        return repo.findByStatut(StatutAppartement.LIBRE);
    }
    
   /* public Page<Appartement> search(String term, Pageable pageable) {
        return repo.search(term, pageable);
    }*/
    
    public Page<Appartement> searchByLibelleOrImmeuble(String term, Pageable pageable) {
        return repo.search(term, pageable);
    }
    
    public Page<Appartement> searchForAppartement(String term, Integer currentId, Pageable pageable) {
        return repo.searchForAppartement(term, currentId, pageable);
    }

}


/*package com.app.service.recouvre;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entities.recouvre.Appartement;
import com.app.repositories.recouvre.AppartementRepository;
import com.app.service.base.BaseService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AppartementService  extends BaseService<Appartement>{

	private final AppartementRepository repo;
	
	
	public AppartementService(AppartementRepository repo) {
        this.repo = repo;
    }

    public List<Appartement> getAvailableForCreate() {
        return repo.findAllAvailable();
    }

    public List<Appartement> getAvailableForEdit(Integer currentId) {
        return repo.findAllAvailableOrCurrent(currentId);
    }
	
}*/