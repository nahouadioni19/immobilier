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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.app.entities.administration.Agence;
import com.app.entities.administration.Banque;
import com.app.entities.recouvre.Bailleur;
import com.app.entities.recouvre.Locataire;
import com.app.repositories.recouvre.BailleurRepository;
import com.app.service.base.BaseService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BailleurService extends BaseService<Bailleur>{

	private final BailleurRepository repo;
	
	@Value("${app.storage.directory}")
    private String storageDirectory;
	
	@Override
    public JpaRepository<Bailleur, Integer> getRepository() {
        return repo;
    }

	public Page<Bailleur> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
    
    @Override
    public void afterUpdate(Bailleur entity) {
        super.afterUpdate(entity);
       // clearCache(TYPE_ROLE_CACHE); 
    }

    @Override
    public void afterSave(Bailleur entity) {
        super.afterSave(entity);
      //  clearCache(TYPE_ROLE_CACHE); 
    }
        
    
    @Transactional
    public Bailleur saveWithDocument(Bailleur bailleur,
                                     MultipartFile carteIdentite,
                                     MultipartFile factureCie) throws IOException {

        boolean isNew = (bailleur.getId() == null);
        Bailleur entity;
        
        Agence agence = getCurrentAgence();
        
        checkAgenceActive(agence);
        
        if (isNew) {
            entity = bailleur;
            entity.setAgence(agence);

        } else {
            entity = repo.findById(bailleur.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Bailleur introuvable"));

            Integer agenceEntityId = entity.getAgence() != null ? entity.getAgence().getId() : null;
            Integer agenceCurrentId = getCurrentAgenceId();

            if (agenceEntityId == null || agenceCurrentId == null
                    || !agenceEntityId.equals(agenceCurrentId)) {
                throw new SecurityException("Accès refusé");
            }

            // mapping champs
            entity.setNom(bailleur.getNom());
            entity.setPrenom(bailleur.getPrenom());
            entity.setCellulaire(bailleur.getCellulaire());
            entity.setTelephone(bailleur.getTelephone());
            entity.setAdresse(bailleur.getAdresse());
            entity.setEmail(bailleur.getEmail());
            entity.setDateNaissance(bailleur.getDateNaissance());
            entity.setLieuNaissance(bailleur.getLieuNaissance());
            entity.setNomContact(bailleur.getNomContact());
            entity.setPrenomContact(bailleur.getPrenomContact());
            entity.setTelContact(bailleur.getTelContact());
            entity.setEmailContact(bailleur.getEmailContact());
            entity.setSexe(bailleur.getSexe());
        }

        entity = repo.save(entity); // 🔥 garantit ID

        String uploadDir = storageDirectory + File.separator + "bailleurs" + File.separator + entity.getId();
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        List<String> allowedTypes = Arrays.asList("image/jpeg", "image/png", "application/pdf");
        long maxSize = 5 * 1024 * 1024;

        if (carteIdentite != null && !carteIdentite.isEmpty()) {
            if (!allowedTypes.contains(carteIdentite.getContentType()))
                throw new IllegalArgumentException("Format invalide carte identité");

            if (carteIdentite.getSize() > maxSize)
                throw new IllegalArgumentException("Fichier trop lourd");

            if (entity.getCarteIdentitePath() != null)
                Files.deleteIfExists(Paths.get(entity.getCarteIdentitePath()));

            String fileName = "ci_" + System.currentTimeMillis() + "_" + carteIdentite.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(carteIdentite.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            entity.setCarteIdentitePath(filePath.toString());
        }

        if (factureCie != null && !factureCie.isEmpty()) {
            if (!allowedTypes.contains(factureCie.getContentType()))
                throw new IllegalArgumentException("Format invalide facture");

            if (factureCie.getSize() > maxSize)
                throw new IllegalArgumentException("Fichier trop lourd");

            if (entity.getFactureCiePath() != null)
                Files.deleteIfExists(Paths.get(entity.getFactureCiePath()));

            String fileName = "facture_" + System.currentTimeMillis() + "_" + factureCie.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(factureCie.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            entity.setFactureCiePath(filePath.toString());
        }

      //  return repo.save(entity);
        
        return entity;
    }


    private String handleFileUpload(MultipartFile file, String oldPath, String prefix, Path uploadPath) throws IOException {
        if (file == null || file.isEmpty()) return oldPath;

        // Vérification
        List<String> allowedTypes = Arrays.asList("image/jpeg","image/png","application/pdf");
        if (!allowedTypes.contains(file.getContentType())) {
            throw new IllegalArgumentException("Format non autorisé pour " + prefix);
        }
        if (file.getSize() > 5*1024*1024) throw new IllegalArgumentException(prefix + " trop volumineux");

        // Supprimer ancien fichier
        if (oldPath != null) Files.deleteIfExists(Paths.get(oldPath));

        // Copier le nouveau
        String fileName = prefix + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }


    public Optional<Bailleur> findById(int id) {
        return repo.findById(id);
    }

    public Bailleur saved(Bailleur bailleur) {
        return repo.save(bailleur);
    }
    
    public Page<Bailleur> search(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repo.findAll(pageable);
        }
        return repo.search(keyword.trim(), pageable);
    }
    
    public Page<Bailleur> searchLocataire(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repo.findAll(pageable);
        }
        return repo.searchBailleur(keyword.trim(), pageable);
    }
}
