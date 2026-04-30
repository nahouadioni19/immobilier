package com.app.service.recouvre;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.dto.IdentificationProjection;
import com.app.entities.recouvre.Identification;
import com.app.repositories.recouvre.IdentificationRepository;
import com.app.security.UserPrincipal;

@Service
public class IdentificationService {
	
	private final IdentificationRepository repo;
	private final PasswordEncoder passwordEncoder;
	
	public IdentificationService(IdentificationRepository repo, PasswordEncoder passwordEncoder) {
		this.repo = repo;
		this.passwordEncoder = passwordEncoder;
	}
	
    public List<Identification> findAll() {
        return repo.findAll();
    }
    
    public void saveAll(List<Identification> identifications) {
        repo.saveAll(identifications);
    }
    
    public List<IdentificationProjection> findIdentificationsNative(UserPrincipal principal, Integer encaisseId, Long agentId, Integer agenceId) {
    	boolean isAgentRecouv = principal.getAuthorities().stream()
    			.peek(auth -> System.out.println("Role détecté: " + auth.getAuthority()))
    	        .anyMatch(auth -> "ROLE_RECOUV".equals(auth.getAuthority()));
    	
		if (isAgentRecouv) {
			return repo.findAvailableIdentifications(principal.getId(), encaisseId, agenceId);
			
		} 
			// Admin → tous ou filtré
	        if (agentId != null && agentId <= 0) {
	            agentId = null;
	        }
			return repo.findBailDetailsByAdmin(agenceId,agentId,encaisseId); // Admin voit tous
				
    }
    
    public List<IdentificationProjection> findAvailableIdentificationsByUtilisateur(UserPrincipal principal, Integer agenceId) {
    	return repo.findAvailableIdentificationsByUtilisateur(principal.getId(), agenceId);
    }
}
