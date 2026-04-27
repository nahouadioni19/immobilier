package com.app.repositories.recouvre;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.entities.recouvre.Appartement;
import com.app.entities.recouvre.Bail;
import com.app.enums.StatutAppartement;

@Repository
public interface AppartementRepository extends JpaRepository<Appartement, Integer>{
	
	 /**
     * Appartements libres (sans bail en cours)
     */
	@Query("""
		SELECT a FROM Appartement a
		WHERE a.statut = com.app.enums.StatutAppartement.LIBRE
	""")
	List<Appartement> findAllAvailable();
	
    //
  /*  @Query("""
    		   SELECT a FROM Appartement a
    		   JOIN  FETCH a.immeuble i
    		   WHERE LOWER(a.libelle) LIKE LOWER(CONCAT('%', :term, '%'))
    		   OR LOWER(i.nomImmeuble) LIKE LOWER(CONCAT('%', :term, '%'))
    		   ORDER BY a.libelle ASC
    		""")
    		Page<Appartement> search(@Param("term") String term, Pageable pageable);*/
	
	
	@EntityGraph(attributePaths = {"immeuble"})
	@Query("""
	   SELECT a FROM Appartement a
	   WHERE 
	       (
	           LOWER(a.libelle) LIKE LOWER(CONCAT('%', :term, '%'))
	           OR LOWER(a.immeuble.nomImmeuble) LIKE LOWER(CONCAT('%', :term, '%'))
	       )
	   AND a.statut = com.app.enums.StatutAppartement.LIBRE
	   ORDER BY a.libelle ASC
	""")
	Page<Appartement> search(@Param("term") String term, Pageable pageable);
	
	//
	@EntityGraph(attributePaths = {"immeuble"})
	@Query("""
	    SELECT a FROM Appartement a
	    WHERE 
	        (
	            a.statut = com.app.enums.StatutAppartement.LIBRE
	            OR a.id = :currentId
	        )
	        AND (
	            LOWER(a.libelle) LIKE LOWER(CONCAT('%', :term, '%'))
	            OR LOWER(a.immeuble.nomImmeuble) LIKE LOWER(CONCAT('%', :term, '%'))
	        )
	    ORDER BY a.libelle ASC
	""")
	Page<Appartement> searchForBail(@Param("term") String term,@Param("currentId") Integer currentId,Pageable pageable);
	
	//
	//@EntityGraph(attributePaths = {"immeuble"})
	@EntityGraph(attributePaths = {"immeuble","immeuble.bailleur"})
	@Query("""
	   SELECT a FROM Appartement a
	   WHERE
	       (
	           LOWER(a.libelle) LIKE LOWER(CONCAT('%', :term, '%'))
	           OR LOWER(a.immeuble.nomImmeuble) LIKE LOWER(CONCAT('%', :term, '%'))
	           OR LOWER(a.immeuble.bailleur.nom) LIKE LOWER(CONCAT('%', :term, '%'))
	           OR LOWER(a.immeuble.bailleur.prenom) LIKE LOWER(CONCAT('%', :term, '%'))
	       )
	   AND
	       (
	           a.statut = com.app.enums.StatutAppartement.LIBRE
	           OR (:currentId IS NOT NULL AND a.id = :currentId)
	       )
	   ORDER BY a.libelle ASC
	""")
	Page<Appartement> searchForAppartement(@Param("term") String term,@Param("currentId") Integer currentId,Pageable pageable);
	
    /**
     * Appartements libres + appartement courant (pour édition)
     */
	
    @Query("""
    	    SELECT b FROM Bail b
    	    JOIN FETCH b.appartement a
    	    JOIN FETCH b.locataire l
    	    WHERE b.id = :id
    	""")
    	Optional<Bail> findByIdWithRelations(@Param("id") Integer id);
    
    //
    
    @Query("""
        SELECT a FROM Appartement a
    	WHERE a.statut = com.app.enums.StatutAppartement.LIBRE
        OR a.id = :currentId
        ORDER BY a.libelle ASC
    """)
    List<Appartement> findAllAvailableOrCurrent(@Param("currentId") Integer currentId);
    
    /**/
    List<Appartement> findByStatut(StatutAppartement statut);

}
