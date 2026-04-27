package com.app.service.administration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entities.administration.Assignation;
import com.app.entities.administration.Utilisateur;
import com.app.repositories.administration.UserRepository;
import com.app.repositories.administration.UtilisateurRepository;
import com.app.service.base.BaseService;
import com.app.utils.JUtils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UtilisateurService extends BaseService<Utilisateur> {

    private final UtilisateurRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    
    private static final String PREFIX_USER = "OPE";
    private static final String DEFAULT_PASSWORD = "O"; // Mot de passe standard

    // -----------------------
    //  Recherches 
    // -----------------------
    public Optional<Utilisateur> findByUsernameIgnoreCase(String username) {
        return repo.findByUsernameIgnoreCase(username);
    }

    public Optional<Utilisateur> findByUsernameIgnoreCaseWithRoles(String username) {
        return repo.findByUsernameIgnoreCaseWithRoles(username);
    }
    
   // public Optional<Utilisateur> findByUsername(String username) {
     //   return repo.findByUsername(username);
     //   return repo.findByUserAdmin();
    //}
    
    public Optional<Utilisateur> findByMatricule(String matricule) {
        return repo.findByMatriculeIgnoreCase(matricule);
    }

    @Override
    public Optional<Utilisateur> findByCode(String username) {
        return findByUsernameIgnoreCase(username);
    }

    /**
     * ⚡ Charge un utilisateur avec ses assignations et rôles
     */    
    @Transactional(readOnly = true)
    public Optional<Utilisateur> findByIdWithAssignations(int id) {
        return repo.findUtilisateurWithAssignationsById(id);
    }


    // -----------------------
    //  Avant sauvegarde / update
    // -----------------------
    @Override
    public void beforeSave(Utilisateur utilisateur) {
        super.beforeSave(utilisateur);
        normalizeUtilisateur(utilisateur);

        // Si le mot de passe est vide ou "O", on met le mot de passe standard
        if (StringUtils.isBlank(utilisateur.getPassword()) || "O".equals(utilisateur.getPassword())) {
            utilisateur.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        }

        // Génération de matricule si absent
        if (StringUtils.isBlank(utilisateur.getMatricule())) {
            String newMatricule = StringUtils.leftPad(
                    String.valueOf(repo.findNextSequenceCode(PREFIX_USER)), 5, "0");
            utilisateur.setMatricule(PREFIX_USER + newMatricule);
        }
    }

    @Override
    public void beforeUpdate(Utilisateur utilisateur) {
        super.beforeUpdate(utilisateur);
        normalizeUtilisateur(utilisateur);

        // Toujours vérifier mot de passe
        if ("O".equals(utilisateur.getPassword())) {
            utilisateur.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        }
    }

    private void normalizeUtilisateur(Utilisateur utilisateur) {
        utilisateur.setNom(utilisateur.getNom().toUpperCase());
        utilisateur.setPrenoms(StringUtils.capitalize(utilisateur.getPrenoms()));
        if (utilisateur.getTelephone() != null) {
            utilisateur.setTelephone(utilisateur.getTelephone().trim().replace(" ", ""));
        }
    }

    // -----------------------
    //  Gestion des assignations
    // -----------------------
    public void saveAll(Utilisateur utilisateur) {
        if (utilisateur.getAssignations() == null) {
            utilisateur.setAssignations(new ArrayList<>());
        }

        List<Assignation> validAssignations = utilisateur.getAssignations().stream()
                .filter(a -> a.getRole() != null && a.getRole().getId() > 0 && a.getRole().getId() > 0)
                .peek(a -> {
                    a.setUtilisateur(utilisateur);
                    if (a.getDateDebut() == null) a.setDateDebut(LocalDate.now());
                    if (a.getDateFin() == null) a.setDateFin(JUtils.stringToDate("31/12/9999"));
                })
                .collect(Collectors.toList());

        utilisateur.setAssignations(validAssignations);

        save(utilisateur);
    }

    // -----------------------
    //  CRUD
    // -----------------------
    @Override
    public JpaRepository<Utilisateur, Integer> getRepository() {
        return repo;
    }

    /*@Transactional
    @Override
    public void save(Utilisateur user) {
    	user.setAgence(getCurrentAgence());
        repo.save(user);
    }*/
    
    @Transactional
    @Override
    public void save(Utilisateur user) {

        boolean isNew = (user.getId() == null);

        if (isNew) {
            // création → on affecte l’agence de l’utilisateur connecté
            user.setAgence(getCurrentAgence());

        } else {
            // modification → on récupère l’existant
            Utilisateur entity = repo.findById(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

            // 🔒 sécurité SaaS
            Integer agenceEntityId = entity.getAgence() != null ? entity.getAgence().getId() : null;
            Integer agenceCurrentId = getCurrentAgenceId();

            if (agenceEntityId != null && !agenceEntityId.equals(agenceCurrentId)) {
                throw new SecurityException("Accès refusé");
            }

            // 🔄 mapping champs modifiables
            entity.setNom(user.getNom());
            entity.setPrenoms(user.getPrenoms());
            entity.setUsername(user.getUsername());
            entity.setEmail(user.getEmail());
            entity.setEnabled(user.isEnabled());
            // ajoute les autres champs utiles

            repo.save(entity);
            return;
        }
      
        repo.save(user);
    }

    public boolean isConformPassword(Utilisateur utilisateur, String password) {
        return passwordEncoder.matches(password, utilisateur.getPassword());
    }

    public Page<Utilisateur> filterAll(int page, int size, String sortField, String sortDirection, String terms) {
        Sort sort = getFilterSort(sortField, sortDirection);
        return repo.filterAll(terms, getPageable(page, size, sort));
    }

    public Page<Utilisateur> filterAllWithAllRole(int page, int size, String sortField, String sortDirection,
            String terms) {
        Sort sort = getFilterSort(sortField, sortDirection);
        return repo.filterAllWithAllRole(terms, getPageable(page, size, sort));
    }

    public long count() {
        return repo.count();
    }

    public List<Utilisateur> findByTerm(String terms) {
        return repo.findByTerm(terms);
    }

    public Page<Utilisateur> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
    
    public List<Utilisateur> findByAgentRecouvrement(String code) {    	
        return repo.findByUtilisateurRecouvrement(code);
    }
    
    @Transactional
    public boolean updatePassword(String username, String currentPassword, String newPassword) {
        Utilisateur user = userRepository.findByUsername(username);
        if (user == null) {
            return false; // utilisateur introuvable
        }

        // Vérifier que le mot de passe actuel est correct
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false; // mot de passe actuel incorrect
        }

        // Mettre à jour le mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        repo.save(user);
        return true;
    }
    
    
    
    public List<Utilisateur> findByUsername(String username) {
        if (username == null) {
            return repo.findAll(); // Admin
        } else {
            return repo.findByUsername(username)
                       .map(List::of)
                       .orElse(List.of()); // retourne une liste avec 0 ou 1 utilisateur
        }
    }
    
    public boolean reinitialiserMotDePasse(Integer userId, String defaultPassword) {
        return repo.findById(userId).map(user -> {
            user.setPassword(passwordEncoder.encode(defaultPassword));
            repo.save(user);
            return true;
        }).orElse(false);
    }
    
    @Transactional
    public void desactiverCompte(Integer id) {

        Utilisateur user = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

     // Protection admin principal
        if ("admin".equals(user.getUsername())) {
            throw new RuntimeException("Impossible de désactiver l'admin principal");
        }
        
        //user.setResetPwd(false);
        user.setEnabled(false);
        
        repo.save(user); // ✅ important
    }


    @Transactional
    public void activerCompte(Integer id) {

        Utilisateur user = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        
        user.setEnabled(true);
        
        repo.save(user); // ✅ important
    }

    //@Transactional(readOnly = true)
    public Utilisateur getUserWithRoles(String username) {

        return repo
                .findUserWithRoles(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

    }
    
}
