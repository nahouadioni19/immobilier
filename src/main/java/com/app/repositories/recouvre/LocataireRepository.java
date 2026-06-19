package com.app.repositories.recouvre;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.entities.recouvre.Locataire;

@Repository
public interface LocataireRepository extends JpaRepository<Locataire, Integer>{
	
//
	
	@Query("""
		    SELECT l FROM Locataire l
		    WHERE (
		        :keyword IS NULL OR :keyword = '' OR
		        LOWER(l.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
		        LOWER(l.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
		        LOWER(l.telephone) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    )
		    AND l.agence.id = :agenceId
		    ORDER BY l.nom ASC
		""")
		Page<Locataire> search(@Param("keyword") String keyword,
		                      @Param("agenceId") Integer agenceId,
		                      Pageable pageable);	
	//
	
	@Query("""
		    SELECT l FROM Locataire l
		    WHERE (
		        :keyword IS NULL OR :keyword = '' OR
		        LOWER(l.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
		        LOWER(l.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
		        LOWER(l.telephone) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    )
		    AND l.agence.id = :agenceId
		    ORDER BY l.nom ASC
		""")
		Page<Locataire> searchLocataire(
		        @Param("keyword") String keyword,
		        @Param("agenceId") Integer agenceId,
		        Pageable pageable
		);
	
	//
	Optional<Locataire> findByIdAndAgenceId(Integer id, Integer agenceId);
	
	//
	Page<Locataire> findByAgenceId(Integer agenceId, Pageable pageable);
	
	Long countByAgenceId(Integer agenceId);

}
