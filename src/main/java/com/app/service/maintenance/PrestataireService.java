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
import com.app.entities.administration.Agence;
import com.app.entities.maintenance.Prestataire;
import com.app.entities.recouvre.Bailleur;
import com.app.repositories.maintenance.PrestataireRepository;
import com.app.repositories.recouvre.BailleurRepository;
import com.app.service.base.BaseService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PrestataireService extends BaseService<Prestataire>{

	private final PrestataireRepository repo;
	
	@Transactional
    public Prestataire saveWithDocument(Prestataire prestataire) throws IOException {

        boolean isNew = (prestataire.getId() == null);
        Prestataire entity;
        
        Agence agence = getCurrentAgence();
        
        checkAgenceActive(agence);
        
        if (isNew) {
            entity = prestataire;
            entity.setAgence(agence);

        } else {
            entity = repo.findByIdAndAgenceId(prestataire.getId(), getCurrentAgenceId())
                    .orElseThrow(() -> new IllegalArgumentException("Prestataire introuvable"));

            Integer agenceEntityId = entity.getAgence() != null ? entity.getAgence().getId() : null;
            Integer agenceCurrentId = getCurrentAgenceId();

            if (agenceEntityId == null || agenceCurrentId == null
                    || !agenceEntityId.equals(agenceCurrentId)) {
                throw new SecurityException("Accès refusé");
            }

            // mapping champs
            entity.setNom(prestataire.getNom());
            entity.setTelephone(prestataire.getTelephone());
            entity.setAdresse(prestataire.getAdresse());
            entity.setEmail(prestataire.getEmail());
            entity.setEmail(prestataire.getEmail());
        }

        entity = repo.save(entity); // 🔥 garantit ID
      
        
        return entity;
    }

    public Optional<Prestataire> findByIdAgence(Integer id) {
        return repo.findByIdAndAgenceId(id, getCurrentAgenceId());
    }

    /*public Bailleur saved(Bailleur bailleur) {
        return repo.save(bailleur);
    }*/
      
    public Page<Prestataire> searchLocataire(String keyword, Pageable pageable) {
    	
    	Integer agenceId = getCurrentAgenceId();
    	
        if (keyword == null || keyword.trim().isEmpty()) {
            return repo.findByAgenceId(agenceId, pageable);
        }
        return repo.searchBailleur(keyword.trim(), agenceId, pageable);
    }
    
    public Page<PrestataireDTO> search(String keyword, Pageable pageable) {
    	
    	Integer agenceId = getCurrentAgenceId();
    	
        Page<Prestataire> page;

        if (keyword == null || keyword.trim().isEmpty()) {
            page = repo.findByAgenceId(agenceId, pageable);
        } else {
            page = repo.search(keyword.trim(), agenceId, pageable);
        }

        return page.map(this::toDTO);
    }

    private PrestataireDTO toDTO(Prestataire b) {
    	PrestataireDTO dto = new PrestataireDTO();

        dto.setId(b.getId());
        dto.setNom(b.getNom());
        dto.setAdresse(b.getAdresse());
        dto.setTelephone(b.getTelephone());

        if (b.getAgence() != null) {
            dto.setAgenceId(b.getAgence().getId());
        }

        return dto;
    }

	@Override
	public JpaRepository<Prestataire, Integer> getRepository() {
		// TODO Auto-generated method stub
		return repo;
	}
}
