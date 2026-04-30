package com.app.repositories.recouvre;

import com.app.entities.recouvre.Bailleur;
import com.app.entities.recouvre.Locataire;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BailleurRepository extends JpaRepository<Bailleur, Integer>{
	
//
	
	@Query("""
		    SELECT b FROM Bailleur b
		    WHERE (
		        :keyword IS NULL OR :keyword = '' OR
		        LOWER(b.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
		        LOWER(b.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
		        LOWER(b.cellulaire) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    )
		    AND b.agence.id = :agenceId
		    ORDER BY b.nom ASC
		""")
		Page<Bailleur> search(@Param("keyword") String keyword,
		                      @Param("agenceId") Integer agenceId,
		                      Pageable pageable);	
	//
	
	@Query("""
		    SELECT b FROM Bailleur b
		    WHERE (
		        :keyword IS NULL OR :keyword = '' OR
		        LOWER(b.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
		        LOWER(b.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
		        LOWER(b.cellulaire) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    )
		    AND b.agence.id = :agenceId
		    ORDER BY b.nom ASC
		""")
		Page<Bailleur> searchBailleur(
		        @Param("keyword") String keyword,
		        @Param("agenceId") Integer agenceId,
		        Pageable pageable
		);
	
	//
	Optional<Bailleur> findByIdAndAgenceId(Integer id, Integer agenceId);
	
	//
	Page<Bailleur> findByAgenceId(Integer agenceId, Pageable pageable);

}
