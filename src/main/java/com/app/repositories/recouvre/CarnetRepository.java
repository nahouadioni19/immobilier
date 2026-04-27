package com.app.repositories.recouvre;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.entities.administration.Utilisateur;
import com.app.entities.recouvre.Carnet;

@Repository
public interface CarnetRepository extends JpaRepository<Carnet, Integer>{

	@Query("SELECT c FROM Carnet c WHERE c.carActive = false")
	Optional<Carnet> findByActive();
	
	@Query("SELECT c FROM Carnet c WHERE c.carActive = false")
	List<Carnet> findAllInactive();
	
	@Query("SELECT c FROM Carnet c " +
	           "WHERE ( :carNumDeb BETWEEN c.carNumDeb AND c.carNumFin ) " +
	           "   OR ( :carNumFin BETWEEN c.carNumDeb AND c.carNumFin ) " +
	           "   OR ( c.carNumDeb BETWEEN :carNumDeb AND :carNumFin ) " +
	           "   OR ( c.carNumFin BETWEEN :carNumDeb AND :carNumFin )")
	    List<Carnet> findOverlapping(@Param("carNumDeb") long carNumDeb,
	                                 @Param("carNumFin") long carNumFin);
	
	boolean existsByCarNumDebLessThanEqualAndCarNumFinGreaterThanEqual(long fin, long deb);
	
	// Liste des carnets d'un utilisateur
    List<Carnet> findByUtilisateur(Utilisateur utilisateur);

    // Optionnel : filtrage par utilisateur et active
    List<Carnet> findByUtilisateurAndCarActiveTrue(Utilisateur utilisateur);
    
    Page<Carnet> findByUtilisateur(Utilisateur utilisateur, Pageable pageable);
    	
}
