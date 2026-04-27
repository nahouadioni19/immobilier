package com.app.service.administration;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entities.administration.Assignation;
import com.app.entities.administration.Utilisateur;
import com.app.repositories.administration.AssignationRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AssignationService {

    private final AssignationRepository repo;

  
    public Optional<Assignation> getAssignationCourante(Utilisateur utilisateur) {
        return repo.findFirstByUtilisateurAndCourantTrue(utilisateur);
    }

    public boolean hasRole(Utilisateur utilisateur, String roleCode) {
        return getAssignationCourante(utilisateur)
                .map(assign -> roleCode.equalsIgnoreCase(assign.getRole().getCode()))
                .orElse(false);
    }
}