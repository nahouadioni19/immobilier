package com.app.service.administration;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entities.administration.Assignation;
import com.app.entities.administration.Utilisateur;
import com.app.repositories.administration.AssignationRepository;
import com.app.service.base.BaseService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AssignationService extends BaseService<Assignation>{

    private final AssignationRepository repo;

    @Override 
    public JpaRepository<Assignation, Integer> getRepository() { 
    	return repo; 
    }
    
    public Optional<Assignation> getAssignationCourante(Utilisateur utilisateur) {
        return repo.findFirstByUtilisateurAndCourantTrue(utilisateur);
    }

    public boolean hasRole(Utilisateur utilisateur, String roleCode) {
        return getAssignationCourante(utilisateur)
                .map(assign -> roleCode.equalsIgnoreCase(assign.getRole().getCode()))
                .orElse(false);
    }
}