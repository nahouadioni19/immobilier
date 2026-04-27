package com.app.repositories.recouvre;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.dto.DashboardLoyerDTO;
import com.app.dto.DashboardLoyerMontantDTO;
import com.app.entities.recouvre.Bail;
import com.app.entities.recouvre.Loyann;

@Repository
public interface LoyannRepository extends JpaRepository<Loyann, Integer> {
    List<Loyann> findByBail(Bail bail);
    
 // Récupère le dernier loyer (partiel ou complet) d’un bail
    Loyann findTopByBailOrderByAnneeDescMoisDesc(Bail bail);
    //
    List<Loyann> findByAnnee(int annee);
    //
    
    @Query(value = """
    		SELECT 
		    CONCAT(CONCAT(CONCAT(loc.nom, ' ', loc.prenom),' ( cél.: ',loc.telephone),' )') AS locataire,		
		    MAX(CASE WHEN l.mois = 1 THEN 1 ELSE 0 END) AS jan,
		    MAX(CASE WHEN l.mois = 2 THEN 1 ELSE 0 END) AS fev,
		    MAX(CASE WHEN l.mois = 3 THEN 1 ELSE 0 END) AS mar,
		    MAX(CASE WHEN l.mois = 4 THEN 1 ELSE 0 END) AS avr,
		    MAX(CASE WHEN l.mois = 5 THEN 1 ELSE 0 END) AS mai,
		    MAX(CASE WHEN l.mois = 6 THEN 1 ELSE 0 END) AS jui,
		    MAX(CASE WHEN l.mois = 7 THEN 1 ELSE 0 END) AS jul,
		    MAX(CASE WHEN l.mois = 8 THEN 1 ELSE 0 END) AS aou,
		    MAX(CASE WHEN l.mois = 9 THEN 1 ELSE 0 END) AS sep,
		    MAX(CASE WHEN l.mois = 10 THEN 1 ELSE 0 END) AS oct,
		    MAX(CASE WHEN l.mois = 11 THEN 1 ELSE 0 END) AS nov,
		    MAX(CASE WHEN l.mois = 12 THEN 1 ELSE 0 END) AS dec
		FROM t_loyann l
		JOIN t_bail b ON b.idt = l.bail_id
		JOIN t_locataire loc ON loc.idt = b.locataire_id
		
		WHERE l.annee = :annee
		
		GROUP BY loc.nom, loc.prenom
		ORDER BY loc.nom
  		 """, nativeQuery = true)
		List<Object[]> getDashboard(@Param("annee") int annee);
		
		//
		@Query(value = """
			    SELECT 
			        CONCAT(CONCAT(CONCAT(loc.nom, ' ', loc.prenom),' ( cél.: ',loc.telephone),' )') AS locataire,
			        MAX(CASE WHEN l.mois = 1 THEN 1 ELSE 0 END) AS jan,
			        MAX(CASE WHEN l.mois = 2 THEN 1 ELSE 0 END) AS fev,
			        MAX(CASE WHEN l.mois = 3 THEN 1 ELSE 0 END) AS mar,
			        MAX(CASE WHEN l.mois = 4 THEN 1 ELSE 0 END) AS avr,
			        MAX(CASE WHEN l.mois = 5 THEN 1 ELSE 0 END) AS mai,
			        MAX(CASE WHEN l.mois = 6 THEN 1 ELSE 0 END) AS jui,
			        MAX(CASE WHEN l.mois = 7 THEN 1 ELSE 0 END) AS jul,
			        MAX(CASE WHEN l.mois = 8 THEN 1 ELSE 0 END) AS aou,
			        MAX(CASE WHEN l.mois = 9 THEN 1 ELSE 0 END) AS sep,
			        MAX(CASE WHEN l.mois = 10 THEN 1 ELSE 0 END) AS oct,
			        MAX(CASE WHEN l.mois = 11 THEN 1 ELSE 0 END) AS nov,
			        MAX(CASE WHEN l.mois = 12 THEN 1 ELSE 0 END) AS dec
			    FROM t_loyann l
			    JOIN t_bail b ON b.idt = l.bail_id
			    JOIN t_locataire loc ON loc.idt = b.locataire_id
			    WHERE l.annee = :annee
			    GROUP BY loc.nom, loc.prenom, loc.telephone
			    """,
			    countQuery = """
			        SELECT COUNT(DISTINCT b.idt)
			        FROM t_loyann l
			        JOIN t_bail b ON b.idt = l.bail_id
			        WHERE l.annee = :annee
			    """,
			    nativeQuery = true)
			Page<DashboardLoyerDTO> getDashboard(@Param("annee") int annee, Pageable pageable);
		
		//
		@Query(value = """
			    SELECT 
			        CONCAT(CONCAT(CONCAT(loc.nom, ' ', loc.prenom),' ( cél.: ',loc.telephone),' )') AS locataire,
			        MAX(CASE WHEN l.mois = 1 THEN l.loyer ELSE 0 END) AS jan,
			        MAX(CASE WHEN l.mois = 2 THEN l.loyer ELSE 0 END) AS fev,
			        MAX(CASE WHEN l.mois = 3 THEN l.loyer ELSE 0 END) AS mar,
			        MAX(CASE WHEN l.mois = 4 THEN l.loyer ELSE 0 END) AS avr,
			        MAX(CASE WHEN l.mois = 5 THEN l.loyer ELSE 0 END) AS mai,
			        MAX(CASE WHEN l.mois = 6 THEN l.loyer ELSE 0 END) AS jui,
			        MAX(CASE WHEN l.mois = 7 THEN l.loyer ELSE 0 END) AS jul,
			        MAX(CASE WHEN l.mois = 8 THEN l.loyer ELSE 0 END) AS aou,
			        MAX(CASE WHEN l.mois = 9 THEN l.loyer ELSE 0 END) AS sep,
			        MAX(CASE WHEN l.mois = 10 THEN l.loyer ELSE 0 END) AS oct,
			        MAX(CASE WHEN l.mois = 11 THEN l.loyer ELSE 0 END) AS nov,
			        MAX(CASE WHEN l.mois = 12 THEN l.loyer ELSE 0 END) AS dec
			    FROM t_loyann l
			    JOIN t_bail b ON b.idt = l.bail_id
			    JOIN t_locataire loc ON loc.idt = b.locataire_id
			    WHERE l.annee = :annee
			    GROUP BY loc.nom, loc.prenom, loc.telephone
			    """,
			    countQuery = """
			        SELECT COUNT(DISTINCT b.idt)
			        FROM t_loyann l
			        JOIN t_bail b ON b.idt = l.bail_id
			        WHERE l.annee = :annee
			    """,
			    nativeQuery = true)
			Page<DashboardLoyerMontantDTO> getDashboardMontant(@Param("annee") int annee, Pageable pageable);
		
		//
		Optional<Loyann> findByBailAndMoisAndAnnee(Bail bail, int mois, int annee);
		
		//
		@Query("""
				SELECT l FROM Loyann l
				JOIN FETCH l.bail b
				JOIN FETCH b.locataire
				WHERE b.statut = 'ACTIF'
			    AND l.annee = :annee
				""")
				List<Loyann> findAllByAnneeWithBail(@Param("annee") int annee);
		
		//
		@Query("""
			    SELECT l
			    FROM Loyann l
			    JOIN FETCH l.bail b
			    JOIN FETCH b.locataire
			    WHERE b.statut = 'ACTIF'
			    AND l.annee = :annee
			""")
			List<Loyann> findAllByAnnee(@Param("annee") int annee);

}

