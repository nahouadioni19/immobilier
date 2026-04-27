package com.app.repositories.recouvre;

import com.app.entities.recouvre.Bailleur;
import com.app.entities.recouvre.Locataire;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BailleurRepository extends JpaRepository<Bailleur, Integer>{
	
	@Query("""
		    SELECT b FROM Bailleur b
		    WHERE 
		        LOWER(b.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
		        OR LOWER(b.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
		        OR LOWER(b.cellulaire) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    ORDER BY b.nom ASC
		""")
		Page<Bailleur> search(@Param("keyword") String keyword, Pageable pageable);
	//************************/
	@Query("""
		    SELECT b FROM Bailleur b
		    WHERE 
		        LOWER(b.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
		        OR LOWER(b.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
		        OR LOWER(b.cellulaire) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    ORDER BY b.nom ASC
		""")
		Page<Bailleur> searchBailleur(@Param("keyword") String keyword, Pageable pageable);

}
