package com.app.service.maintenance;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.app.dto.BailleurDTO;
import com.app.dto.PrestataireDTO;
import com.app.dto.TypeInterventionDTO;
import com.app.entities.administration.Agence;
import com.app.entities.maintenance.Prestataire;
import com.app.entities.maintenance.TypeIntervention;
import com.app.entities.recouvre.Bailleur;
import com.app.repositories.maintenance.PrestataireRepository;
import com.app.repositories.maintenance.TypeInterventionRepository;
import com.app.repositories.recouvre.BailleurRepository;
import com.app.service.base.BaseService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TypeInterventionService extends BaseService<TypeIntervention>{

	private final TypeInterventionRepository repo;
	
	@Transactional
    public TypeIntervention saveIntervention(TypeIntervention typeIntervention) throws IOException {

        boolean isNew = (typeIntervention.getId() == null);
        TypeIntervention entity;
        
        Agence agence = getCurrentAgence();
        
        checkAgenceActive(agence);
        
        if (isNew) {
            entity = typeIntervention;
          //  entity.setAgence(agence);

        } else {
            /*entity = repo.findByIdAndAgenceId(typeIntervention.getId(), getCurrentAgenceId())
                    .orElseThrow(() -> new IllegalArgumentException("Prestataire introuvable"));

            Integer agenceEntityId = entity.getAgence() != null ? entity.getAgence().getId() : null;
            Integer agenceCurrentId = getCurrentAgenceId();

            if (agenceEntityId == null || agenceCurrentId == null
                    || !agenceEntityId.equals(agenceCurrentId)) {
                throw new SecurityException("Accès refusé");
            }*/

        	entity = repo.findById(typeIntervention.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Type intervention introuvable"));
            // mapping champs
            entity.setLibelle(typeIntervention.getLibelle());
        }

        entity = repo.save(entity); // 🔥 garantit ID
      
        
        return entity;
    }

    /*public Optional<Prestataire> findByIdAgence(Integer id) {
        return repo.findByIdAndAgenceId(id, getCurrentAgenceId());
    }*/

      
    public Page<TypeIntervention> searchLocataire(String keyword, Pageable pageable) {
    	
    	Integer agenceId = getCurrentAgenceId();
    	
        if (keyword == null || keyword.trim().isEmpty()) {
           // return repo.findByAgenceId(agenceId, pageable);
            return repo.findAllByOrderByLibelleAsc(pageable);
        }
        //return repo.searchLibelle(keyword.trim(), agenceId, pageable);
        return repo.search(keyword.trim(), pageable);
    }
    
    public Page<TypeInterventionDTO> search(String keyword, Pageable pageable) {
    	
    	Integer agenceId = getCurrentAgenceId();
    	
        Page<TypeIntervention> page;

        if (keyword == null || keyword.trim().isEmpty()) {
           // page = repo.findByAgenceId(agenceId, pageable);
            page = repo.findAllByOrderByLibelleAsc(pageable);
        } else {
            //page = repo.search(keyword.trim(), agenceId, pageable);
            page = repo.search(keyword.trim(), pageable);
        }

        return page.map(this::toDTO);
    }

    private TypeInterventionDTO toDTO(TypeIntervention b) {
    	TypeInterventionDTO dto = new TypeInterventionDTO();

        dto.setId(b.getId());
        dto.setLibelle(b.getLibelle());

        /*if (b.getAgence() != null) {
            dto.setAgenceId(b.getAgence().getId());
        }*/

        return dto;
    }

	@Override
	public JpaRepository<TypeIntervention, Integer> getRepository() {
		// TODO Auto-generated method stub
		return repo;
	}
}
