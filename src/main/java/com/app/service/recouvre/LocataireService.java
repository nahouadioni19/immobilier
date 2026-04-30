package com.app.service.recouvre;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PathVariable;

import com.app.entities.recouvre.Bailleur;
import com.app.entities.recouvre.Locataire;
import com.app.repositories.recouvre.LocataireRepository;
import com.app.service.base.BaseService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LocataireService extends BaseService<Locataire> {
	
	private final LocataireRepository repo;
	
	@Value("${app.storage.directory}")
    private String storageDirectory;
	
	@Override
    public JpaRepository<Locataire, Integer> getRepository() {
        return repo;
    }

	public Page<Locataire> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
    
    @Override
    public void afterUpdate(Locataire entity) {
        super.afterUpdate(entity);
    }

    @Override
    public void afterSave(Locataire entity) {
        super.afterSave(entity);
      //  clearCache(TYPE_ROLE_CACHE); 
    }
    
    @Transactional
    public Locataire saveWithDocument(Locataire locataire, MultipartFile documentIdentite) throws IOException {
        boolean isNew = (locataire.getId() == null);
        Locataire entity;

        if (isNew) {
            // Nouveau bailleur
            entity = locataire;
            entity.setAgence(getCurrentAgence());
          //  entity = repo.save(entity); // Sauvegarde initiale pour générer l'ID
        } else {
        	// Modification
        	entity = repo.findById(locataire.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Locataire introuvable : " + locataire.getId()));

            Integer agenceEntityId = entity.getAgence() != null ? entity.getAgence().getId() : null;
            Integer agenceCurrentId = getCurrentAgenceId();

            if (agenceEntityId == null || agenceCurrentId == null
                    || !agenceEntityId.equals(agenceCurrentId)) {
                throw new SecurityException("Accès refusé");
            }
            // 🔹 MAJ des champs modifiables uniquement
            entity.setNom(locataire.getNom());
            entity.setPrenom(locataire.getPrenom());
            entity.setPerschg(locataire.getPerschg());
            entity.setTelephone(locataire.getTelephone());
            entity.setEmail(locataire.getEmail());
            entity.setProfession(locataire.getProfession());
            entity.setType(locataire.getType());
            entity.setNomContact(locataire.getNomContact());
            entity.setPrenomContact(locataire.getPrenomContact());
            entity.setTelContact(locataire.getTelContact());
            entity.setEmailContact(locataire.getEmailContact());
            entity.setSexe(locataire.getSexe());
        }
        
        entity = repo.save(entity);

        // 🔹 Gestion fichiers (même code que tu avais)
        String uploadDir = storageDirectory + File.separator + "locataires" + File.separator + entity.getId();
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        List<String> allowedTypes = Arrays.asList("image/jpeg", "image/png", "application/pdf");
        long maxSize = 5 * 1024 * 1024;

        // Carte identité
        if (documentIdentite != null && !documentIdentite.isEmpty()) {
            if (!allowedTypes.contains(documentIdentite.getContentType()))
                throw new IllegalArgumentException("Format non autorisé pour le document d'identité");
            if (documentIdentite.getSize() > maxSize)
                throw new IllegalArgumentException("Document d'identité trop volumineuse");

            if (!isNew && entity.getDocumentPath() != null)
                Files.deleteIfExists(Paths.get(entity.getDocumentPath()));

            String fileName = "document_identite_" + System.currentTimeMillis() + "_" + documentIdentite.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(documentIdentite.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            entity.setDocumentPath(filePath.toString());
        }

        // 🔹 Sauvegarde finale
        return repo.save(entity);
    }    

    public Optional<Locataire> findById(int id) {
        return repo.findById(id);
    }

    public Locataire saved(Locataire locataire) {
        return repo.save(locataire);
    }
        
    public List<Locataire> search(String term) {
    	
    	Integer agenceId = getCurrentAgenceId();
    	
        Page<Locataire> page = repo.search(term, agenceId, PageRequest.of(0, 50));
        
        return page.getContent();
    }

    public Page<Locataire> search(String term, Pageable pageable) {
    	
    	Integer agenceId = getCurrentAgenceId();
    	
        return repo.search(term, agenceId, pageable);
    }

    public Page<Locataire> searchLocataire(String keyword, Pageable pageable) {
        
    	Integer agenceId = getCurrentAgenceId();
    	
    	if (keyword == null || keyword.trim().isEmpty()) {
            return repo.findLocataireByAgenceId(agenceId, pageable);
        }
    	
        return repo.searchLocataire(keyword.trim(), agenceId, pageable);
    }
        
}
