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

import com.app.dto.InterventionDTO;
import com.app.entities.administration.Agence;
import com.app.entities.maintenance.Intervention;
import com.app.entities.recouvre.Bailleur;
import com.app.form.InterventionForm;
import com.app.mapper.InterventionMapper;
import com.app.repositories.maintenance.InterventionRepository;
import com.app.repositories.maintenance.PrestataireRepository;
import com.app.repositories.maintenance.TypeinterventionRepository;
import com.app.repositories.recouvre.AppartementRepository;
import com.app.repositories.recouvre.BailleurRepository;
import com.app.repositories.recouvre.LocataireRepository;
import com.app.service.base.BaseService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class InterventionService extends BaseService<Intervention>{

	private final InterventionRepository repository;
	private final AppartementRepository appartementRepository;
	private final LocataireRepository locataireRepository;
	private final PrestataireRepository prestataireRepository;
	private final TypeinterventionRepository typeinterventionRepository;
	
	private final InterventionMapper mapper;
	
	
	@Transactional
	public Intervention saveIntervention(InterventionForm form) {

	    Agence agence = getCurrentAgence();
	    checkAgenceActive(agence);

	    final boolean isNew = (form.getId() == null);

	    Intervention entity;

	    if (isNew) {

	        entity = new Intervention();
	        entity.setAgence(agence);

	    } else {

	        entity = repository.findByIdAndAgenceId(form.getId(), agence.getId())
	                .orElseThrow(() ->
	                        new IllegalArgumentException("Intervention introuvable"));

	        Integer agenceEntityId = entity.getAgence() != null
	                ? entity.getAgence().getId()
	                : null;

	        if (!agence.getId().equals(agenceEntityId)) {
	            throw new SecurityException("Accès refusé");
	        }
	    }

	    // Mapping des champs simples
	    mapper.updateEntity(form, entity);

	    // Relations
	    entity.setAppartement(
	            appartementRepository.findById(form.getAppartementId())
	                    .orElseThrow(() ->
	                            new IllegalArgumentException("Appartement introuvable"))
	    );

	    entity.setLocataire(
	            form.getLocataireId() == null
	                    ? null
	                    : locataireRepository.findById(form.getLocataireId())
	                    .orElseThrow(() ->
	                            new IllegalArgumentException("Locataire introuvable"))
	    );

	    entity.setPrestataire(
	            form.getPrestataireId() == null
	                    ? null
	                    : prestataireRepository.findById(form.getPrestataireId())
	                    .orElseThrow(() ->
	                            new IllegalArgumentException("Prestataire introuvable"))
	    );

	    entity.setTypeIntervention(
	            typeinterventionRepository.findById(form.getTypeInterventionId())
	                    .orElseThrow(() ->
	                            new IllegalArgumentException("Type d'intervention introuvable"))
	    );

	    return repository.save(entity);
	}
	
	
    public Optional<Intervention> findByIdAgence(Integer id) {
        return repository.findByIdAndAgenceId(id, getCurrentAgenceId());
    }

    /*public Bailleur saved(Bailleur bailleur) {
        return repository.save(bailleur);
    }*/
      
    /*public Page<Intervention> searchLocataire(String keyword, Pageable pageable) {
    	
    	Integer agenceId = getCurrentAgenceId();
    	
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findByAgenceId(agenceId, pageable);
        }
        return repository.searchBailleur(keyword.trim(), agenceId, pageable);
    }*/
    
    public Page<InterventionDTO> search(String keyword, Pageable pageable) {

        Integer agenceId = getCurrentAgenceId();

        Page<Intervention> page;

        if (keyword == null || keyword.trim().isEmpty()) {
            page = repository.findByAgenceId(agenceId, pageable);
        } else {
            page = repository.search(keyword.trim(), agenceId, pageable);
        }

        return page.map(mapper::toDTO);
    }

    private InterventionDTO toDTO(Intervention entity) {

        if (entity == null) {
            return null;
        }

        InterventionDTO dto = new InterventionDTO();

        dto.setId(entity.getId());

        dto.setObjet(entity.getObjet());
        dto.setDescription(entity.getDescription());

        dto.setDateDeclaration(entity.getDateDeclaration());
        dto.setDatePrevue(entity.getDatePrevue());
        dto.setDateDebut(entity.getDateDebut());
        dto.setDateFin(entity.getDateFin());

        dto.setStatut(entity.getStatut());

        dto.setCoutEstime(entity.getCoutEstime());
        dto.setCoutReel(entity.getCoutReel());

        dto.setObservation(entity.getObservation());

        dto.setUrgente(entity.getUrgente());

        if (entity.getAgence() != null) {
            dto.setAgenceId(entity.getAgence().getId());
            dto.setAgenceNom(entity.getAgence().getNom());
        }

        if (entity.getAppartement() != null) {
            dto.setAppartementId(entity.getAppartement().getId());
            dto.setAppartementLibelle(entity.getAppartement().getLibelle());
        }

        if (entity.getLocataire() != null) {
            dto.setLocataireId(entity.getLocataire().getId());
            dto.setLocataireNomComplet(entity.getLocataire().getNom());
        }

        if (entity.getPrestataire() != null) {
            dto.setPrestataireId(entity.getPrestataire().getId());
            dto.setPrestataireNom(entity.getPrestataire().getNom());
        }

        if (entity.getTypeIntervention() != null) {
            dto.setTypeInterventionId(entity.getTypeIntervention().getId());
            dto.setTypeInterventionLibelle(entity.getTypeIntervention().getLibelle());
        }

        return dto;
    }

	@Override
	public JpaRepository<Intervention, Integer> getRepository() {
		// TODO Auto-generated method stub
		return repository;
	}
}
