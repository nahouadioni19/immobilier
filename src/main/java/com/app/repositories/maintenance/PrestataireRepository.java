package com.app.repositories.maintenance;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entities.maintenance.Prestataire;

public interface PrestataireRepository extends JpaRepository<Prestataire, Integer>{
	
//
	
	@Query("""
		    SELECT p FROM Prestataire p
		    WHERE (
		        :keyword IS NULL OR :keyword = '' OR
		        LOWER(p.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
		        LOWER(p.adresse) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
		        LOWER(p.telephone) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    )
		    AND p.agence.id = :agenceId
		    ORDER BY p.nom ASC
		""")
		Page<Prestataire> search(@Param("keyword") String keyword,
		                      @Param("agenceId") Integer agenceId,
		                      Pageable pageable);	
	//
	
	@Query("""
		    SELECT p FROM Prestataire p
		    WHERE (
		        :keyword IS NULL OR :keyword = '' OR
		        LOWER(p.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
		        LOWER(p.adresse) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
		        LOWER(p.telephone) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    )
		    AND p.agence.id = :agenceId
		    ORDER BY p.nom ASC
		""")
		Page<Prestataire> searchPrestataire(
		        @Param("keyword") String keyword,
		        @Param("agenceId") Integer agenceId,
		        Pageable pageable
		);
	
	//
	Optional<Prestataire> findByIdAndAgenceId(Integer id, Integer agenceId);
	
	//
	Page<Prestataire> findByAgenceId(Integer agenceId, Pageable pageable);


}
