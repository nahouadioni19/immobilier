package com.app.repositories.recouvre;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.entities.recouvre.Bailleur;
import com.app.entities.recouvre.Locataire;

@Repository
public interface LocataireRepository extends JpaRepository<Locataire, Integer>{

	@Query("""
		   SELECT l FROM Locataire l
		   WHERE LOWER(l.nom) LIKE LOWER(CONCAT('%', :term, '%'))
		   OR LOWER(l.prenom) LIKE LOWER(CONCAT('%', :term, '%'))
		   ORDER BY l.nom ASC
	 """)
	 Page<Locataire> search(@Param("term") String term, Pageable pageable);
	
	/*recherche du locataire*/
	
	@Query("""
		    SELECT b FROM Locataire b
		    WHERE 
		        LOWER(b.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
		        OR LOWER(b.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
		        OR LOWER(b.telephone) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    ORDER BY b.nom ASC
		""")
		Page<Locataire> searchLocataire(@Param("keyword") String keyword, Pageable pageable);

}
