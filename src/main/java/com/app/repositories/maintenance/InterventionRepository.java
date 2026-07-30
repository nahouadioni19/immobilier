package com.app.repositories.maintenance;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entities.maintenance.Intervention;
import com.app.entities.maintenance.Prestataire;

public interface InterventionRepository extends JpaRepository<Intervention, Integer>{
	
	Optional<Intervention> findByIdAndAgenceId(Integer id, Integer agenceId);
	
	//
	Page<Intervention> findByAgenceId(Integer agenceId, Pageable pageable);
	
	//

	@Query("""
	        SELECT DISTINCT i
	        FROM Intervention i
	            LEFT JOIN i.appartement a
	            LEFT JOIN i.locataire l
	            LEFT JOIN i.prestataire p
	            LEFT JOIN i.typeIntervention t
	            LEFT JOIN a.immeuble im
	        WHERE i.agence.id = :agenceId
	          AND (
	                LOWER(i.objet) LIKE LOWER(CONCAT('%', :keyword, '%'))
	             OR LOWER(COALESCE(i.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
	             OR LOWER(COALESCE(i.observation, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
	             OR LOWER(COALESCE(a.numAppart, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
	             OR LOWER(COALESCE(a.libelle, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
	             OR LOWER(COALESCE(im.nomImmeuble, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
	             OR LOWER(COALESCE(l.nom, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
	             OR LOWER(COALESCE(p.nom, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
	             OR LOWER(COALESCE(t.libelle, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
	          )
	        ORDER BY i.dateDeclaration DESC
	        """)
	    Page<Intervention> search(
	            @Param("keyword") String keyword,
	            @Param("agenceId") Integer agenceId,
	            Pageable pageable);

}
