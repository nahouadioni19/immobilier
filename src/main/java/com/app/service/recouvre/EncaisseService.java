package com.app.service.recouvre;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.app.dto.EncaisseForm;
import com.app.entities.administration.Agence;
import com.app.entities.administration.Utilisateur;
import com.app.entities.recouvre.Bail;
import com.app.entities.recouvre.Encaisse;
import com.app.entities.recouvre.Identification;
import com.app.entities.recouvre.Loyann;
import com.app.repositories.EncaisseListDto;
import com.app.repositories.administration.UtilisateurRepository;
import com.app.repositories.recouvre.BailRepository;
import com.app.repositories.recouvre.EncaisseRepository;
import com.app.repositories.recouvre.IdentificationRepository;
import com.app.security.UserPrincipal;
import com.app.service.base.BaseService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EncaisseService extends BaseService<Encaisse>{
	
	@Autowired
	private final EncaisseRepository repo;
	private final BailRepository bailRepository;
	private final IdentificationRepository identificationRepository;
	private final UtilisateurRepository utilisateurRepository;
	private final LoyannService loyannService;
	
	@Override
    public JpaRepository<Encaisse, Integer> getRepository() {
        return repo;
    }
	
	public List<Encaisse> findAll() {
        return repo.findAll();
    }
		
	public void deleteById(Integer id) {
		repo.deleteById(id);
    }
	
	
	public List<Loyann> repartirLoyer(Bail bail, long montantPayé, long loyerMensuel, LocalDate dateDebut) {
	    List<Loyann> loyers = new ArrayList<>();
	    
	    long reste = montantPayé;
	    LocalDate current = dateDebut;

	    while (reste > 0) {
	        Loyann l = new Loyann();
	        l.setBail(bail);
	        l.setMois(current.getMonthValue());
	        l.setAnnee(current.getYear());
	        
	        if (reste >= loyerMensuel) {
	            l.setLoyer(loyerMensuel);
	            reste -= loyerMensuel;
	        } else {
	            l.setLoyer(reste);
	            reste = 0;
	        }
	        
	        loyers.add(l);
	        current = current.plusMonths(1); // passer au mois suivant
	    }

	    return loyers;
	}	
	
	public Page<EncaisseListDto> findByUtilisateur(UserPrincipal principal, Long agentId, String keyword, Pageable pageable) {
	    boolean isDirec = principal.getAuthorities().stream()
	        .anyMatch(auth -> "ROLE_DIREC".equals(auth.getAuthority()));	    // ROLE_RECOUV
	    
	    if (isDirec) {
	    	
	    	if (keyword != null && keyword.trim().isEmpty()) {
	            keyword = null;
	        }

	        if (agentId != null && agentId <= 0) {
	            agentId = null;
	        }

		    return repo.findEncaissePageByAdmin(agentId, keyword, pageable);	    	
	    	
	    } else {
	    	
	    	if (keyword != null && keyword.trim().isEmpty()) {
	            keyword = null;
	        }
	    	
	    	return repo.findEncaissePageByUtilisateur(principal.getUsername(), keyword, pageable);
	    }

	}
	
	public Page<EncaisseListDto> findWithFilters(
	        UserPrincipal principal,
	        Long agentId,
	        String keyword,
	        Pageable pageable) {

	    if (keyword != null && keyword.trim().isEmpty()) {
	        keyword = null;
	    }

	    return repo.findWithFilters(agentId, keyword, pageable);
	}

	
	private Long defaultLong(Long value) {
	    return value != null ? value : 0L;
	}
	
	@Transactional
	public Encaisse saveEncaissement(EncaisseForm form) {

	    Agence currentAgence = getCurrentAgence();

	    Encaisse entity;

	    boolean isNew = (form.getId() == null);

	    if (isNew) {
	        entity = new Encaisse();
	        entity.setAgence(currentAgence);
	    } else {
	        entity = repo.findById(form.getId())
	                .orElseThrow(() -> new IllegalArgumentException("Encaisse introuvable"));

	        if (entity.getAgence() == null 
	                || !entity.getAgence().getId().equals(currentAgence.getId())) {
	            throw new SecurityException("Accès refusé");
	        }
	    }
	    
	    Integer bailId = form.getBailId();
	    Integer identId = form.getIdentificationId();

	    // =========================
	    // 🔗 Bail sécurisé
	    if (bailId != null) {

	        Bail bail = bailRepository.findById(bailId)
	                .orElseThrow(() -> new IllegalArgumentException("Bail introuvable"));

	        checkAgence(bail.getAgence(), currentAgence.getId(), "Bail d’une autre agence");

	        entity.setBail(bail);

	    } else {
	        entity.setBail(null);
	    }

	    // =========================
	    // 🔗 Identification sécurisé
	    // =========================
	    if (identId != null) {

	        Identification ident = identificationRepository.findById(identId)
	                .orElseThrow(() -> new IllegalArgumentException("Identification introuvable"));

	        checkAgence(ident.getAgence(), currentAgence.getId(), "Identification d’une autre agence");

	        entity.setIdentification(ident);

	    } else {
	        entity.setIdentification(null);
	    }

	    

	    // =========================
	    // 🔗 Utilisateur sécurisé
	    // =========================
	    if (form.getUtilisateurId() != null) {
	        Utilisateur user = utilisateurRepository.findById(form.getUtilisateurId())
	                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

	        if (user.getAgence() == null 
	                || !user.getAgence().getId().equals(currentAgence.getId())) {
	            throw new SecurityException("Utilisateur d’une autre agence");
	        }

	        entity.setUtilisateur(user);
	    }

	    // =========================
	    // 🔢 Mapping simple
	    // =========================
	    entity.setEncDate(form.getEncDate());

	    entity.setEncMontant(defaultLong(form.getEncMontant()));
	    entity.setEncPerdeb(defaultLong(form.getEncPerdeb()));
	    entity.setEncAndeb(defaultLong(form.getEncAndeb()));
	    entity.setEncPerfin(defaultLong(form.getEncPerfin()));
	    entity.setEncAnfin(defaultLong(form.getEncAnfin()));
	    entity.setEnctotal(defaultLong(form.getEnctotal()));
	    entity.setEncloyer(defaultLong(form.getEncloyer()));

	    entity.setEncvalide(form.isEncvalide());

	    entity.setEncmois(defaultLong(form.getEncmois()));
	    entity.setEncannee(defaultLong(form.getEncannee()));
	    entity.setEncArriere(defaultLong(form.getEncArriere()));
	    entity.setEncPenalite(defaultLong(form.getEncPenalite()));
	    entity.setEncNet(defaultLong(form.getEncNet()));
	    entity.setEncRepport(defaultLong(form.getEncRepport()));
	    entity.setEncMontReppo(defaultLong(form.getEncMontReppo()));

	    entity.setEncStatutRetour(form.getEncStatutRetour());
	    entity.setEncMode(form.getEncMode());
	    entity.setEncDeb(form.getEncDeb());
	    entity.setEncFin(form.getEncFin());
	    entity.setEncNumChq(form.getEncNumChq());

	    entity.setStatut(0); // par défaut en attente

	    // =========================
	    // 🔐 agence auto (important SaaS)
	    // =========================
	    entity.setAgence(currentAgence);

	    return repo.save(entity);
	}

	
	private void checkAgence(Agence agenceEntity, Integer currentAgenceId, String message) {

	    if (agenceEntity == null) {
	        throw new SecurityException(message);
	    }

	    if (!java.util.Objects.equals(agenceEntity.getId(), currentAgenceId)) {
	        throw new SecurityException(message);
	    }
	}
	
	
	@Transactional
	public void validerPaiement(Integer id, Utilisateur user) {

	    Encaisse encaisse = repo.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Encaisse introuvable"));

	    // =========================
	    // 🔐 sécurité agence SaaS
	    // =========================
	    if (encaisse.getAgence() == null 
	            || !encaisse.getAgence().getId().equals(user.getAgence().getId())) {
	        throw new SecurityException("Accès refusé");
	    }

	    Bail bail = encaisse.getBail();

	    if (bail == null) {
	        throw new IllegalArgumentException("Bail introuvable");
	    }

	    //long montantPaye = encaisse.getEncMontant() != null ? encaisse.getEncMontant() : 0L;
	    
	    long montantEncaisse = encaisse.getEncMontant() != null ? encaisse.getEncMontant() : 0L;
	    long penalite = encaisse.getEncPenalite() != null ? encaisse.getEncPenalite() : 0L;

	    long montantPaye = montantEncaisse - penalite;
	    
	    long loyerMensuel = bail.getMontantLoyer() != null ? bail.getMontantLoyer() : 0L;
	    LocalDate dateDebut = bail.getDateFin();

	    loyannService.repartirLoyer(bail, montantPaye, loyerMensuel, dateDebut);

	    encaisse.setEncvalide(true);
	    encaisse.setStatut(2);

	    repo.save(encaisse);
	}
	
	public Optional<Encaisse> findById(Integer id) {
	    return repo.findById(id);
	}
	
}
